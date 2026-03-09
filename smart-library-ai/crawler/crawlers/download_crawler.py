"""
下载页爬虫（电子书搜索链接生成）

支持平台（已实测验证，仅支持ISBN搜索）：
- Library Genesis (https://libgen.li) - 电子书下载主力站

说明：
- Open Library 主要是图书信息展示，不提供直接下载
- WorldCat 是图书馆联合目录，帮助找实体书，不是电子书下载站
- 目前只保留真正的电子书下载平台

测试日期：2026/03/09
测试 ISBN：9787506365437

@author JacoryCyJin
@date 2025/02/27
@update 2026/03/08 - 适配统一链接爬取架构
@update 2026/03/09 - 只使用ISBN搜索，避免版本混淆
@update 2026/03/09 - 只保留真正的电子书下载平台
"""
import logging
from typing import List, Dict, Optional
import urllib.parse
from .base_link_crawler import BaseLinkCrawler

logger = logging.getLogger(__name__)


class DownloadCrawler(BaseLinkCrawler):
    """下载页爬虫（生成电子书搜索链接，只使用ISBN）"""
    
    def __init__(self):
        # 初始化基类：link_type=2(下载页), platform=8(LibGen)
        super().__init__(link_type=self.LINK_TYPE_DOWNLOAD, platform=self.PLATFORM_LIBGEN)
    
    def search_links(self, resource_id: str, isbn: Optional[str] = None, 
                    title: Optional[str] = None) -> List[Dict]:
        """
        生成电子书搜索链接（只使用ISBN搜索）
        
        Args:
            resource_id: 资源ID
            isbn: ISBN号（必需）
            title: 标题（不使用）
        
        Returns:
            List[Dict]: 搜索链接列表
        """
        try:
            if not isbn:
                logger.warning(f"资源 {resource_id} 缺少 ISBN，无法生成下载链接")
                return []
            
            links = []
            
            # Library Genesis - 电子书下载主力（实测可用：42KB 内容）
            libgen_url = f"https://libgen.li/index.php?req={urllib.parse.quote(isbn)}"
            links.append({
                'platform': self.PLATFORM_LIBGEN,
                'url': libgen_url,
                'title': 'Library Genesis',
                'description': '电子书下载 - 支持 PDF/EPUB/MOBI 等多种格式',
                'sort_order': 1
            })
            
            logger.info(f"生成 {len(links)} 个电子书下载链接（ISBN: {isbn}）")
            return links
            
        except Exception as e:
            logger.error(f"生成下载链接失败 (resource_id={resource_id}): {e}")
            return []

