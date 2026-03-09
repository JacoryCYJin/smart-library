"""
资源链接爬取任务数据库辅助类（优化版）

@author JacoryCyJin
@date 2026/03/08
"""
import json
import logging
from typing import List, Dict, Optional
from .db_helper import DatabaseHelper

logger = logging.getLogger(__name__)


class LinkTaskHelper:
    """资源链接爬取任务辅助类"""
    
    # 任务状态常量
    STATUS_PENDING = 0      # 待处理
    STATUS_PROCESSING = 1   # 处理中
    STATUS_COMPLETED = 2    # 已完成
    STATUS_FAILED = 3       # 失败
    STATUS_NO_RESOURCE = 4  # 无资源
    
    # 链接类型常量
    LINK_TYPE_INFO = 1      # 书籍页
    LINK_TYPE_DOWNLOAD = 2  # 下载页
    LINK_TYPE_REVIEW = 3    # 解读页
    
    @staticmethod
    def create_task(resource_id: str, isbn: Optional[str], title: Optional[str]) -> bool:
        """
        创建爬取任务（一本书一条记录）
        
        Args:
            resource_id: 资源ID
            isbn: ISBN号
            title: 标题
        
        Returns:
            bool: 是否创建成功
        """
        try:
            db = DatabaseHelper()
            
            sql = """
                INSERT INTO resource_link_crawl_task 
                (resource_id, isbn, title)
                VALUES (:resource_id, :isbn, :title)
                ON DUPLICATE KEY UPDATE
                    isbn = VALUES(isbn),
                    title = VALUES(title)
            """
            
            db.execute_query(sql, {
                'resource_id': resource_id,
                'isbn': isbn,
                'title': title
            })
            
            return True
            
        except Exception as e:
            logger.error(f"创建任务失败: {e}")
            return False
    
    @staticmethod
    def get_pending_tasks(link_type: int, limit: int = 10) -> List[Dict]:
        """
        获取待处理任务（按链接类型）
        
        Args:
            link_type: 链接类型（1-书籍页 / 2-下载页 / 3-解读页）
            limit: 最大数量
        
        Returns:
            List[Dict]: 任务列表
        """
        try:
            db = DatabaseHelper()
            
            # 根据链接类型选择对应的状态字段
            status_field_map = {
                1: 'info_page_status',
                2: 'download_page_status',
                3: 'review_page_status'
            }
            
            status_field = status_field_map.get(link_type)
            if not status_field:
                logger.error(f"无效的链接类型: {link_type}")
                return []
            
            sql = f"""
                SELECT id, resource_id, isbn, title
                FROM resource_link_crawl_task
                WHERE {status_field} = 0
                ORDER BY ctime ASC
                LIMIT :limit
            """
            
            result = db.execute_query(sql, {'limit': limit})
            
            tasks = []
            for row in result.fetchall():
                tasks.append({
                    'id': row[0],
                    'resource_id': row[1],
                    'isbn': row[2],
                    'title': row[3]
                })
            
            return tasks
            
        except Exception as e:
            logger.error(f"获取待处理任务失败: {e}")
            return []
    
    @staticmethod
    def update_page_status(task_id: int, link_type: int, status: int, 
                          error_msg: Optional[str] = None) -> bool:
        """
        更新特定页面类型的状态
        
        Args:
            task_id: 任务ID
            link_type: 链接类型（1-书籍页 / 2-下载页 / 3-解读页）
            status: 新状态
            error_msg: 错误信息（可选）
        
        Returns:
            bool: 是否更新成功
        """
        try:
            db = DatabaseHelper()
            
            # 根据链接类型选择对应的状态字段
            status_field_map = {
                1: 'info_page_status',
                2: 'download_page_status',
                3: 'review_page_status'
            }
            
            status_field = status_field_map.get(link_type)
            if not status_field:
                logger.error(f"无效的链接类型: {link_type}")
                return False
            
            sql = f"""
                UPDATE resource_link_crawl_task
                SET {status_field} = :status, error_msg = :error_msg
                WHERE id = :task_id
            """
            
            db.execute_query(sql, {
                'status': status,
                'error_msg': error_msg,
                'task_id': task_id
            })
            
            # 更新整体状态
            LinkTaskHelper._update_overall_status(task_id)
            
            return True
            
        except Exception as e:
            logger.error(f"更新任务状态失败: {e}")
            return False
    
    @staticmethod
    def save_page_result(task_id: int, link_type: int, links: List[Dict]) -> bool:
        """
        保存特定页面类型的爬取结果
        
        Args:
            task_id: 任务ID
            link_type: 链接类型（1-书籍页 / 2-下载页 / 3-解读页）
            links: 链接列表
        
        Returns:
            bool: 是否保存成功
        """
        try:
            db = DatabaseHelper()
            
            # 根据链接类型选择对应的字段
            field_map = {
                1: ('info_page_count', 'info_page_json', 'info_page_status'),
                2: ('download_page_count', 'download_page_json', 'download_page_status'),
                3: ('review_page_count', 'review_page_json', 'review_page_status')
            }
            
            fields = field_map.get(link_type)
            if not fields:
                logger.error(f"无效的链接类型: {link_type}")
                return False
            
            count_field, json_field, status_field = fields
            
            # 将链接列表转换为 JSON
            result_json = json.dumps(links, ensure_ascii=False)
            links_count = len(links)
            
            # 根据结果数量决定状态
            status = LinkTaskHelper.STATUS_COMPLETED if links_count > 0 else LinkTaskHelper.STATUS_NO_RESOURCE
            
            sql = f"""
                UPDATE resource_link_crawl_task
                SET {count_field} = :count, 
                    {json_field} = :json, 
                    {status_field} = :status,
                    error_msg = NULL
                WHERE id = :task_id
            """
            
            db.execute_query(sql, {
                'count': links_count,
                'json': result_json,
                'status': status,
                'task_id': task_id
            })
            
            logger.info(f"任务 {task_id} 保存结果成功：{links_count} 个链接")
            
            # 更新整体状态
            LinkTaskHelper._update_overall_status(task_id)
            
            return True
            
        except Exception as e:
            logger.error(f"保存任务结果失败: {e}")
            return False
    
    @staticmethod
    def _update_overall_status(task_id: int):
        """
        更新任务的整体状态
        
        Args:
            task_id: 任务ID
        """
        try:
            db = DatabaseHelper()
            
            sql = """
                UPDATE resource_link_crawl_task
                SET overall_status = CASE
                    WHEN info_page_status = 2 AND download_page_status = 2 AND review_page_status = 2 THEN 2
                    WHEN info_page_status = 3 AND download_page_status = 3 AND review_page_status = 3 THEN 3
                    WHEN info_page_status IN (2, 4) OR download_page_status IN (2, 4) OR review_page_status IN (2, 4) THEN 1
                    ELSE 0
                END
                WHERE id = :task_id
            """
            
            db.execute_query(sql, {'task_id': task_id})
            
        except Exception as e:
            logger.error(f"更新整体状态失败: {e}")
    
    @staticmethod
    def get_task_statistics() -> List[Dict]:
        """
        获取任务统计信息
        
        Returns:
            List[Dict]: 统计信息
        """
        try:
            db = DatabaseHelper()
            
            sql = """
                SELECT 
                    '书籍页' as page_type,
                    COUNT(*) as total,
                    SUM(CASE WHEN info_page_status = 0 THEN 1 ELSE 0 END) as pending,
                    SUM(CASE WHEN info_page_status = 1 THEN 1 ELSE 0 END) as processing,
                    SUM(CASE WHEN info_page_status = 2 THEN 1 ELSE 0 END) as completed,
                    SUM(CASE WHEN info_page_status = 3 THEN 1 ELSE 0 END) as failed,
                    SUM(CASE WHEN info_page_status = 4 THEN 1 ELSE 0 END) as no_resource,
                    SUM(info_page_count) as total_links
                FROM resource_link_crawl_task
                
                UNION ALL
                
                SELECT 
                    '下载页' as page_type,
                    COUNT(*) as total,
                    SUM(CASE WHEN download_page_status = 0 THEN 1 ELSE 0 END) as pending,
                    SUM(CASE WHEN download_page_status = 1 THEN 1 ELSE 0 END) as processing,
                    SUM(CASE WHEN download_page_status = 2 THEN 1 ELSE 0 END) as completed,
                    SUM(CASE WHEN download_page_status = 3 THEN 1 ELSE 0 END) as failed,
                    SUM(CASE WHEN download_page_status = 4 THEN 1 ELSE 0 END) as no_resource,
                    SUM(download_page_count) as total_links
                FROM resource_link_crawl_task
                
                UNION ALL
                
                SELECT 
                    '解读页' as page_type,
                    COUNT(*) as total,
                    SUM(CASE WHEN review_page_status = 0 THEN 1 ELSE 0 END) as pending,
                    SUM(CASE WHEN review_page_status = 1 THEN 1 ELSE 0 END) as processing,
                    SUM(CASE WHEN review_page_status = 2 THEN 1 ELSE 0 END) as completed,
                    SUM(CASE WHEN review_page_status = 3 THEN 1 ELSE 0 END) as failed,
                    SUM(CASE WHEN review_page_status = 4 THEN 1 ELSE 0 END) as no_resource,
                    SUM(review_page_count) as total_links
                FROM resource_link_crawl_task
            """
            
            result = db.execute_query(sql)
            
            stats = []
            for row in result.fetchall():
                stats.append({
                    'page_type': row[0],
                    'total': row[1],
                    'pending': row[2],
                    'processing': row[3],
                    'completed': row[4],
                    'failed': row[5],
                    'no_resource': row[6],
                    'total_links': row[7]
                })
            
            return stats
            
        except Exception as e:
            logger.error(f"获取任务统计失败: {e}")
            return []
