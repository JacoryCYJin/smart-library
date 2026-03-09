"""
爬虫模块

@author JacoryCyJin
@date 2025/02/27
@update 2026/03/08 - 新增统一链接爬虫
"""
from .douban_book_crawler import DoubanBookCrawler
from .douban_author_crawler import DoubanAuthorCrawler
from .download_crawler import DownloadCrawler
from .wiki_author_crawler import WikiAuthorCrawler
from .douban_link_crawler import DoubanLinkCrawler
from .review_crawler import ReviewCrawler
from .base_link_crawler import BaseLinkCrawler

__all__ = [
    'DoubanBookCrawler', 
    'DoubanAuthorCrawler', 
    'DownloadCrawler', 
    'WikiAuthorCrawler',
    'DoubanLinkCrawler',
    'ReviewCrawler',
    'BaseLinkCrawler'
]
