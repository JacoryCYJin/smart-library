"""
Smart Library 爬虫系统 - 统一入口

功能：
1. 爬取豆瓣图书分类体系
2. 根据分类爬取图书信息
3. 爬取 Z-Library 电子书文件

@author JacoryCyJin
@date 2025/02/27
"""
import sys
import logging
import time
import uuid
import requests
from bs4 import BeautifulSoup
from config import Config
from crawlers import DoubanBookCrawler, ZLibraryCrawler
from utils import DatabaseHelper, MinioHelper

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('crawler.log', encoding='utf-8'),
        logging.StreamHandler()
    ]
)

logger = logging.getLogger(__name__)


class SmartLibraryCrawler:
    """智能图书馆爬虫"""
    
    # 豆瓣一级分类映射
    MAIN_CATEGORIES = {
        '文学': 'literature',
        '流行': 'popular',
        '文化': 'culture',
        '生活': 'lifestyle',
        '经管': 'business',
        '科技': 'technology'
    }
    
    def __init__(self):
        self.db = DatabaseHelper()
        self.minio = MinioHelper()
        self.book_crawler = DoubanBookCrawler()
        self.zlib_crawler = ZLibraryCrawler()
        self.session = requests.Session()
        self.session.headers.update({'User-Agent': Config.USER_AGENT})
        
        self.stats = {
            'categories_created': 0,
            'tags_created': 0,
            'books_crawled': 0,
            'books_skipped': 0,
            'files_downloaded': 0,
            'relations_created': 0
        }
    
    def crawl_all(self, books_per_tag=10, download_files=False):
        """
        完整爬虫流程
        
        Args:
            books_per_tag: 每个标签爬取的图书数量
            download_files: 是否下载电子书文件
        """
        logger.info("\n" + "=" * 60)
        logger.info("开始完整爬虫流程")
        logger.info("=" * 60)
        
        start_time = time.time()
        
        # 步骤 1: 爬取分类体系
        logger.info("\n步骤 1: 爬取豆瓣分类体系")
        category_map = self._crawl_categories()
        
        if not category_map:
            logger.error("分类爬取失败，终止流程")
            return
        
        # 步骤 2: 根据分类爬取图书
        logger.info("\n步骤 2: 根据分类爬取图书")
        self._crawl_books_by_categories(category_map, books_per_tag)
        
        # 步骤 3: 下载电子书文件（可选）
        if download_files:
            logger.info("\n步骤 3: 下载电子书文件")
            self._download_ebook_files()
        
        # 输出统计
        elapsed_time = time.time() - start_time
        self._print_stats(elapsed_time)
    
    def _crawl_categories(self):
        """爬取豆瓣图书分类体系"""
        logger.info("=" * 60)
        logger.info("开始爬取豆瓣图书分类")
        logger.info("=" * 60)
        
        try:
            url = "https://book.douban.com/tag/"
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            
            soup = BeautifulSoup(response.text, 'lxml')
            category_map = {}
            sections = soup.select('.tagCol')
            
            for section in sections:
                title_elem = section.select_one('h2')
                if not title_elem:
                    continue
                
                main_category_name = title_elem.text.strip().replace(' · · · · · · ', '')
                
                if main_category_name not in self.MAIN_CATEGORIES:
                    continue
                
                logger.info(f"\n处理一级分类: {main_category_name}")
                
                # 创建一级分类
                main_category_id = f"cat-{self.MAIN_CATEGORIES[main_category_name]}"
                self._create_category(
                    category_id=main_category_id,
                    name=main_category_name,
                    parent_id=None,
                    level=1,
                    sort_order=list(self.MAIN_CATEGORIES.keys()).index(main_category_name)
                )
                
                # 获取该分类下的所有标签
                tag_links = section.select('.tagList a')
                
                for idx, tag_link in enumerate(tag_links):
                    tag_name = tag_link.text.strip()
                    
                    # 移除标签后的数字
                    if '(' in tag_name:
                        tag_name = tag_name.split('(')[0]
                    
                    # 创建二级分类
                    tag_category_id = f"cat-{self.MAIN_CATEGORIES[main_category_name]}-{uuid.uuid4().hex[:8]}"
                    
                    self._create_category(
                        category_id=tag_category_id,
                        name=tag_name,
                        parent_id=main_category_id,
                        level=2,
                        sort_order=idx
                    )
                    
                    # 同时创建标签
                    tag_id = f"tag-{uuid.uuid4().hex[:8]}"
                    self._create_tag(tag_id=tag_id, name=tag_name, tag_type=2)
                    
                    # 存储映射关系
                    category_map[tag_name] = {
                        'category_id': tag_category_id,
                        'tag_id': tag_id,
                        'parent_category': main_category_name
                    }
                    
                    logger.info(f"  ✓ {tag_name}")
                
                time.sleep(1)
            
            logger.info("\n" + "=" * 60)
            logger.info(f"分类爬取完成，共 {len(category_map)} 个标签")
            logger.info("=" * 60)
            
            return category_map
            
        except Exception as e:
            logger.error(f"爬取分类失败: {e}")
            return {}
    
    def _crawl_books_by_categories(self, category_map, books_per_tag):
        """根据分类爬取图书"""
        total_tags = len(category_map)
        
        for idx, (tag_name, tag_info) in enumerate(category_map.items(), 1):
            logger.info(f"\n[{idx}/{total_tags}] 爬取标签: {tag_name}")
            logger.info(f"  父分类: {tag_info['parent_category']}")
            
            try:
                # 爬取该标签下的图书
                books = self.book_crawler.crawl_top_books_with_ids(
                    tag=tag_name,
                    count=books_per_tag
                )
                
                # 处理每本图书
                for book_id, success in books:
                    if success and book_id:
                        # 建立图书-分类关联
                        try:
                            self.db.insert_resource_category_rel(
                                book_id,
                                tag_info['category_id']
                            )
                            self.stats['relations_created'] += 1
                        except Exception as e:
                            logger.debug(f"分类关联已存在: {e}")
                        
                        # 建立图书-标签关联
                        try:
                            self.db.insert_resource_tag_rel(
                                book_id,
                                tag_info['tag_id'],
                                weight=1.0
                            )
                            self.stats['relations_created'] += 1
                        except Exception as e:
                            logger.debug(f"标签关联已存在: {e}")
                        
                        self.stats['books_crawled'] += 1
                    else:
                        self.stats['books_skipped'] += 1
                
                logger.info(f"  ✓ 完成，爬取 {len([b for b in books if b[1]])} 本图书")
                
                # 延迟，避免被封
                time.sleep(2)
                
            except Exception as e:
                logger.error(f"爬取标签 {tag_name} 失败: {e}")
                continue
    
    def _download_ebook_files(self, limit=None):
        """下载电子书文件"""
        logger.info("=" * 60)
        logger.info("开始下载电子书文件")
        logger.info("=" * 60)
        
        # 获取所有图书的 ISBN
        isbns = self.db.get_all_isbns()
        total = len(isbns) if not limit else min(len(isbns), limit)
        
        logger.info(f"共 {len(isbns)} 本图书，准备下载 {total} 本")
        
        for idx, (resource_id, isbn) in enumerate(isbns[:total], 1):
            logger.info(f"\n[{idx}/{total}] 处理图书: {isbn}")
            
            try:
                # 搜索 Z-Library
                books = self.zlib_crawler.search_by_isbn(isbn)
                
                if not books:
                    logger.info("  未找到电子书")
                    continue
                
                # 下载第一个结果的所有格式
                book = books[0]
                
                for file_type, file_type_name in [(1, 'PDF'), (2, 'EPUB'), (3, 'MOBI')]:
                    # 检查是否已存在
                    if self.db.file_exists(resource_id, file_type):
                        logger.info(f"  {file_type_name} 已存在，跳过")
                        continue
                    
                    # 下载文件
                    file_path = self.zlib_crawler.download_book(book, file_type_name.lower())
                    
                    if file_path:
                        # 上传到 MinIO
                        file_url = self.minio.upload_file(file_path, 'library-attachments')
                        
                        if file_url:
                            # 保存到数据库
                            import os
                            file_size = os.path.getsize(file_path)
                            
                            self.db.insert_resource_file({
                                'resource_id': resource_id,
                                'file_type': file_type,
                                'file_url': file_url,
                                'file_size': file_size
                            })
                            
                            self.stats['files_downloaded'] += 1
                            logger.info(f"  ✓ {file_type_name} 下载成功")
                            
                            # 删除本地文件
                            os.remove(file_path)
                
                time.sleep(2)
                
            except Exception as e:
                logger.error(f"处理图书失败 {isbn}: {e}")
                continue
    
    def _create_category(self, category_id, name, parent_id, level, sort_order):
        """创建分类"""
        try:
            self.db.insert_category({
                'category_id': category_id,
                'name': name,
                'parent_id': parent_id,
                'level': level,
                'sort_order': sort_order
            })
            self.stats['categories_created'] += 1
        except Exception as e:
            logger.debug(f"分类已存在或插入失败: {name}")
    
    def _create_tag(self, tag_id, name, tag_type):
        """创建标签"""
        try:
            self.db.insert_tag({
                'tag_id': tag_id,
                'name': name,
                'type': tag_type
            })
            self.stats['tags_created'] += 1
        except Exception as e:
            logger.debug(f"标签已存在或插入失败: {name}")
    
    def _print_stats(self, elapsed_time):
        """打印统计信息"""
        logger.info("\n" + "=" * 60)
        logger.info("爬取完成！统计信息：")
        logger.info("=" * 60)
        logger.info(f"分类创建: {self.stats['categories_created']}")
        logger.info(f"标签创建: {self.stats['tags_created']}")
        logger.info(f"图书爬取: {self.stats['books_crawled']}")
        logger.info(f"图书跳过: {self.stats['books_skipped']}")
        logger.info(f"文件下载: {self.stats['files_downloaded']}")
        logger.info(f"关联创建: {self.stats['relations_created']}")
        logger.info(f"耗时: {elapsed_time / 60:.2f} 分钟")
        logger.info("=" * 60)


def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='Smart Library 爬虫系统')
    parser.add_argument('--books-per-tag', type=int, default=10, 
                        help='每个标签爬取的图书数量（默认: 10）')
    parser.add_argument('--download-files', action='store_true',
                        help='是否下载电子书文件')
    
    args = parser.parse_args()
    
    try:
        crawler = SmartLibraryCrawler()
        crawler.crawl_all(
            books_per_tag=args.books_per_tag,
            download_files=args.download_files
        )
        return 0
    except KeyboardInterrupt:
        logger.info("\n用户中断爬取")
        return 1
    except Exception as e:
        logger.error(f"爬取失败: {e}")
        import traceback
        traceback.print_exc()
        return 1


if __name__ == '__main__':
    sys.exit(main())
