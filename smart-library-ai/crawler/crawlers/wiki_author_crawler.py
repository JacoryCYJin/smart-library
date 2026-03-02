"""
维基百科/百度百科作者信息爬虫

用于从维基百科和百度百科爬取作者详细信息，作为豆瓣的替代方案

@author JacoryCyJin
@date 2025/03/02
"""
import requests
from bs4 import BeautifulSoup
import time
import logging
import random
from urllib.parse import quote
from config import Config
from utils import DatabaseHelper, MinioHelper

logger = logging.getLogger(__name__)


class WikiAuthorCrawler:
    """维基百科/百度百科作者信息爬虫"""
    
    def __init__(self):
        self.db = DatabaseHelper()
        self.minio = MinioHelper()
        self.session = requests.Session()
        
        # 随机 User-Agent 列表
        self.user_agents = [
            'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
            'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15',
            'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0'
        ]
        
        # 使用随机 User-Agent
        self.session.headers.update({
            'User-Agent': random.choice(self.user_agents),
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
            'Connection': 'keep-alive'
        })
    
    def crawl_author_detail(self, author_id, author_name):
        """
        爬取作者详细信息（优先百度百科，备选维基百科）
        
        Args:
            author_id: 作者ID
            author_name: 作者姓名
        
        Returns:
            'success': 成功
            'no_result': 未找到结果
            'failed': 失败
        """
        try:
            # 优先尝试百度百科（中文作者信息更全）
            logger.info(f"  尝试从百度百科获取: {author_name}")
            result = self._crawl_from_baidu(author_id, author_name)
            
            if result == 'success':
                return 'success'
            
            # 百度百科失败，尝试维基百科
            logger.info(f"  百度百科未找到，尝试维基百科: {author_name}")
            result = self._crawl_from_wikipedia(author_id, author_name)
            
            return result
            
        except Exception as e:
            logger.error(f"  爬取作者详情失败 {author_name}: {e}")
            return 'failed'
    
    def _crawl_from_wikipedia(self, author_id, author_name):
        """
        从维基百科爬取作者信息
        
        Args:
            author_id: 作者ID
            author_name: 作者姓名
        
        Returns:
            'success': 成功
            'no_result': 未找到结果
            'failed': 失败
        """
        try:
            # 使用维基百科 API 搜索
            search_url = f"https://zh.wikipedia.org/w/api.php"
            search_params = {
                'action': 'opensearch',
                'search': author_name,
                'limit': 1,
                'namespace': 0,
                'format': 'json'
            }
            
            response = self.session.get(search_url, params=search_params, timeout=10)
            response.raise_for_status()
            
            search_results = response.json()
            
            # 检查是否有结果
            if not search_results[1] or len(search_results[1]) == 0:
                logger.debug(f"  维基百科未找到: {author_name}")
                return 'no_result'
            
            # 获取第一个结果的标题和 URL
            page_title = search_results[1][0]
            page_url = search_results[3][0]
            
            logger.debug(f"  找到维基百科页面: {page_title}")
            logger.debug(f"  URL: {page_url}")
            
            # 获取页面内容
            response = self.session.get(page_url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 解析作者信息
            author_data = self._parse_wikipedia_page(soup, author_id, author_name, page_url)
            
            if not author_data:
                logger.debug(f"  解析维基百科页面失败: {author_name}")
                return 'no_result'
            
            # 上传作者照片（可选）
            photo_url = self._get_wikipedia_photo(soup)
            if photo_url:
                logger.debug(f"  找到照片 URL: {photo_url}")
                try:
                    photo_file = self.minio.upload_from_url(
                        photo_url,
                        bucket_name=Config.MINIO_BUCKET_AVATARS
                    )
                    if photo_file:
                        author_data['photo_url'] = self.minio.get_file_url(
                            photo_file,
                            bucket_name=Config.MINIO_BUCKET_AVATARS
                        )
                        logger.info(f"  ✓ 照片上传成功")
                    else:
                        author_data['photo_url'] = None
                except Exception as e:
                    logger.debug(f"  照片上传失败: {e}")
                    author_data['photo_url'] = None
            else:
                logger.info(f"  ⚠ 未找到照片")
            
            # 更新数据库
            self.db.update_author_detail(author_id, author_data)
            
            logger.info(f"  ✓ 从维基百科获取成功: {author_name}")
            time.sleep(random.randint(2, 5))  # 随机延迟
            return 'success'
            
        except Exception as e:
            logger.debug(f"  维基百科爬取失败: {e}")
            return 'no_result'
    
    def _crawl_from_baidu(self, author_id, author_name):
        """
        从百度百科爬取作者信息
        
        Args:
            author_id: 作者ID
            author_name: 作者姓名
        
        Returns:
            'success': 成功
            'no_result': 未找到结果
            'failed': 失败
        """
        try:
            # 百度百科搜索 URL
            search_url = f"https://baike.baidu.com/item/{quote(author_name)}"
            
            logger.debug(f"  访问百度百科: {search_url}")
            
            response = self.session.get(search_url, timeout=10)
            
            # 检查是否找到页面
            if response.status_code == 404:
                logger.debug(f"  百度百科未找到: {author_name}")
                return 'no_result'
            
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 解析作者信息
            author_data = self._parse_baidu_page(soup, author_id, author_name, response.url)
            
            if not author_data:
                logger.debug(f"  解析百度百科页面失败: {author_name}")
                return 'no_result'
            
            # 上传作者照片（如果有）
            photo_url = author_data.get('photo_url')  # 从 JSON 或 HTML 提取的照片 URL
            if photo_url and photo_url.startswith('http'):
                logger.debug(f"  找到照片 URL: {photo_url}")
                try:
                    photo_file = self.minio.upload_from_url(
                        photo_url,
                        bucket_name=Config.MINIO_BUCKET_AVATARS
                    )
                    if photo_file:
                        author_data['photo_url'] = self.minio.get_file_url(
                            photo_file,
                            bucket_name=Config.MINIO_BUCKET_AVATARS
                        )
                        logger.info(f"  ✓ 照片上传成功")
                    else:
                        # 上传失败，清除照片 URL
                        author_data['photo_url'] = None
                except Exception as e:
                    logger.debug(f"  照片上传失败: {e}")
                    author_data['photo_url'] = None
            else:
                logger.info(f"  ⚠ 未找到照片")
            
            # 更新数据库
            self.db.update_author_detail(author_id, author_data)
            
            logger.info(f"  ✓ 从百度百科获取成功: {author_name}")
            time.sleep(random.randint(2, 5))  # 随机延迟
            return 'success'
            
        except Exception as e:
            logger.debug(f"  百度百科爬取失败: {e}")
            return 'no_result'
    
    def _parse_wikipedia_page(self, soup, author_id, author_name, page_url):
        """
        解析维基百科页面
        
        Args:
            soup: BeautifulSoup 对象
            author_id: 作者ID
            author_name: 作者姓名
            page_url: 页面 URL
        
        Returns:
            dict: 作者数据
        """
        try:
            author_data = {
                'author_id': author_id,
                'name': author_name,
                'original_name': None,
                'country': None,
                'photo_url': None,
                'description': None,
                'source_origin': 3,  # 3-维基百科
                'source_url': page_url
            }
            
            # 提取简介（第一段）
            content_div = soup.select_one('#mw-content-text .mw-parser-output')
            if content_div:
                # 找到第一个非空段落
                paragraphs = content_div.find_all('p', recursive=False)
                for p in paragraphs:
                    text = p.get_text(strip=True)
                    if text and len(text) > 20:
                        author_data['description'] = text
                        logger.debug(f"  提取到简介: {text[:50]}...")
                        break
            
            # 提取信息框数据
            infobox = soup.select_one('.infobox')
            if infobox:
                rows = infobox.find_all('tr')
                for row in rows:
                    th = row.find('th')
                    td = row.find('td')
                    
                    if th and td:
                        label = th.get_text(strip=True) or ''  # 确保不是 None
                        value = td.get_text(strip=True) or ''
                        
                        # 提取原名
                        if label and ('原名' in label or '本名' in label):
                            author_data['original_name'] = value
                            logger.debug(f"  提取到原名: {value}")
                        
                        # 提取国籍
                        if label and any(keyword in label for keyword in ['国籍', '國籍', '国家']):
                            # 统一国家名称
                            country = self._normalize_country_name(value)
                            author_data['country'] = country
                            logger.debug(f"  提取到国籍: {country}")
            
            return author_data
            
        except Exception as e:
            logger.error(f"解析维基百科页面失败: {e}")
            return None
    
    def _extract_baidu_json_data(self, soup):
        """
        从百度百科页面提取 JSON 数据
        
        Args:
            soup: BeautifulSoup 对象
        
        Returns:
            dict: 解析后的 JSON 数据，失败返回 None
        """
        try:
            # 查找包含 window.PAGE_DATA 的 script 标签
            scripts = soup.find_all('script')
            for script in scripts:
                if script.string and 'window.PAGE_DATA' in script.string:
                    # 提取 JSON 数据
                    script_text = script.string
                    # 找到 window.PAGE_DATA= 后面的 JSON
                    start_idx = script_text.find('window.PAGE_DATA=')
                    if start_idx == -1:
                        continue
                    
                    # 跳过 'window.PAGE_DATA='
                    start_idx += len('window.PAGE_DATA=')
                    
                    # 找到 JSON 结束位置（通常是分号或换行）
                    # 使用简单的括号匹配来找到完整的 JSON
                    json_str = script_text[start_idx:].strip()
                    
                    # 移除末尾的分号和其他内容
                    if ';' in json_str:
                        # 找到第一个不在字符串中的分号
                        brace_count = 0
                        in_string = False
                        escape = False
                        for i, char in enumerate(json_str):
                            if escape:
                                escape = False
                                continue
                            if char == '\\':
                                escape = True
                                continue
                            if char == '"' and not escape:
                                in_string = not in_string
                            if not in_string:
                                if char == '{':
                                    brace_count += 1
                                elif char == '}':
                                    brace_count -= 1
                                    if brace_count == 0:
                                        json_str = json_str[:i+1]
                                        break
                    
                    # 解析 JSON
                    import json
                    data = json.loads(json_str)
                    return data
            
            return None
        except Exception as e:
            logger.debug(f"提取百度百科 JSON 数据失败: {e}")
            return None
    
    def _parse_baidu_page(self, soup, author_id, author_name, page_url):
        """
        解析百度百科页面
        
        Args:
            soup: BeautifulSoup 对象
            author_id: 作者ID
            author_name: 作者姓名
            page_url: 页面 URL
        
        Returns:
            dict: 作者数据
        """
        try:
            author_data = {
                'author_id': author_id,
                'name': author_name,
                'original_name': None,
                'country': None,
                'photo_url': None,
                'description': None,
                'source_origin': 4,  # 4-百度百科
                'source_url': page_url
            }
            
            # 尝试从 JSON 数据中提取
            logger.debug(f"  [百度百科] 尝试从 JSON 数据提取...")
            json_data = self._extract_baidu_json_data(soup)
            
            if json_data:
                logger.debug(f"  ✓ 成功提取 JSON 数据")
                
                # 提取简介（优先从 HTML 获取完整版，JSON 的 description 是截断的）
                # 先尝试从 HTML 提取完整简介
                summary_selectors = [
                    '.lemma-summary',
                    '.J-summary',
                    '.summary-content'
                ]
                
                for selector in summary_selectors:
                    summary = soup.select_one(selector)
                    if summary:
                        text = summary.get_text(strip=True)
                        if text and len(text) > 20:
                            author_data['description'] = text
                            logger.debug(f"  ✓ 提取到完整简介 ({selector}): {text[:50]}...")
                            break
                
                # 如果 HTML 没有提取到，才使用 JSON 的截断版本
                if not author_data['description'] and 'description' in json_data:
                    author_data['description'] = json_data['description']
                    logger.debug(f"  ✓ 提取到简介（JSON）: {author_data['description'][:50]}...")
                
                # 提取基本信息卡片
                if 'card' in json_data:
                    card = json_data['card']
                    
                    # 检查 card 是否为 None
                    if card is not None:
                        # 合并 left 和 right 数组
                        all_items = []
                        if 'left' in card:
                            all_items.extend(card['left'])
                        if 'right' in card:
                            all_items.extend(card['right'])
                    
                        logger.debug(f"  找到 {len(all_items)} 个信息项")
                    
                        for item in all_items:
                            key = item.get('key', '')
                            title = item.get('title', '') or ''
                            data_list = item.get('data', [])
                        
                            if not data_list:
                                continue
                        
                            # 提取值
                            value_parts = []
                            for data_item in data_list:
                                if 'text' in data_item:
                                    text_parts = data_item['text']
                                    for text_part in text_parts:
                                        if isinstance(text_part, dict) and 'text' in text_part:
                                            value_parts.append(text_part['text'])
                                        elif isinstance(text_part, str):
                                            value_parts.append(text_part)
                        
                            value = ''.join(value_parts)
                        
                            if value:
                                # 匹配外文名/原名（只保留包含英文字母的）
                                if key == 'foreignName' or (title and any(keyword in title for keyword in ['外文名', '原名', '本名', '别名', '英文名'])):
                                    if value != author_name:
                                        has_english = any(c.isalpha() and ord(c) < 128 for c in value)
                                        if has_english:
                                            if not author_data['original_name']:
                                                author_data['original_name'] = value
                                                logger.info(f"  ✓ 提取到原名: {value}")
                                            else:
                                                has_english_old = any(c.isalpha() and ord(c) < 128 for c in author_data['original_name'])
                                                if not has_english_old:
                                                    author_data['original_name'] = value
                                                    logger.info(f"  ✓ 提取到原名: {value}")
                            
                                # 匹配国籍
                                if key == 'nationality' or (title and any(keyword in title for keyword in ['国籍', '國籍', '国家'])):
                                    country = self._normalize_country_name(value)
                                    author_data['country'] = country
                                    logger.info(f"  ✓ 提取到国籍: {country}")
                
                # 提取照片
                if 'albums' in json_data and len(json_data['albums']) > 0:
                    first_album = json_data['albums'][0]
                    if 'coverPic' in first_album:
                        cover_pic = first_album['coverPic']
                        if 'url' in cover_pic:
                            photo_url = cover_pic['url']
                            logger.debug(f"  ✓ 找到照片 URL: {photo_url}")
                            # 照片 URL 已经是完整的，不需要处理
                            if 'icon' not in photo_url.lower() and 'default' not in photo_url.lower():
                                author_data['photo_url'] = photo_url
            else:
                logger.warning(f"  ⚠ 未能提取 JSON 数据，尝试 HTML 解析...")
                # 回退到 HTML 解析（保留原有逻辑作为备选）
                return self._parse_baidu_page_html(soup, author_id, author_name, page_url)
            
            # 检查是否提取到任何有效信息
            has_data = any([
                author_data['description'],
                author_data['original_name'],
                author_data['country']
            ])
            
            if not has_data:
                logger.warning(f"  ⚠ 百度百科页面未提取到任何有效信息")
                return None
            
            return author_data
            
        except Exception as e:
            logger.error(f"解析百度百科页面失败: {e}")
            import traceback
            logger.error(traceback.format_exc())
            return None
    
    def _parse_baidu_page_html(self, soup, author_id, author_name, page_url):
        """
        从 HTML 解析百度百科页面（备选方案）
        
        Args:
            soup: BeautifulSoup 对象
            author_id: 作者ID
            author_name: 作者姓名
            page_url: 页面 URL
        
        Returns:
            dict: 作者数据
        """
        try:
            author_data = {
                'author_id': author_id,
                'name': author_name,
                'original_name': None,
                'country': None,
                'photo_url': None,
                'description': None,
                'source_origin': 4,  # 4-百度百科
                'source_url': page_url
            }
            
            # 提取简介（摘要）
            summary_selectors = [
                '.lemma-summary',
                '.J-summary',
                '.summary-content',
                '.lemma_desc',
                '.para'
            ]
            
            for selector in summary_selectors:
                summary = soup.select_one(selector)
                if summary:
                    text = summary.get_text(strip=True)
                    if text and len(text) > 20:
                        author_data['description'] = text
                        break
            
            # 提取基本信息
            basic_info_selectors = [
                '.basic-info',
                '.basicInfo-block',
                '.lemmaWgt-baseInfo'
            ]
            
            basic_info = None
            for selector in basic_info_selectors:
                basic_info = soup.select_one(selector)
                if basic_info:
                    break
            
            if basic_info:
                # 提取信息项
                items = basic_info.find_all('dt')
                if not items:
                    items = basic_info.find_all(class_='name')
                
                if items:
                    for dt in items:
                        label = dt.get_text(strip=True)
                        dd = dt.find_next_sibling('dd') or dt.find_next_sibling(class_='value') or dt.find_next_sibling()
                        
                        if dd:
                            value = dd.get_text(strip=True)
                            
                            # 提取原名/外文名
                            if label and any(keyword in label for keyword in ['外文名', '原名', '本名', '别名', '英文名']):
                                author_data['original_name'] = value
                            
                            # 提取国籍
                            if label and any(keyword in label for keyword in ['国籍', '國籍', '国家']):
                                author_data['country'] = self._normalize_country_name(value)
            
            return author_data if any([author_data['description'], author_data['original_name'], author_data['country']]) else None
            
        except Exception as e:
            logger.error(f"HTML 解析百度百科页面失败: {e}")
            return None
    
    def _get_wikipedia_photo(self, soup):
        """获取维基百科作者照片 URL"""
        try:
            # 查找信息框中的图片
            infobox = soup.select_one('.infobox')
            if infobox:
                img = infobox.find('img')
                if img and img.get('src'):
                    photo_url = img['src']
                    # 补全 URL
                    if photo_url.startswith('//'):
                        photo_url = 'https:' + photo_url
                    logger.debug(f"  找到维基百科照片: {photo_url}")
                    return photo_url
        except Exception as e:
            logger.debug(f"  获取维基百科照片失败: {e}")
        
        return None
    
    def _normalize_country_name(self, country):
        """
        统一国家名称
        
        Args:
            country: 原始国家名称
        
        Returns:
            str: 标准化后的国家名称
        """
        if not country:
            return country
        
        # 国家名称映射表
        country_mapping = {
            '中华人民共和国': '中国',
            '中華人民共和國': '中国',
            'People\'s Republic of China': '中国',
            'PRC': '中国',
            '美利坚合众国': '美国',
            'United States of America': '美国',
            'USA': '美国',
            '大不列颠及北爱尔兰联合王国': '英国',
            'United Kingdom': '英国',
            'UK': '英国',
            '俄罗斯联邦': '俄罗斯',
            'Russian Federation': '俄罗斯',
            '大韩民国': '韩国',
            'Republic of Korea': '韩国',
            'South Korea': '韩国',
        }
        
        # 查找映射
        for key, value in country_mapping.items():
            if key in country:
                return value
        
        return country
