"""
Z-Library 电子书爬虫

@author JacoryCyJin
@date 2025/02/27
"""
import requests
from bs4 import BeautifulSoup
import time
import logging
from config import Config
import uuid

logger = logging.getLogger(__name__)


class ZLibraryCrawler:
    """Z-Library 爬虫"""
    
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': Config.USER_AGENT
        })
        # Z-Library 镜像站点列表（需要定期更新）
        self.base_urls = [
            'https://zh.singlelogin.re',  # 主站
            'https://zh.z-lib.gs',         # 备用站
        ]
        self.current_base_url = self.base_urls[0]
    
    def search_by_isbn(self, isbn):
        """
        根据 ISBN 搜索图书
        
        Args:
            isbn: ISBN 号
        
        Returns:
            dict: 图书信息，包含下载链接、标签等
        """
        try:
            # 搜索 URL
            search_url = f"{self.current_base_url}/s/{isbn}"
            
            logger.info(f"搜索 ISBN: {isbn}")
            response = self.session.get(search_url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 查找第一个搜索结果
            book_item = soup.select_one('.resItemBox')
            if not book_item:
                logger.warning(f"未找到 ISBN {isbn} 的图书")
                return None
            
            # 提取图书详情页链接
            detail_link = book_item.select_one('a[href^="/book/"]')
            if not detail_link:
                return None
            
            detail_url = self.current_base_url + detail_link['href']
            
            # 访问详情页获取下载链接和标签
            time.sleep(Config.REQUEST_DELAY)
            return self._parse_detail_page(detail_url, isbn)
            
        except Exception as e:
            logger.error(f"搜索 ISBN {isbn} 失败: {e}")
            return None
    
    def _parse_detail_page(self, detail_url, isbn):
        """
        解析图书详情页
        
        Args:
            detail_url: 详情页 URL
            isbn: ISBN 号
        
        Returns:
            dict: 图书信息
        """
        try:
            response = self.session.get(detail_url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 提取标签
            tags = []
            tag_elements = soup.select('.bookProperty .property_label:-soup-contains("Tags") + .property_value a')
            for tag_elem in tag_elements:
                tag_name = tag_elem.text.strip()
                if tag_name:
                    tags.append(tag_name)
            
            # 提取下载链接
            download_links = []
            download_buttons = soup.select('a[href*="/dl/"]')
            for btn in download_buttons:
                download_url = self.current_base_url + btn['href']
                # 尝试识别文件格式
                file_format = self._detect_format(btn.text)
                if file_format:
                    download_links.append({
                        'url': download_url,
                        'format': file_format
                    })
            
            if not download_links:
                logger.warning(f"未找到 ISBN {isbn} 的下载链接")
                return None
            
            return {
                'isbn': isbn,
                'tags': tags,
                'download_links': download_links,
                'detail_url': detail_url
            }
            
        except Exception as e:
            logger.error(f"解析详情页失败 {detail_url}: {e}")
            return None
    
    def _detect_format(self, text):
        """
        从按钮文本识别文件格式
        
        Args:
            text: 按钮文本
        
        Returns:
            int: 文件格式类型 (1-PDF, 2-EPUB, 3-MOBI)
        """
        text = text.upper()
        if 'PDF' in text:
            return 1
        elif 'EPUB' in text:
            return 2
        elif 'MOBI' in text:
            return 3
        return None
    
    def download_file(self, download_url):
        """
        下载文件
        
        Args:
            download_url: 下载链接
        
        Returns:
            bytes: 文件内容
        """
        try:
            logger.info(f"下载文件: {download_url}")
            response = self.session.get(download_url, timeout=30)
            response.raise_for_status()
            
            # 检查是否是文件内容
            content_type = response.headers.get('Content-Type', '')
            if 'application' in content_type or 'octet-stream' in content_type:
                return response.content
            else:
                logger.warning(f"下载链接返回非文件内容: {content_type}")
                return None
                
        except Exception as e:
            logger.error(f"下载文件失败 {download_url}: {e}")
            return None
