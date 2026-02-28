"""
豆瓣图书爬虫

@author JacoryCyJin
@date 2025/02/27
"""
import requests
from bs4 import BeautifulSoup
import time
import logging
import uuid
from config import Config
from utils import DatabaseHelper, MinioHelper

logger = logging.getLogger(__name__)


class DoubanBookCrawler:
    """豆瓣图书爬虫"""
    
    def __init__(self):
        self.db = DatabaseHelper()
        self.minio = MinioHelper()
        self.session = requests.Session()
        self.session.headers.update({'User-Agent': Config.USER_AGENT})
    
    def crawl_book_by_isbn(self, isbn):
        """
        根据 ISBN 爬取图书信息
        
        Args:
            isbn: 图书 ISBN
        
        Returns:
            resource_id: 成功返回 resource_id，失败返回 None
        """
        try:
            # 检查是否已存在
            existing_id = self.db.resource_exists(isbn)
            if existing_id:
                logger.info(f"图书已存在: {isbn}")
                return existing_id
            
            # 获取图书详情页
            url = f"https://book.douban.com/isbn/{isbn}/"
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 解析图书信息
            book_data = self._parse_book_detail(soup, isbn)
            if not book_data:
                logger.warning(f"解析图书信息失败: {isbn}")
                return False
            
            # 上传封面图片
            cover_url = self._get_cover_url(soup)
            if cover_url:
                cover_file = self.minio.upload_from_url(cover_url)
                if cover_file:
                    book_data['cover_url'] = self.minio.get_file_url(cover_file)
            
            # 插入数据库
            self.db.insert_resource(book_data)
            
            # 处理作者
            authors = self._get_authors(soup)
            if authors:
                book_data['author_name'] = ', '.join(authors)  # 保存作者快照
            
            for idx, author_name in enumerate(authors):
                # 生成 32 位纯字母数字 UUID（与 Java UUIDUtil 一致）
                author_id = uuid.uuid4().hex
                self.db.insert_author({
                    'author_id': author_id,
                    'name': author_name,
                    'description': None,
                    'photo_url': None
                })
                self.db.insert_resource_author_rel(book_data['resource_id'], author_id, idx)
            
            logger.info(f"图书爬取成功: {book_data['title']}")
            time.sleep(Config.REQUEST_DELAY)
            return book_data['resource_id']
            
        except Exception as e:
            logger.error(f"爬取图书失败 {isbn}: {e}")
            return None
    
    def _parse_book_detail(self, soup, isbn):
        """解析图书详情"""
        try:
            # 标题
            title_elem = soup.select_one('#wrapper h1 span')
            if not title_elem:
                return None
            title = title_elem.text.strip()
            
            # 简介
            description = None
            intro_elem = soup.select_one('#link-report .intro')
            if intro_elem:
                description = intro_elem.text.strip()
            
            # 获取 info 区域
            info_elem = soup.select_one('#info')
            if not info_elem:
                return None
            
            # 出版社（在 <a> 标签中）
            publisher = None
            publisher_elem = info_elem.select_one('span.pl:-soup-contains("出版社") + a')
            if publisher_elem:
                publisher = publisher_elem.text.strip()
            
            # 其他信息从文本中提取
            info_text = info_elem.text
            publish_date = self._extract_info(info_text, '出版年:')
            page_count = self._extract_info(info_text, '页数:')
            price = self._extract_info(info_text, '定价:')
            
            # 转换页数为整数
            if page_count:
                try:
                    page_count = int(page_count.replace('页', '').strip())
                except:
                    page_count = None
            
            # 转换价格为浮点数
            if price:
                try:
                    price = float(price.replace('元', '').replace('CNY', '').strip())
                except:
                    price = None
            
            # 处理日期格式：将 "2025-3" 转换为 "2025-03-01"
            if publish_date:
                publish_date = self._normalize_date(publish_date)
            
            # 获取豆瓣评分
            source_score = None
            rating_elem = soup.select_one('#interest_sectl .rating_num')
            if rating_elem:
                try:
                    source_score = float(rating_elem.text.strip())
                except:
                    source_score = None
            
            # 生成 32 位纯字母数字 UUID（与 Java UUIDUtil 一致）
            resource_id = uuid.uuid4().hex
            
            return {
                'resource_id': resource_id,
                'title': title,
                'summary': description,
                'cover_url': None,  # 稍后上传封面后更新
                'type': 1,  # 图书类型
                'isbn': isbn,
                'publisher': publisher,
                'pub_date': publish_date,
                'page_count': page_count,
                'price': price,
                'author_name': None,  # 稍后从作者列表提取
                'translator_name': None,  # 稍后从译者列表提取
                'source_origin': 1,  # 1-豆瓣
                'source_url': f"https://book.douban.com/isbn/{isbn}/",
                'source_score': source_score
            }
        except Exception as e:
            logger.error(f"解析图书详情失败: {e}")
            return None
    
    def _normalize_date(self, date_str):
        """
        标准化日期格式为 YYYY-MM-DD
        
        Args:
            date_str: 原始日期字符串，如 "2025-3", "2018-10", "2001"
        
        Returns:
            标准化后的日期字符串 "YYYY-MM-DD"，失败返回 None
        """
        if not date_str:
            return None
        
        try:
            # 移除可能的空格
            date_str = date_str.strip()
            
            # 情况1: "2025-3" -> "2025-03-01"
            # 情况2: "2018-10" -> "2018-10-01"
            # 情况3: "2001" -> "2001-01-01"
            parts = date_str.split('-')
            
            if len(parts) == 1:
                # 只有年份
                year = parts[0]
                return f"{year}-01-01"
            elif len(parts) == 2:
                # 年-月
                year, month = parts
                month = month.zfill(2)  # 补零
                return f"{year}-{month}-01"
            elif len(parts) == 3:
                # 年-月-日
                year, month, day = parts
                month = month.zfill(2)
                day = day.zfill(2)
                return f"{year}-{month}-{day}"
            else:
                return None
        except Exception as e:
            logger.warning(f"日期格式化失败 {date_str}: {e}")
            return None
    
    def _get_cover_url(self, soup):
        """获取封面 URL"""
        cover_elem = soup.select_one('#mainpic img')
        if cover_elem and cover_elem.get('src'):
            return cover_elem['src'].replace('/s/', '/l/')  # 获取大图
        return None
    
    def _get_authors(self, soup):
        """
        获取作者列表（提取名字和链接）
        
        Returns:
            list: [(name, url), ...]
        """
        authors = []
        # 查找"作者"标签后的所有链接
        info_elem = soup.select_one('#info')
        if info_elem:
            # 找到"作者"这一行
            author_span = info_elem.find('span', string=lambda x: x and '作者' in x)
            if author_span:
                # 获取同一行的所有链接
                parent = author_span.parent
                author_links = parent.find_all('a')
                for link in author_links:
                    name = link.text.strip()
                    url = link.get('href', '')
                    if name and url:
                        authors.append((name, url))
        return authors
    
    def _get_translators(self, soup):
        """
        获取译者列表（提取名字和链接）
        
        Returns:
            list: [(name, url), ...]
        """
        translators = []
        # 查找"译者"标签后的所有链接
        info_elem = soup.select_one('#info')
        if info_elem:
            # 找到"译者"这一行
            translator_span = info_elem.find('span', string=lambda x: x and '译者' in x)
            if translator_span:
                # 获取同一行的所有链接
                parent = translator_span.parent
                translator_links = parent.find_all('a')
                for link in translator_links:
                    name = link.text.strip()
                    url = link.get('href', '')
                    if name and url:
                        translators.append((name, url))
        return translators
    
    def _clean_author_name(self, name):
        """
        清理作者名字，去掉国籍前缀和英文原名
        
        Args:
            name: 原始作者名字
              - "[韩] 韩江" → "韩江"
              - "[法] 杰西·安佐斯佩（Jessie Inchauspé）" → "杰西·安佐斯佩"
              - "(英) 作者名" → "作者名"
              - "【美】作者名" → "作者名"
        
        Returns:
            清理后的名字
        """
        import re
        
        # 步骤1: 去掉国籍前缀
        # 匹配：[韩]、[美]、(英)、(英国)、【日】等
        # 支持中文方括号、英文方括号、圆括号
        pattern_prefix = r'^[\[【\(][^\]】\)]+[\]】\)]\s*'
        cleaned_name = re.sub(pattern_prefix, '', name).strip()
        
        # 步骤2: 去掉英文原名（括号中的内容）
        # 匹配：（Jessie Inchauspé）、(John Smith) 等
        pattern_english = r'[（\(][^）\)]+[）\)]'
        cleaned_name = re.sub(pattern_english, '', cleaned_name).strip()
        
        return cleaned_name if cleaned_name else name
    
    def _extract_info(self, text, label):
        """从信息文本中提取特定字段"""
        try:
            lines = text.split('\n')
            for line in lines:
                if label in line:
                    # 提取冒号后的内容
                    value = line.split(label)[1].strip()
                    # 移除可能的多余空格和换行
                    value = ' '.join(value.split())
                    return value if value else None
        except:
            pass
        return None
    
    def crawl_top_books_with_ids(self, tag='小说', category_id=None, start=0, count=20):
        """
        爬取豆瓣图书标签页（使用生成器边爬边返回）
        
        Args:
            tag: 标签名称
            category_id: 分类ID（用于建立关联）
            start: 起始位置
            count: 数量
        
        Yields:
            (book_id, success): 图书 ID 和是否成功
        """
        try:
            url = f"https://book.douban.com/tag/{tag}"
            params = {'start': start, 'type': 'T'}
            
            response = self.session.get(url, params=params, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            book_items = soup.select('.subject-item')
            
            for item in book_items[:count]:
                # 获取图书链接
                link = item.select_one('.info h2 a')
                if not link:
                    continue
                
                book_url = link['href']
                
                # 尝试获取 ISBN（从详情页）
                try:
                    detail_response = self.session.get(book_url, timeout=10)
                    detail_soup = BeautifulSoup(detail_response.text, 'lxml')
                    info_text = detail_soup.select_one('#info').text
                    isbn = self._extract_info(info_text, 'ISBN:')
                    
                    if isbn:
                        book_id = self.crawl_book_by_isbn_with_id(isbn, category_id)
                        if book_id:
                            yield (book_id, True)  # 边爬边返回
                        else:
                            yield (None, False)
                    
                    time.sleep(Config.REQUEST_DELAY)
                except Exception as e:
                    logger.error(f"获取图书详情失败 {book_url}: {e}")
                    yield (None, False)
                    continue
            
            logger.info(f"标签 {tag} 爬取完成")
            
        except Exception as e:
            logger.error(f"爬取标签页失败 {tag}: {e}")
    
    def crawl_book_by_isbn_with_id(self, isbn, category_id=None):
        """
        根据 ISBN 爬取图书信息（返回图书 ID）
        
        Args:
            isbn: 图书 ISBN
            category_id: 分类ID（可选，如果提供则立即建立关联）
        
        Returns:
            图书 ID，失败返回 None
        """
        try:
            # 检查是否已存在
            existing_id = self.db.resource_exists(isbn)
            if existing_id:
                logger.info(f"图书已存在: {isbn}")
                # 如果提供了分类ID，建立关联
                if category_id:
                    try:
                        self.db.insert_resource_category_rel(existing_id, category_id)
                    except:
                        pass  # 关联可能已存在
                return existing_id
            
            # 获取图书详情页
            url = f"https://book.douban.com/isbn/{isbn}/"
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            
            # 解析图书信息
            book_data = self._parse_book_detail(soup, isbn)
            if not book_data:
                logger.warning(f"解析图书信息失败: {isbn}")
                return None
            
            # 上传封面图片（必须成功）
            cover_url = self._get_cover_url(soup)
            if not cover_url:
                logger.warning(f"  未找到封面图片，跳过此书: {isbn}")
                return None
            
            try:
                cover_file = self.minio.upload_from_url(cover_url)
                if not cover_file:
                    logger.warning(f"  封面上传失败，跳过此书: {isbn}")
                    return None
                book_data['cover_url'] = self.minio.get_file_url(cover_file)
                logger.debug(f"  封面上传成功")
            except Exception as e:
                logger.warning(f"  封面上传失败，跳过此书: {isbn} - {e}")
                return None
            
            # 处理作者
            authors = self._get_authors(soup)
            if authors:
                # 清理作者名字后保存快照
                author_names = [self._clean_author_name(name) for name, url in authors]
                book_data['author_name'] = ', '.join(author_names)  # 保存作者快照
            
            # 处理译者
            translators = self._get_translators(soup)
            if translators:
                # 清理译者名字后保存快照
                translator_names = [self._clean_author_name(name) for name, url in translators]
                book_data['translator_name'] = ', '.join(translator_names)  # 保存译者快照
            
            # 插入数据库
            self.db.insert_resource(book_data)
            resource_id = book_data['resource_id']
            
            # 立即插入作者关联（作者独立排序，从 1 开始）
            for idx, (raw_author_name, author_url) in enumerate(authors):
                # 清理作者名字（去掉国籍前缀和英文原名）
                author_name = self._clean_author_name(raw_author_name)
                
                # 检查作者是否已存在（按清理后的名字去重）
                existing_author_id = self.db.get_author_id_by_name(author_name)
                
                if existing_author_id:
                    # 作者已存在，直接使用
                    author_id = existing_author_id
                    logger.debug(f"  作者已存在: {author_name}")
                else:
                    # 生成 32 位纯字母数字 UUID（与 Java UUIDUtil 一致）
                    author_id = uuid.uuid4().hex
                    
                    # 插入新作者（使用清理后的名字）
                    self.db.insert_author({
                        'author_id': author_id,
                        'name': author_name,  # 清理后的名字
                        'original_name': None,
                        'country': None,
                        'photo_url': None,
                        'description': None,
                        'source_origin': 1,  # 1-豆瓣
                        'source_url': None  # 稍后爬取作者详情时更新
                    })
                    logger.debug(f"  创建新作者: {author_name}")
                    
                    # 创建作者爬取任务（传入链接，可能是 /author/xxx 或 /search/xxx）
                    try:
                        self.db.create_author_crawl_task(author_id, author_name, author_url)
                        logger.debug(f"  已创建作者爬取任务: {author_name} -> {author_url}")
                    except:
                        pass  # 任务可能已存在
                
                # 插入资源-作者关联（角色为"作者"，sort_order 传入 idx，实际存储为 idx+1）
                self.db.insert_resource_author_rel(resource_id, author_id, idx, role='作者')
            
            # 立即插入译者关联（译者独立排序，从 1 开始）
            for idx, (raw_translator_name, translator_url) in enumerate(translators):
                # 清理译者名字（去掉国籍前缀和英文原名）
                translator_name = self._clean_author_name(raw_translator_name)
                
                # 检查译者是否已存在（按清理后的名字去重）
                existing_translator_id = self.db.get_author_id_by_name(translator_name)
                
                if existing_translator_id:
                    # 译者已存在，直接使用
                    translator_id = existing_translator_id
                    logger.debug(f"  译者已存在: {translator_name}")
                else:
                    # 生成 32 位纯字母数字 UUID（与 Java UUIDUtil 一致）
                    translator_id = uuid.uuid4().hex
                    
                    # 插入新译者（存入 author 表，使用清理后的名字）
                    self.db.insert_author({
                        'author_id': translator_id,
                        'name': translator_name,  # 清理后的名字
                        'original_name': None,
                        'country': None,
                        'photo_url': None,
                        'description': None,
                        'source_origin': 1,  # 1-豆瓣
                        'source_url': None  # 稍后爬取作者详情时更新
                    })
                    logger.debug(f"  创建新译者: {translator_name}")
                    
                    # 只为有 /author/xxx 链接的译者创建爬取任务
                    if translator_url:
                        try:
                            self.db.create_author_crawl_task(translator_id, translator_name, translator_url)
                            logger.debug(f"  已创建译者爬取任务: {translator_name} -> {translator_url}")
                        except:
                            pass  # 任务可能已存在
                    else:
                        logger.debug(f"  译者无详情页链接，跳过任务创建: {translator_name}")
                
                # 插入资源-译者关联（角色为"译者"，译者独立排序从 1 开始）
                # sort_order 传入 idx，实际存储为 idx+1
                self.db.insert_resource_author_rel(resource_id, translator_id, idx, role='译者')
            
            # 立即插入分类关联
            if category_id:
                try:
                    self.db.insert_resource_category_rel(resource_id, category_id)
                    logger.debug(f"  已建立分类关联: {category_id}")
                except Exception as e:
                    logger.warning(f"  分类关联失败: {e}")
            
            logger.info(f"图书爬取成功: {book_data['title']}")
            time.sleep(Config.REQUEST_DELAY)
            return resource_id
            
        except Exception as e:
            logger.error(f"爬取图书失败 {isbn}: {e}")
            return None
