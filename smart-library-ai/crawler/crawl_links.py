"""
统一资源链接爬取执行器（优化版）

用法:
    python crawl_links.py --type info --limit 10       # 爬取书籍页
    python crawl_links.py --type download --limit 10   # 爬取下载页
    python crawl_links.py --type review --limit 10     # 爬取解读页
    python crawl_links.py --type all --limit 10        # 爬取所有类型（一次性完成）

@author JacoryCyJin
@date 2026/03/08
"""
import argparse
import logging
import time
import signal
import sys
from typing import List, Dict
from utils.link_task_helper import LinkTaskHelper
from utils.db_helper import DatabaseHelper
from crawlers.douban_link_crawler import DoubanLinkCrawler
from crawlers.download_crawler import DownloadCrawler
from crawlers.review_crawler import ReviewCrawler

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 全局变量：当前正在处理的任务
current_task_id = None
current_resource_id = None
current_link_type = None


class LinkCrawlExecutor:
    """链接爬取执行器"""
    
    def __init__(self):
        self.crawlers = {
            LinkTaskHelper.LINK_TYPE_INFO: DoubanLinkCrawler(),      # 书籍页
            LinkTaskHelper.LINK_TYPE_DOWNLOAD: DownloadCrawler(),    # 下载页
            LinkTaskHelper.LINK_TYPE_REVIEW: ReviewCrawler() # 解读页
        }
        
        # 注册信号处理器（Ctrl+C 中断）
        signal.signal(signal.SIGINT, self._handle_interrupt)
        signal.signal(signal.SIGTERM, self._handle_interrupt)
    
    def _handle_interrupt(self, signum, frame):
        """
        处理中断信号（Ctrl+C）
        清理当前正在处理的任务数据
        """
        global current_task_id, current_resource_id, current_link_type
        
        logger.warning("\n\n收到中断信号，正在清理当前任务数据...")
        
        if current_task_id and current_resource_id and current_link_type:
            try:
                # 1. 重置任务状态为待处理
                LinkTaskHelper.update_page_status(
                    current_task_id, 
                    current_link_type, 
                    LinkTaskHelper.STATUS_PENDING,
                    error_msg="任务被用户中断"
                )
                logger.info(f"✓ 已重置任务 {current_task_id} 状态为待处理")
                
                # 2. 删除已插入的 resource_link 记录
                self._cleanup_resource_links(current_resource_id, current_link_type)
                logger.info(f"✓ 已清理资源 {current_resource_id} 的链接数据")
                
            except Exception as e:
                logger.error(f"清理任务数据失败: {e}")
        
        logger.info("清理完成，程序退出")
        sys.exit(0)
    
    def _cleanup_resource_links(self, resource_id: str, link_type: int):
        """
        清理 resource_link 表中的链接数据
        
        Args:
            resource_id: 资源ID
            link_type: 链接类型
        """
        try:
            db = DatabaseHelper()
            
            query = """
            DELETE FROM resource_link
            WHERE resource_id = :resource_id
            AND link_type = :link_type
            """
            
            db.execute_query(query, {
                'resource_id': resource_id,
                'link_type': link_type
            })
            
        except Exception as e:
            logger.error(f"清理 resource_link 失败: {e}")
    
    def execute_tasks(self, link_type: int, limit: int = 10):
        """
        执行爬取任务（按链接类型）
        
        Args:
            link_type: 链接类型（1-书籍页 / 2-下载页 / 3-解读页）
            limit: 最大任务数
        """
        # 获取待处理任务
        tasks = LinkTaskHelper.get_pending_tasks(link_type=link_type, limit=limit)
        
        if not tasks:
            logger.info(f"没有待处理的{self._get_type_name(link_type)}任务")
            return
        
        logger.info(f"找到 {len(tasks)} 个待处理的{self._get_type_name(link_type)}任务")
        
        success_count = 0
        failed_count = 0
        no_resource_count = 0
        
        crawler = self.crawlers.get(link_type)
        if not crawler:
            logger.error(f"未找到对应的爬虫 (type={link_type})")
            return
        
        for task in tasks:
            task_id = task['id']
            resource_id = task['resource_id']
            isbn = task['isbn']
            title = task['title']
            
            # 设置全局变量（用于中断清理）
            global current_task_id, current_resource_id, current_link_type
            current_task_id = task_id
            current_resource_id = resource_id
            current_link_type = link_type
            
            logger.info(f"\n处理任务 {task_id}: {title}")
            
            try:
                # 更新状态为处理中
                LinkTaskHelper.update_page_status(task_id, link_type, LinkTaskHelper.STATUS_PROCESSING)
                
                # 执行爬取
                links = crawler.search_links(
                    resource_id=resource_id,
                    isbn=isbn,
                    title=title
                )
                
                # 保存结果
                if links:
                    # 去重：检查是否已存在相同链接
                    links = self._deduplicate_links(resource_id, links, link_type)
                    
                    if links:
                        LinkTaskHelper.save_page_result(task_id, link_type, links)
                        logger.info(f"  ✓ 成功爬取 {len(links)} 个链接")
                        success_count += 1
                        
                        # 将链接插入到 resource_link 表
                        self._save_links_to_db(resource_id, links, link_type)
                    else:
                        logger.warning(f"  ✗ 所有链接均已存在，跳过")
                        LinkTaskHelper.update_page_status(
                            task_id,
                            link_type,
                            LinkTaskHelper.STATUS_COMPLETED,
                            error_msg="链接已存在"
                        )
                else:
                    LinkTaskHelper.update_page_status(
                        task_id,
                        link_type,
                        LinkTaskHelper.STATUS_NO_RESOURCE,
                        error_msg="未找到相关链接"
                    )
                    logger.warning(f"  ✗ 未找到链接")
                    no_resource_count += 1
                
                # 清除全局变量
                current_task_id = None
                current_resource_id = None
                current_link_type = None
                
                # 延迟，避免请求过快
                time.sleep(2)
                
            except Exception as e:
                logger.error(f"  ✗ 任务失败: {e}")
                LinkTaskHelper.update_page_status(
                    task_id,
                    link_type,
                    LinkTaskHelper.STATUS_FAILED,
                    error_msg=str(e)
                )
                failed_count += 1
                
                # 清除全局变量
                current_task_id = None
                current_resource_id = None
                current_link_type = None
        
        # 输出统计
        logger.info("\n" + "="*50)
        logger.info(f"{self._get_type_name(link_type)}爬取完成：成功 {success_count}，失败 {failed_count}，无资源 {no_resource_count}")
        logger.info("="*50)
    
    def execute_all_types(self, limit: int = 10):
        """
        一次性爬取所有类型的链接（不包括下载页，因为暂时停用）
        
        Args:
            limit: 每种类型的最大任务数
        """
        logger.info("开始爬取所有类型的链接（书籍页 + 解读页）...")
        
        # 只爬取书籍页和解读页（下载页暂时停用）
        for link_type in [
            LinkTaskHelper.LINK_TYPE_INFO,      # 书籍页
            # LinkTaskHelper.LINK_TYPE_DOWNLOAD,  # 下载页（已注释，暂时停用）
            LinkTaskHelper.LINK_TYPE_REVIEW     # 解读页
        ]:
            logger.info(f"\n{'='*50}")
            logger.info(f"开始爬取{self._get_type_name(link_type)}")
            logger.info(f"{'='*50}")
            
            self.execute_tasks(link_type, limit)
            
            time.sleep(3)  # 不同类型之间延迟
    
    def _deduplicate_links(self, resource_id: str, links: List[Dict], link_type: int) -> List[Dict]:
        """
        去重链接：检查数据库中是否已存在相同的链接
        
        Args:
            resource_id: 资源ID
            links: 链接列表
            link_type: 链接类型
        
        Returns:
            List[Dict]: 去重后的链接列表
        """
        try:
            db = DatabaseHelper()
            
            # 查询已存在的链接URL
            query = """
            SELECT url
            FROM resource_link
            WHERE resource_id = :resource_id
            AND link_type = :link_type
            AND deleted = 0
            """
            
            result = db.execute_query(query, {
                'resource_id': resource_id,
                'link_type': link_type
            })
            
            existing_urls = {row[0] for row in result.fetchall()}
            
            # 过滤掉已存在的链接
            new_links = [link for link in links if link.get('url') not in existing_urls]
            
            if len(new_links) < len(links):
                logger.info(f"  去重：{len(links)} 个链接中有 {len(links) - len(new_links)} 个已存在")
            
            return new_links
            
        except Exception as e:
            logger.error(f"去重检查失败: {e}")
            return links  # 出错时返回原列表
    
    def _save_links_to_db(self, resource_id: str, links: List[Dict], link_type: int):
        """
        将爬取的链接保存到 resource_link 表
        
        Args:
            resource_id: 资源ID
            links: 链接列表
            link_type: 链接类型
        """
        try:
            db = DatabaseHelper()
            
            for link in links:
                # 生成 link_id
                import uuid
                link_id = str(uuid.uuid4())
                
                # 从链接数据中提取 platform（如果有）
                platform = link.get('platform', 99)  # 默认为其他
                
                query = """
                INSERT INTO resource_link (
                    link_id, resource_id, link_type, platform,
                    url, title, description, sort_order, status
                ) VALUES (
                    :link_id, :resource_id, :link_type, :platform,
                    :url, :title, :description, :sort_order, 1
                )
                ON DUPLICATE KEY UPDATE
                    url = VALUES(url),
                    title = VALUES(title),
                    description = VALUES(description),
                    sort_order = VALUES(sort_order)
                """
                
                db.execute_query(query, {
                    'link_id': link_id,
                    'resource_id': resource_id,
                    'link_type': link_type,
                    'platform': platform,
                    'url': link.get('url'),
                    'title': link.get('title'),
                    'description': link.get('description'),
                    'sort_order': link.get('sort_order', 0)
                })
            
            logger.info(f"  ✓ 已保存 {len(links)} 个链接到 resource_link 表")
            
        except Exception as e:
            logger.error(f"保存链接到数据库失败: {e}")
    
    def _get_type_name(self, link_type: int) -> str:
        """获取链接类型名称"""
        type_map = {
            LinkTaskHelper.LINK_TYPE_INFO: "书籍页",
            LinkTaskHelper.LINK_TYPE_DOWNLOAD: "下载页",
            LinkTaskHelper.LINK_TYPE_REVIEW: "解读页"
        }
        return type_map.get(link_type, "未知")


def main():
    parser = argparse.ArgumentParser(description='爬取资源链接')
    parser.add_argument(
        '--type',
        choices=['info', 'download', 'review', 'all'],
        required=True,
        help='爬取类型: info-书籍页 / download-下载页 / review-解读页 / all-全部'
    )
    parser.add_argument(
        '--limit',
        type=int,
        default=10,
        help='最大任务数（默认10）'
    )
    
    args = parser.parse_args()
    
    executor = LinkCrawlExecutor()
    
    # 根据参数确定 link_type
    if args.type == 'all':
        executor.execute_all_types(limit=args.limit)
    else:
        type_map = {
            'info': LinkTaskHelper.LINK_TYPE_INFO,
            'download': LinkTaskHelper.LINK_TYPE_DOWNLOAD,
            'review': LinkTaskHelper.LINK_TYPE_REVIEW
        }
        link_type = type_map[args.type]
        executor.execute_tasks(link_type=link_type, limit=args.limit)


if __name__ == '__main__':
    main()
