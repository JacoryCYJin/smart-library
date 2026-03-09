"""
豆瓣书籍页链接爬虫

@author JacoryCyJin
@date 2026/03/08
"""
import logging
from typing import List, Dict, Optional
from .base_link_crawler import BaseLinkCrawler

logger = logging.getLogger(__name__)


class DoubanLinkCrawler(BaseLinkCrawler):
    """豆瓣书籍页爬虫"""
    
    def __init__(self):
        # 初始化基类：link_type=1(书籍页), platform=1(豆瓣)
        super().__init__(link_type=self.LINK_TYPE_INFO, platform=self.PLATFORM_DOUBAN)
        
        self.base_url = "https://book.douban.com"
    
    def search_links(self, resource_id: str, isbn: Optional[str] = None, 
                    title: Optional[str] = None) -> List[Dict]:
        """
        搜索书籍页链接（豆瓣、当当、Goodreads）
        
        Args:
            resource_id: 资源ID
            isbn: ISBN号
            title: 标题
        
        Returns:
            List[Dict]: 书籍页链接列表
        """
        try:
            links = []
            
            if not isbn:
                logger.warning(f"资源 {resource_id} 缺少 ISBN，无法生成书籍页链接")
                return []
            
            # 1. 豆瓣读书
            links.append({
                'platform': self.PLATFORM_DOUBAN,
                'url': f"{self.base_url}/isbn/{isbn}",
                'title': '豆瓣读书',
                'description': f'查看《{title or "本书"}》在豆瓣读书的详细信息、评分和书评',
                'sort_order': 1
            })
            
            # 2. 当当网
            links.append({
                'platform': self.PLATFORM_DANGDANG,
                'url': f"http://search.dangdang.com/?key={isbn}",
                'title': '当当网',
                'description': '购买链接、价格信息',
                'sort_order': 2
            })
            
            # 3. Goodreads
            links.append({
                'platform': self.PLATFORM_GOODREADS,
                'url': f"https://www.goodreads.com/search?q={isbn}",
                'title': 'Goodreads',
                'description': '国际评分、英文书评',
                'sort_order': 3
            })
            
            return links
            
        except Exception as e:
            logger.error(f"生成书籍页链接失败 (resource_id={resource_id}): {e}")
            return []
