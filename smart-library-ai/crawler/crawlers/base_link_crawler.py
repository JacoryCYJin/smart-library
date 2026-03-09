"""
资源链接爬虫基类

@author JacoryCyJin
@date 2026/03/08
"""
import logging
from abc import ABC, abstractmethod
from typing import List, Dict, Optional

logger = logging.getLogger(__name__)


class BaseLinkCrawler(ABC):
    """资源链接爬虫基类"""
    
    # 链接类型常量
    LINK_TYPE_INFO = 1      # 书籍页
    LINK_TYPE_DOWNLOAD = 2  # 下载页
    LINK_TYPE_REVIEW = 3    # 解读页
    
    # 平台常量
    PLATFORM_DOUBAN = 1
    PLATFORM_ZLIBRARY = 2
    PLATFORM_BILIBILI = 3
    PLATFORM_YOUTUBE = 4
    PLATFORM_DANGDANG = 5
    PLATFORM_GOODREADS = 6
    PLATFORM_ANNAS_ARCHIVE = 7
    PLATFORM_LIBGEN = 8
    PLATFORM_JIUMO = 9
    PLATFORM_OTHER = 99
    
    def __init__(self, link_type: int, platform: int):
        """
        初始化爬虫
        
        Args:
            link_type: 链接类型（1-书籍页 / 2-下载页 / 3-解读页）
            platform: 平台（1-豆瓣 / 2-ZLibrary / 3-B站 / 4-YouTube / 99-其他）
        """
        self.link_type = link_type
        self.platform = platform
    
    @abstractmethod
    def search_links(self, resource_id: str, isbn: Optional[str] = None, 
                    title: Optional[str] = None) -> List[Dict]:
        """
        搜索资源链接
        
        Args:
            resource_id: 资源ID
            isbn: ISBN号（可选）
            title: 标题（可选）
        
        Returns:
            List[Dict]: 链接列表，每个字典包含：
                - url: 链接地址
                - title: 链接标题
                - description: 链接描述
                - format: 文件格式（仅下载页需要，如 PDF/EPUB/MOBI）
                - sort_order: 排序权重（可选）
        """
        pass
    
    def get_link_type_name(self) -> str:
        """获取链接类型名称"""
        type_map = {
            self.LINK_TYPE_INFO: "书籍页",
            self.LINK_TYPE_DOWNLOAD: "下载页",
            self.LINK_TYPE_REVIEW: "解读页"
        }
        return type_map.get(self.link_type, "未知")
    
    def get_platform_name(self) -> str:
        """获取平台名称"""
        platform_map = {
            self.PLATFORM_DOUBAN: "豆瓣",
            self.PLATFORM_ZLIBRARY: "ZLibrary",
            self.PLATFORM_BILIBILI: "哔哩哔哩",
            self.PLATFORM_YOUTUBE: "YouTube",
            self.PLATFORM_DANGDANG: "当当",
            self.PLATFORM_GOODREADS: "Goodreads",
            self.PLATFORM_ANNAS_ARCHIVE: "Anna's Archive",
            self.PLATFORM_LIBGEN: "Library Genesis",
            self.PLATFORM_JIUMO: "鸠摩搜书",
            self.PLATFORM_OTHER: "其他"
        }
        return platform_map.get(self.platform, "未知")
    
    def format_result(self, links: List[Dict]) -> Dict:
        """
        格式化爬取结果
        
        Args:
            links: 链接列表
        
        Returns:
            Dict: 格式化后的结果，包含：
                - success: 是否成功
                - links: 链接列表
                - count: 链接数量
        """
        return {
            "success": len(links) > 0,
            "links": links,
            "count": len(links)
        }
