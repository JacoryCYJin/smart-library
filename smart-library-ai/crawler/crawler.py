"""
Smart Library 爬虫系统 - 统一入口

功能：
1. 基于任务队列的豆瓣图书爬取（支持断点续爬）
2. 基于任务队列的 Z-Library 电子书文件下载
3. 进度追踪和错误处理

@author JacoryCyJin
@date 2025/02/27
"""
import sys
import logging
import time
import uuid
from config import Config
from crawlers import DoubanBookCrawler, DoubanAuthorCrawler, ZLibraryCrawler
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
    """智能图书馆爬虫（基于任务队列）"""
    
    def __init__(self):
        self.db = DatabaseHelper()
        self.minio = MinioHelper()
        self.book_crawler = DoubanBookCrawler()
        self.author_crawler = DoubanAuthorCrawler()
        self.zlib_crawler = ZLibraryCrawler()
        
        self.stats = {
            'books_crawled': 0,
            'books_skipped': 0,
            'authors_crawled': 0,
            'authors_skipped': 0,
            'files_downloaded': 0,
            'tasks_completed': 0,
            'tasks_failed': 0
        }
    
    def init_douban_tasks(self, books_per_category=20):
        """
        初始化豆瓣爬虫任务
        
        Args:
            books_per_category: 每个分类的目标爬取数量
        """
        logger.info("=" * 60)
        logger.info("初始化豆瓣爬虫任务")
        logger.info("=" * 60)
        
        # 初始化豆瓣任务
        douban_count = self.db.init_douban_tasks(books_per_category)
        logger.info(f"创建豆瓣任务: {douban_count} 个")
        logger.info(f"每个分类目标: {books_per_category} 本图书")
        
        logger.info("=" * 60)
    
    def init_zlibrary_tasks(self):
        """
        初始化 ZLibrary 爬虫任务
        """
        logger.info("=" * 60)
        logger.info("初始化 ZLibrary 爬虫任务")
        logger.info("=" * 60)
        
        # 初始化 ZLibrary 任务
        zlib_count = self.db.init_zlibrary_tasks()
        logger.info(f"创建 ZLibrary 任务: {zlib_count} 个")
        
        logger.info("=" * 60)
    
    def crawl_douban(self, limit=None):
        """
        爬取豆瓣图书（基于任务队列）
        
        Args:
            limit: 限制处理的任务数量
        """
        logger.info("\n" + "=" * 60)
        logger.info("开始爬取豆瓣图书")
        logger.info("=" * 60)
        
        start_time = time.time()
        
        # 获取待处理任务
        tasks = self.db.get_pending_douban_tasks(limit)
        
        if not tasks:
            logger.info("没有待处理的豆瓣任务")
            return
        
        logger.info(f"共 {len(tasks)} 个待处理任务")
        
        for idx, (task_id, category_id, category_name, progress, target) in enumerate(tasks, 1):
            logger.info(f"\n[{idx}/{len(tasks)}] 处理分类: {category_name}")
            logger.info(f"  进度: {progress}/{target}")
            
            try:
                # 更新任务状态为处理中
                self.db.update_douban_task_status(task_id, status=1)
                
                # 计算还需要爬取的数量
                remaining = target - progress
                
                if remaining <= 0:
                    logger.info("  已完成目标，标记为完成")
                    self.db.update_douban_task_status(task_id, status=2, progress=target)
                    self.stats['tasks_completed'] += 1
                    continue
                
                # 爬取图书（传入 category_id 用于建立关联）
                books = self.book_crawler.crawl_top_books_with_ids(
                    tag=category_name,
                    category_id=category_id,
                    count=remaining
                )
                
                success_count = 0
                
                # 处理每本图书
                for book_id, success in books:
                    if success and book_id:
                        success_count += 1
                        self.stats['books_crawled'] += 1
                        
                        # 自动创建 ZLibrary 下载任务
                        try:
                            created = self.db.create_zlibrary_task_for_resource(book_id)
                            if created:
                                logger.info(f"  ✓ 已创建 ZLibrary 下载任务: {book_id}")
                            else:
                                logger.debug(f"  ZLibrary 任务已存在或图书无 ISBN: {book_id}")
                        except Exception as e:
                            logger.warning(f"  创建 ZLibrary 任务失败: {e}")
                    else:
                        self.stats['books_skipped'] += 1
                
                # 更新进度
                new_progress = progress + success_count
                
                if new_progress >= target:
                    # 完成任务
                    self.db.update_douban_task_status(task_id, status=2, progress=new_progress)
                    self.stats['tasks_completed'] += 1
                    logger.info(f"  ✓ 任务完成，共爬取 {new_progress} 本图书")
                else:
                    # 更新进度，保持处理中状态
                    self.db.update_douban_task_status(task_id, status=1, progress=new_progress)
                    logger.info(f"  进度更新: {new_progress}/{target}")
                
                # 延迟，避免被封
                time.sleep(2)
                
            except Exception as e:
                logger.error(f"处理任务失败: {e}")
                self.db.update_douban_task_status(task_id, status=3, error_msg=str(e))
                self.stats['tasks_failed'] += 1
                continue
        
        elapsed_time = time.time() - start_time
        self._print_douban_stats(elapsed_time)
    
    def crawl_author(self, limit=None):
        """
        爬取作者详细信息（基于任务队列）
        
        Args:
            limit: 限制处理的任务数量
        """
        logger.info("\n" + "=" * 60)
        logger.info("开始爬取作者详细信息")
        logger.info("=" * 60)
        
        start_time = time.time()
        
        # 获取待处理任务
        tasks = self.db.get_pending_author_tasks(limit)
        
        if not tasks:
            logger.info("没有待处理的作者任务")
            return
        
        logger.info(f"共 {len(tasks)} 个待处理任务")
        
        for idx, (task_id, author_id, author_name, author_url) in enumerate(tasks, 1):
            logger.info(f"\n[{idx}/{len(tasks)}] 处理作者: {author_name}")
            
            try:
                # 更新任务状态为处理中
                self.db.update_author_task_status(task_id, status=1)
                
                # 爬取作者详情
                result = self.author_crawler.crawl_author_detail(
                    author_id, 
                    author_name, 
                    author_url
                )
                
                if result == 'success':
                    # 标记任务完成
                    self.db.update_author_task_status(task_id, status=2)
                    self.stats['tasks_completed'] += 1
                    self.stats['authors_crawled'] += 1
                elif result == 'no_url':
                    # 标记为无资源（没有 URL）
                    self.db.update_author_task_status(task_id, status=4, error_msg="没有作者 URL")
                    self.stats['authors_skipped'] += 1
                elif result == 'blocked':
                    # 标记为待处理（触发反爬虫，稍后重试）
                    self.db.update_author_task_status(task_id, status=0, error_msg="触发反爬虫验证")
                    self.stats['authors_skipped'] += 1
                    logger.warning(f"  ⚠ 触发反爬虫，任务重置为待处理，建议稍后重试")
                else:
                    # 标记为失败
                    self.db.update_author_task_status(task_id, status=3, error_msg="爬取失败")
                    self.stats['tasks_failed'] += 1
                
                # 延迟，避免被封（作者爬取延迟更长）
                time.sleep(5)
                
            except Exception as e:
                logger.error(f"处理任务失败: {e}")
                self.db.update_author_task_status(task_id, status=3, error_msg=str(e))
                self.stats['tasks_failed'] += 1
                continue
        
        elapsed_time = time.time() - start_time
        self._print_author_stats(elapsed_time)
    
    def crawl_zlibrary(self, limit=None):
        """
        下载 ZLibrary 电子书文件（基于任务队列）
        
        Args:
            limit: 限制处理的任务数量
        """
        logger.info("\n" + "=" * 60)
        logger.info("开始下载 ZLibrary 电子书")
        logger.info("=" * 60)
        
        start_time = time.time()
        
        # 获取待处理任务
        tasks = self.db.get_pending_zlibrary_tasks(limit)
        
        if not tasks:
            logger.info("没有待处理的 ZLibrary 任务")
            return
        
        logger.info(f"共 {len(tasks)} 个待处理任务")
        
        for idx, (task_id, resource_id, isbn, title) in enumerate(tasks, 1):
            logger.info(f"\n[{idx}/{len(tasks)}] 处理图书: {title} (ISBN: {isbn})")
            
            try:
                # 更新任务状态为处理中
                self.db.update_zlibrary_task_status(task_id, status=1)
                
                # 下载电子书文件
                files = self.zlib_crawler.download_and_get_content(isbn)
                
                if not files:
                    logger.info("  未找到可下载的电子书")
                    self.db.update_zlibrary_task_status(task_id, status=4, error_msg="未找到电子书")
                    continue
                
                file_count = 0
                pdf_ok = epub_ok = mobi_ok = 0
                
                # 上传每个文件到 MinIO
                for file_type, file_content in files.items():
                    # 检查是否已存在
                    if self.db.file_exists(resource_id, file_type):
                        logger.info(f"  {self._get_format_name(file_type)} 已存在，跳过")
                        # 标记为已下载
                        if file_type == 1:
                            pdf_ok = 1
                        elif file_type == 2:
                            epub_ok = 1
                        elif file_type == 3:
                            mobi_ok = 1
                        continue
                    
                    # 生成文件名
                    ext_map = {1: '.pdf', 2: '.epub', 3: '.mobi'}
                    file_name = f"{uuid.uuid4().hex}{ext_map.get(file_type, '.bin')}"
                    
                    # 上传到 MinIO
                    content_type_map = {
                        1: 'application/pdf',
                        2: 'application/epub+zip',
                        3: 'application/x-mobipocket-ebook'
                    }
                    
                    uploaded_file = self.minio.upload_file(
                        file_content,
                        file_name,
                        bucket_name=Config.MINIO_BUCKET_ATTACHMENTS,
                        content_type=content_type_map.get(file_type, 'application/octet-stream')
                    )
                    
                    if uploaded_file:
                        # 获取文件 URL
                        file_url = self.minio.get_file_url(
                            uploaded_file,
                            bucket_name=Config.MINIO_BUCKET_ATTACHMENTS
                        )
                        
                        # 保存到数据库
                        self.db.insert_resource_file({
                            'resource_id': resource_id,
                            'file_type': file_type,
                            'file_url': file_url,
                            'file_size': len(file_content)
                        })
                        
                        file_count += 1
                        self.stats['files_downloaded'] += 1
                        
                        # 标记为已下载
                        if file_type == 1:
                            pdf_ok = 1
                        elif file_type == 2:
                            epub_ok = 1
                        elif file_type == 3:
                            mobi_ok = 1
                        
                        logger.info(f"  ✓ {self._get_format_name(file_type)} 上传成功")
                
                # 标记任务完成
                self.db.update_zlibrary_task_status(
                    task_id, 
                    status=2,
                    pdf_downloaded=pdf_ok,
                    epub_downloaded=epub_ok,
                    mobi_downloaded=mobi_ok
                )
                self.stats['tasks_completed'] += 1
                logger.info(f"  ✓ 任务完成，共上传 {file_count} 个文件")
                
                time.sleep(2)
                
            except Exception as e:
                logger.error(f"处理任务失败: {e}")
                self.db.update_zlibrary_task_status(task_id, status=3, error_msg=str(e))
                self.stats['tasks_failed'] += 1
                continue
        
        elapsed_time = time.time() - start_time
        self._print_zlibrary_stats(elapsed_time)
    
    def _get_format_name(self, file_type):
        """获取文件格式名称"""
        format_map = {1: 'PDF', 2: 'EPUB', 3: 'MOBI'}
        return format_map.get(file_type, 'UNKNOWN')
    
    def _print_douban_stats(self, elapsed_time):
        """打印豆瓣爬取统计"""
        logger.info("\n" + "=" * 60)
        logger.info("豆瓣爬取完成！统计信息：")
        logger.info("=" * 60)
        logger.info(f"图书爬取: {self.stats['books_crawled']}")
        logger.info(f"图书跳过: {self.stats['books_skipped']}")
        logger.info(f"任务完成: {self.stats['tasks_completed']}")
        logger.info(f"任务失败: {self.stats['tasks_failed']}")
        logger.info(f"耗时: {elapsed_time / 60:.2f} 分钟")
        logger.info("=" * 60)
    
    def _print_author_stats(self, elapsed_time):
        """打印作者爬取统计"""
        logger.info("\n" + "=" * 60)
        logger.info("作者爬取完成！统计信息：")
        logger.info("=" * 60)
        logger.info(f"作者爬取: {self.stats['authors_crawled']}")
        logger.info(f"作者跳过: {self.stats['authors_skipped']}")
        logger.info(f"任务完成: {self.stats['tasks_completed']}")
        logger.info(f"任务失败: {self.stats['tasks_failed']}")
        logger.info(f"耗时: {elapsed_time / 60:.2f} 分钟")
        logger.info("=" * 60)
    
    def _print_zlibrary_stats(self, elapsed_time):
        """打印 ZLibrary 下载统计"""
        logger.info("\n" + "=" * 60)
        logger.info("ZLibrary 下载完成！统计信息：")
        logger.info("=" * 60)
        logger.info(f"文件下载: {self.stats['files_downloaded']}")
        logger.info(f"任务完成: {self.stats['tasks_completed']}")
        logger.info(f"任务失败: {self.stats['tasks_failed']}")
        logger.info(f"耗时: {elapsed_time / 60:.2f} 分钟")
        logger.info("=" * 60)


def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='Smart Library 爬虫系统（基于任务队列）')
    
    subparsers = parser.add_subparsers(dest='command', help='子命令')
    
    # init-douban 命令：初始化豆瓣任务
    init_douban_parser = subparsers.add_parser('init-douban', help='初始化豆瓣爬虫任务')
    init_douban_parser.add_argument('--books-per-category', type=int, default=20,
                                   help='每个分类的目标爬取数量（默认: 20）')
    
    # init-zlibrary 命令：初始化 ZLibrary 任务
    init_zlib_parser = subparsers.add_parser('init-zlibrary', help='初始化 ZLibrary 爬虫任务')
    
    # douban 命令：爬取豆瓣图书
    douban_parser = subparsers.add_parser('douban', help='爬取豆瓣图书')
    douban_parser.add_argument('--limit', type=int, default=None,
                              help='限制处理的任务数量')
    
    # author 命令：爬取作者详情
    author_parser = subparsers.add_parser('author', help='爬取作者详细信息')
    author_parser.add_argument('--limit', type=int, default=None,
                              help='限制处理的任务数量')
    
    # zlibrary 命令：下载电子书文件
    zlib_parser = subparsers.add_parser('zlibrary', help='下载 ZLibrary 电子书')
    zlib_parser.add_argument('--limit', type=int, default=None,
                            help='限制处理的任务数量')
    
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        return 1
    
    try:
        crawler = SmartLibraryCrawler()
        
        if args.command == 'init-douban':
            crawler.init_douban_tasks(args.books_per_category)
        elif args.command == 'init-zlibrary':
            crawler.init_zlibrary_tasks()
        elif args.command == 'douban':
            crawler.crawl_douban(args.limit)
        elif args.command == 'author':
            crawler.crawl_author(args.limit)
        elif args.command == 'zlibrary':
            crawler.crawl_zlibrary(args.limit)
        
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
