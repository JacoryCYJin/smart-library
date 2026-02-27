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
            是否成功
        """
        try:
            # 检查是否已存在
            if self.db.resource_exists(isbn):
                logger.info(f"图书已存在: {isbn}")
                return False
            
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
                author_id = f"auth-{uuid.uuid4().hex[:8]}"
                self.db.insert_author({
                    'author_id': author_id,
                    'name': author_name,
                    'description': None,
                    'photo_url': None
                })
                self.db.insert_resource_author_rel(book_data['resource_id'], author_id, idx)
            
            logger.info(f"图书爬取成功: {book_data['title']}")
            time.sleep(Config.REQUEST_DELAY)
            return True
            
        except Exception as e:
            logger.error(f"爬取图书失败 {isbn}: {e}")
            return False
    
    def _parse_book_detail(self, soup, isbn):
        """解析图书详情"""
        try:
            # 标题
            title_elem = soup.select_one('#wrapper h1 span')
            if not title_elem:
                return None
            title = title_elem.text.strip()
            
            # 副标题
            subtitle = None
            subtitle_elem = soup.select_one('#info span[property="v:subtitle"]')
            if subtitle_elem:
                subtitle = subtitle_elem.text.strip()
            
            # 简介
            description = None
            intro_elem = soup.select_one('#link-report .intro')
            if intro_elem:
                description = intro_elem.text.strip()
            
            # 出版信息
            info_text = soup.select_one('#info').text
            publisher = self._extract_info(info_text, '出版社:')
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
            
            resource_id = f"res-{uuid.uuid4().hex[:12]}"
            
            return {
                'resource_id': resource_id,
                'title': title,
                'sub_title': subtitle,
                'summary': description,
                'cover_url': None,
                'type': 1,  # 图书类型
                'isbn': isbn,
                'publisher': publisher,
                'pub_date': publish_date,
                'page_count': page_count,
                'price': price,
                'author_name': None,  # 稍后从作者列表提取
                'source_origin': 'douban',
                'source_url': f"https://book.douban.com/isbn/{isbn}/",
                'source_score': None
            }
        except Exception as e:
            logger.error(f"解析图书详情失败: {e}")
            return None
    
    def _get_cover_url(self, soup):
        """获取封面 URL"""
        cover_elem = soup.select_one('#mainpic img')
        if cover_elem and cover_elem.get('src'):
            return cover_elem['src'].replace('/s/', '/l/')  # 获取大图
        return None
    
    def _get_authors(self, soup):
        """获取作者列表"""
        authors = []
        author_elems = soup.select('#info span:contains("作者") + a')
        for elem in author_elems:
            authors.append(elem.text.strip())
        return authors
    
    def _extract_info(self, text, label):
        """从信息文本中提取特定字段"""
        try:
            lines = text.split('\n')
            for line in lines:
                if label in line:
                    return line.split(label)[1].strip()
        except:
            pass
        return None
    
    def crawl_top_books(self, tag='小说', start=0, count=20):
        """
        爬取豆瓣图书标签页
        
        Args:
            tag: 标签名称
            start: 起始位置
            count: 数量
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
                book_id = book_url.split('/')[-2]
                
                # 尝试获取 ISBN（从详情页）
                try:
                    detail_response = self.session.get(book_url, timeout=10)
                    detail_soup = BeautifulSoup(detail_response.text, 'lxml')
                    info_text = detail_soup.select_one('#info').text
                    isbn = self._extract_info(info_text, 'ISBN:')
                    
                    if isbn:
                        self.crawl_book_by_isbn(isbn)
                    
                    time.sleep(Config.REQUEST_DELAY)
                except Exception as e:
                    logger.error(f"获取图书详情失败 {book_url}: {e}")
                    continue
            
            logger.info(f"标签 {tag} 爬取完成")
            
        except Exception as e:
            logger.error(f"爬取标签页失败 {tag}: {e}")
    
    def crawl_top_books_with_ids(self, tag='小说', start=0, count=20):
        """
        爬取豆瓣图书标签页（返回图书 ID 列表）
        
        Args:
            tag: 标签名称
            start: 起始位置
            count: 数量
        
        Returns:
            [(book_id, success), ...] 图书 ID 和是否成功的列表
        """
        results = []
        
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
                        book_id = self.crawl_book_by_isbn_with_id(isbn)
                        if book_id:
                            results.append((book_id, True))
                        else:
                            results.append((None, False))
                    
                    time.sleep(Config.REQUEST_DELAY)
                except Exception as e:
                    logger.error(f"获取图书详情失败 {book_url}: {e}")
                    results.append((None, False))
                    continue
            
            logger.info(f"标签 {tag} 爬取完成")
            
        except Exception as e:
            logger.error(f"爬取标签页失败 {tag}: {e}")
        
        return results
    
    def crawl_book_by_isbn_with_id(self, isbn):
        """
        根据 ISBN 爬取图书信息（返回图书 ID）
        
        Args:
            isbn: 图书 ISBN
        
        Returns:
            图书 ID，失败返回 None
        """
        try:
            # 检查是否已存在
            if self.db.resource_exists(isbn):
                logger.info(f"图书已存在: {isbn}")
                # 获取已存在图书的 ID
                existing_id = self.db.get_resource_id_by_isbn(isbn)
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
                author_id = f"auth-{uuid.uuid4().hex[:8]}"
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
