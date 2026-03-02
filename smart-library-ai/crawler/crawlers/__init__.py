"""
爬虫模块

@author JacoryCyJin
@date 2025/02/27
"""
from .douban_book_crawler import DoubanBookCrawler
from .douban_author_crawler import DoubanAuthorCrawler
from .zlibrary_crawler import ZLibraryCrawler
from .wiki_author_crawler import WikiAuthorCrawler

__all__ = ['DoubanBookCrawler', 'DoubanAuthorCrawler', 'ZLibraryCrawler', 'WikiAuthorCrawler']
