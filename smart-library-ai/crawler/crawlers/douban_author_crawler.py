"""
豆瓣作者详情爬虫

@author JacoryCyJin
@date 2025/02/28
"""
import requests
from bs4 import BeautifulSoup
import time
import logging
import random
from urllib.parse import unquote, parse_qs, urlparse, quote
from config import Config
from utils import DatabaseHelper, MinioHelper

logger = logging.getLogger(__name__)


class DoubanAuthorCrawler:
    """豆瓣作者详情爬虫"""
    
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
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1',
            'Sec-Fetch-Dest': 'document',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-Site': 'none',
            'Sec-Fetch-User': '?1'
        })
        
        # 初始化 Session，访问一次主页获取 Cookies
        try:
            self.session.get('https://book.douban.com/', timeout=10)
            logger.info("已初始化 Session 并获取 Cookies")
        except Exception as e:
            logger.warning(f"初始化 Session 失败: {e}")
    
    def search_author_url(self, author_name):
        """
        搜索作者的豆瓣页面 URL
        
        Args:
            author_name: 作者姓名
        
        Returns:
            作者页面 URL，失败返回 None
        """
        try:
            # 方案：使用豆瓣通用搜索（不指定分类）
            search_url = "https://www.douban.com/search"
            params = {
                'q': author_name
            }
            
            # 添加 Referer
            headers = {
                'Referer': 'https://book.douban.com/'
            }
            
            # 随机延迟
            time.sleep(random.uniform(1, 3))
            
            logger.info(f"  搜索 URL: {search_url}?q={author_name}")
            
            response = self.session.get(search_url, params=params, headers=headers, timeout=15)
            
            # 检查是否触发验证码
            if 'sec.douban.com' in response.url or response.status_code == 403:
                logger.warning(f"  触发豆瓣搜索反爬虫验证: {author_name}")
                return None
                
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 查找所有链接
            all_links = soup.find_all('a', href=True)
            target_links = []
            
            for a in all_links:
                raw_href = a.get('href', '')
                if not raw_href.startswith('http'):
                    continue
                
                # 解码 URL，以便匹配被编码的字符
                decoded_href = unquote(raw_href)
                
                # 匹配 personage, celebrity, 或 book.douban.com/author/
                if '/personage/' in decoded_href or '/celebrity/' in decoded_href or 'book.douban.com/author/' in decoded_href:
                    # 如果是重定向链接 (link2)，尝试提取真实 URL
                    if 'www.douban.com/link2/' in raw_href:
                        try:
                            parsed = urlparse(raw_href)
                            query = parse_qs(parsed.query)
                            if 'url' in query:
                                real_url = query['url'][0]
                                a['href'] = real_url # 更新 href 为真实 URL
                                target_links.append(a)
                        except:
                            pass
                    else:
                        target_links.append(a)
            
            logger.info(f"  找到 {len(target_links)} 个作者链接")
            
            if target_links:
                # 优先选择 book.douban.com/author/ 链接，因为这是我们主要关注的
                for link in target_links:
                    if 'book.douban.com/author/' in link['href']:
                        author_url = link['href']
                        author_title = link.get_text(strip=True)
                        logger.info(f"  ✓ 找到书籍作者页面: {author_title} -> {author_url}")
                        return author_url
                
                # 如果没有书籍作者链接，返回第一个匹配的（可能是影人）
                author_url = target_links[0]['href']
                author_title = target_links[0].get_text(strip=True)
                logger.info(f"  ✓ 找到作者页面: {author_title} -> {author_url}")
                return author_url
            
            logger.warning(f"  未找到作者页面: {author_name}")
            return None
            
        except Exception as e:
            logger.error(f"搜索作者失败 {author_name}: {e}")
            import traceback
            traceback.print_exc()
            return None
    
    def crawl_author_detail(self, author_id, author_name, author_url=None):
        """
        爬取作者详细信息
        
        Args:
            author_id: 作者ID
            author_name: 作者姓名
            author_url: 作者页面URL（/author/xxx 或 /personage/xxx）
        
        Returns:
            'success': 成功
            'no_url': 没有 URL
            'failed': 失败
        """
        try:
            # 如果没有提供 URL，先搜索
            if not author_url:
                logger.debug(f"  未提供作者 URL，开始搜索...")
                author_url = self.search_author_url(author_name)
                if not author_url:
                    logger.info(f"  ⚠ 无法自动找到作者页面，标记为待手动补充: {author_name}")
                    return 'no_url'
            
            # 检查 URL 类型：如果是搜索链接，跳过
            if '/search/' in author_url:
                logger.info(f"  ⚠ 作者链接是搜索页面，跳过: {author_name}")
                return 'no_url'
            
            # 补全 URL（如果是相对路径）
            if author_url.startswith('/'):
                author_url = f"https://book.douban.com{author_url}"
            
            logger.debug(f"  访问作者链接: {author_url}")
            
            # 设置 Referer 模拟从搜索页跳转
            headers = {
                'Referer': f'https://www.douban.com/search?q={quote(author_name)}'
            }
            
            # 访问作者链接
            # 尝试先禁止重定向，看看能否直接获取信息（避免跳转到 www.douban.com 被封）
            try:
                response = self.session.get(author_url, headers=headers, timeout=10, allow_redirects=False)
                
                if response.status_code == 200:
                    logger.debug("  成功访问原始链接，未重定向")
                    final_url = response.url
                elif response.status_code in (301, 302, 303, 307):
                    redirect_url = response.headers.get('Location')
                    logger.debug(f"  页面重定向到: {redirect_url}")
                    # 如果重定向到 www.douban.com/personage/，可能会被封
                    # 但我们没有选择，只能跟随
                    if redirect_url.startswith('/'):
                        redirect_url = f"https://book.douban.com{redirect_url}"
                    
                    response = self.session.get(redirect_url, headers=headers, timeout=10)
                    final_url = response.url
                else:
                    # 其他状态码交给后面统一处理
                    final_url = response.url

                # 检查是否被重定向到验证页面或 403
                if 'sec.douban.com' in response.url or response.status_code == 403:
                    logger.warning(f"  触发豆瓣反爬虫验证 (详情页): {author_name}")
                    return 'blocked'
                
                response.raise_for_status()
                
            except requests.exceptions.RequestException as e:
                logger.error(f"请求失败: {e}")
                return 'failed'
            
            # 获取最终的 URL（重定向后的）
            final_url = response.url
            logger.debug(f"  重定向到: {final_url}")
            
            # 检查是否是有效的作者页面
            if '/personage/' not in final_url and '/celebrity/' not in final_url:
                logger.warning(f"  不是有效的作者页面: {final_url}")
                return 'failed'
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 解析作者信息
            author_data = self._parse_author_detail(soup, author_id, author_name, final_url)
            if not author_data:
                logger.warning(f"  解析作者信息失败: {author_name}")
                return 'failed'
            
            logger.debug(f"  解析成功，准备上传照片...")
            
            # 上传作者照片（可选，失败不影响其他信息保存）
            photo_url = self._get_photo_url(soup)
            if photo_url:
                logger.debug(f"  找到照片 URL: {photo_url}")
                try:
                    # 上传到 library-avatars bucket
                    photo_file = self.minio.upload_from_url(
                        photo_url, 
                        bucket_name=Config.MINIO_BUCKET_AVATARS
                    )
                    if photo_file:
                        author_data['photo_url'] = self.minio.get_file_url(
                            photo_file,
                            bucket_name=Config.MINIO_BUCKET_AVATARS
                        )
                        logger.debug(f"  照片上传成功: {photo_file}")
                    else:
                        logger.debug(f"  照片上传失败，继续保存其他信息")
                except Exception as e:
                    logger.debug(f"  照片上传失败: {e}，继续保存其他信息")
            else:
                logger.debug(f"  未找到作者照片，继续保存其他信息")
            
            # 更新数据库
            logger.debug(f"  准备更新数据库...")
            logger.debug(f"  作者数据: {author_data}")
            self.db.update_author_detail(author_id, author_data)
            
            logger.info(f"  ✓ 作者信息爬取成功: {author_name}")
            time.sleep(Config.REQUEST_DELAY + 2)  # 额外增加 2 秒延迟
            return 'success'
            
        except Exception as e:
            logger.error(f"  爬取作者详情失败 {author_name}: {e}")
            import traceback
            traceback.print_exc()
            return 'failed'
    
    def _parse_author_detail(self, soup, author_id, author_name, author_url):
        """
        解析作者详情页（豆瓣 personage 页面）
        
        Args:
            soup: BeautifulSoup 对象
            author_id: 作者ID
            author_name: 作者姓名
            author_url: 作者页面URL
        
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
                'source_origin': 1,  # 1-豆瓣
                'source_url': author_url
            }
            
            # 1. 提取简介
            # 尝试多种选择器：#intro .bd (personage), .intro (book author), #intro
            description = None
            intro_selectors = ['#intro .bd', '.intro', '#intro', 'div.bd']
            for selector in intro_selectors:
                intro_elem = soup.select_one(selector)
                if intro_elem:
                    # 如果有 .all (展开全部)，优先使用
                    all_elem = intro_elem.select_one('.all')
                    if all_elem:
                        text = all_elem.get_text(strip=True)
                    else:
                        text = intro_elem.get_text(strip=True)
                    
                    # 简单过滤，避免提取到无关短语
                    if text and len(text) > 10 and '展开' not in text[:5]:
                        description = text
                        logger.debug(f"  提取到简介 ({selector}): {description[:50]}...")
                        break
            
            if description:
                author_data['description'] = description
            else:
                logger.debug(f"  未找到简介元素")
            
            # 2. 提取作者信息
            # 尝试从 #content .info (personage) 或 #content (book author) 提取
            info_text = ""
            info_elem = soup.select_one('#content .info') or soup.select_one('#content')
            if info_elem:
                info_text = info_elem.get_text()
                logger.debug(f"  找到作者信息区域")
                
                # 提取原名
                if '原名' in info_text:
                    lines = info_text.split('\n')
                    for line in lines:
                        if '原名' in line and (':' in line or '：' in line):
                            parts = line.replace('：', ':').split(':')
                            if len(parts) > 1:
                                original_name = parts[-1].strip()
                                if original_name:
                                    author_data['original_name'] = original_name
                                    logger.debug(f"  提取到原名: {original_name}")
                                break
                
                # 提取国籍 (处理多种格式: [美], 国籍: 美国, etc)
                if '国籍' in info_text:
                    lines = info_text.split('\n')
                    for line in lines:
                        if '国籍' in line and (':' in line or '：' in line):
                            parts = line.replace('：', ':').split(':')
                            if len(parts) > 1:
                                country = parts[-1].strip()
                                if country:
                                    author_data['country'] = country
                                    logger.debug(f"  提取到国籍: {country}")
                                break
            else:
                logger.debug(f"  未找到作者信息元素")
            
            return author_data
            
        except Exception as e:
            logger.error(f"解析作者详情失败: {e}")
            import traceback
            traceback.print_exc()
            return None
    
    def _get_photo_url(self, soup):
        """获取作者照片 URL（支持多种页面结构）"""
        # 尝试多种选择器
        selectors = ['#content .pic img', '.article .pic img', '#mainpic img', 'div.pic img']
        
        for selector in selectors:
            photo_elem = soup.select_one(selector)
            if photo_elem and photo_elem.get('src'):
                photo_url = photo_elem['src']
                # 确保不是默认头像
                if 'icon/user_normal' not in photo_url and 'icon/u' not in photo_url:
                    logger.debug(f"  找到照片 URL ({selector}): {photo_url}")
                    return photo_url
        
        logger.debug(f"  未找到照片元素")
        return None
