"""
数据库操作工具类

@author JacoryCyJin
@date 2025/02/27
"""
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from config import Config
import logging

logger = logging.getLogger(__name__)


class DatabaseHelper:
    """数据库操作助手"""
    
    def __init__(self):
        self.engine = create_engine(
            Config.get_db_url(),
            pool_pre_ping=True,
            pool_recycle=3600,
            pool_size=5,
            max_overflow=10,
            pool_timeout=30,
            echo=False
        )
        self.Session = sessionmaker(bind=self.engine)
    
    def get_session(self):
        """获取数据库会话"""
        return self.Session()
    
    def execute_query(self, query, params=None):
        """执行查询"""
        session = self.get_session()
        try:
            result = session.execute(text(query), params or {})
            session.commit()
            return result
        except Exception as e:
            session.rollback()
            logger.error(f"数据库查询失败: {e}")
            raise
        finally:
            session.close()
    
    def insert_resource(self, resource_data):
        """插入资源数据"""
        query = """
        INSERT INTO resource (
            resource_id, title, summary, cover_url, type,
            isbn, publisher, pub_date, page_count, price,
            author_name, translator_name, source_origin, source_url, source_score
        ) VALUES (
            :resource_id, :title, :summary, :cover_url, :type,
            :isbn, :publisher, :pub_date, :page_count, :price,
            :author_name, :translator_name, :source_origin, :source_url, :source_score
        )
        """
        return self.execute_query(query, resource_data)
    
    def insert_author(self, author_data):
        """插入作者数据"""
        query = """
        INSERT INTO author (
            author_id, name, original_name, country, photo_url, description,
            source_origin, source_url
        ) VALUES (
            :author_id, :name, :original_name, :country, :photo_url, :description,
            :source_origin, :source_url
        )
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            original_name = VALUES(original_name),
            country = VALUES(country),
            photo_url = VALUES(photo_url),
            description = VALUES(description),
            source_origin = VALUES(source_origin),
            source_url = VALUES(source_url)
        """
        return self.execute_query(query, author_data)
    
    def get_author_id_by_name(self, author_name):
        """
        根据作者名字获取作者ID（用于去重）
        
        Args:
            author_name: 作者姓名
        
        Returns:
            author_id 或 None
        """
        query = """
        SELECT author_id 
        FROM author 
        WHERE name = :name AND deleted = 0 
        LIMIT 1
        """
        result = self.execute_query(query, {'name': author_name})
        row = result.fetchone()
        return row[0] if row else None
    
    def insert_resource_author_rel(self, resource_id, author_id, sort_order=0, role='作者'):
        """
        插入资源-作者关联
        
        Args:
            resource_id: 资源ID
            author_id: 作者ID
            sort_order: 排序（从0开始）
            role: 角色（'作者' 或 '译者'）
        """
        query = """
        INSERT INTO resource_author_rel (
            resource_id, author_id, sort, role
        ) VALUES (
            :resource_id, :author_id, :sort, :role
        )
        """
        return self.execute_query(query, {
            'resource_id': resource_id,
            'author_id': author_id,
            'sort': sort_order + 1,  # sort 从 1 开始
            'role': role
        })
    
    def insert_category(self, category_data):
        """插入分类数据"""
        query = """
        INSERT INTO category (
            category_id, name, parent_id, level, sort_order
        ) VALUES (
            :category_id, :name, :parent_id, :level, :sort_order
        )
        ON DUPLICATE KEY UPDATE
            name = VALUES(name)
        """
        return self.execute_query(query, category_data)
    
    def insert_resource_category_rel(self, resource_id, category_id):
        """插入资源-分类关联"""
        query = """
        INSERT INTO resource_category_rel (
            resource_id, category_id
        ) VALUES (
            :resource_id, :category_id
        )
        """
        return self.execute_query(query, {
            'resource_id': resource_id,
            'category_id': category_id
        })
    
    def insert_resource_file(self, file_data):
        """插入资源文件数据"""
        query = """
        INSERT INTO resource_file (
            resource_id, file_type, file_url, file_size
        ) VALUES (
            :resource_id, :file_type, :file_url, :file_size
        )
        ON DUPLICATE KEY UPDATE
            file_url = VALUES(file_url),
            file_size = VALUES(file_size)
        """
        return self.execute_query(query, file_data)
    
    def insert_tag(self, tag_data):
        """插入标签数据"""
        query = """
        INSERT INTO tag (
            tag_id, name, type
        ) VALUES (
            :tag_id, :name, :type
        )
        ON DUPLICATE KEY UPDATE
            name = VALUES(name)
        """
        return self.execute_query(query, tag_data)
    
    def insert_resource_tag_rel(self, resource_id, tag_id, weight=1.0):
        """插入资源-标签关联"""
        query = """
        INSERT INTO resource_tag_rel (
            resource_id, tag_id, weight
        ) VALUES (
            :resource_id, :tag_id, :weight
        )
        ON DUPLICATE KEY UPDATE
            weight = VALUES(weight)
        """
        return self.execute_query(query, {
            'resource_id': resource_id,
            'tag_id': tag_id,
            'weight': weight
        })
    
    def get_all_isbns(self):
        """获取所有资源的 ISBN"""
        query = "SELECT resource_id, isbn FROM resource WHERE isbn IS NOT NULL AND isbn != ''"
        result = self.execute_query(query)
        return [(row[0], row[1]) for row in result.fetchall()]
    
    def resource_exists(self, isbn):
        """
        检查资源是否已存在（通过 ISBN）
        
        Args:
            isbn: 图书 ISBN
        
        Returns:
            resource_id 或 None
        """
        query = "SELECT resource_id FROM resource WHERE isbn = :isbn AND deleted = 0 LIMIT 1"
        result = self.execute_query(query, {'isbn': isbn})
        row = result.fetchone()
        return row[0] if row else None
    
    def get_resource_id_by_isbn(self, isbn):
        """
        根据 ISBN 获取资源 ID
        
        Args:
            isbn: 图书 ISBN
        
        Returns:
            resource_id 或 None
        """
        return self.resource_exists(isbn)
    
    def file_exists(self, resource_id, file_type):
        """
        检查资源文件是否已存在
        
        Args:
            resource_id: 资源 ID
            file_type: 文件类型 (1-PDF, 2-EPUB, 3-MOBI)
        
        Returns:
            bool: 是否存在
        """
        query = """
        SELECT COUNT(*) as count 
        FROM resource_file 
        WHERE resource_id = :resource_id 
        AND file_type = :file_type 
        AND deleted = 0
        """
        result = self.execute_query(query, {
            'resource_id': resource_id,
            'file_type': file_type
        })
        return result.fetchone()[0] > 0
    
    def get_tag_by_name(self, tag_name):
        """
        根据标签名称获取标签 ID
        
        Args:
            tag_name: 标签名称
        
        Returns:
            tag_id 或 None
        """
        query = "SELECT tag_id FROM tag WHERE name = :name LIMIT 1"
        result = self.execute_query(query, {'name': tag_name})
        row = result.fetchone()
        return row[0] if row else None
    
    def category_exists(self, category_id):
        """
        检查分类是否存在
        
        Args:
            category_id: 分类 ID
        
        Returns:
            bool: 是否存在
        """
        query = "SELECT COUNT(*) as count FROM category WHERE category_id = :category_id"
        result = self.execute_query(query, {'category_id': category_id})
        return result.fetchone()[0] > 0
    
    def get_all_level2_categories(self):
        """
        获取所有二级分类
        
        Returns:
            list: [(category_id, name, parent_id), ...]
        """
        query = """
        SELECT category_id, name, parent_id 
        FROM category 
        WHERE level = 2 AND deleted = 0
        ORDER BY sort_order
        """
        result = self.execute_query(query)
        return [(row[0], row[1], row[2]) for row in result.fetchall()]
    
    # ==========================================
    # 豆瓣爬虫任务管理
    # ==========================================
    
    def init_douban_tasks(self, target_per_category=20):
        """
        初始化豆瓣爬虫任务（基于二级分类）
        
        Args:
            target_per_category: 每个分类的目标爬取数量
        
        Returns:
            int: 创建的任务数量
        """
        categories = self.get_all_level2_categories()
        created_count = 0
        
        for category_id, category_name, parent_id in categories:
            # 检查任务是否已存在
            check_query = """
            SELECT COUNT(*) FROM douban_crawl_task 
            WHERE category_id = :category_id
            """
            result = self.execute_query(check_query, {'category_id': category_id})
            
            if result.fetchone()[0] == 0:
                # 创建新任务
                insert_query = """
                INSERT INTO douban_crawl_task (
                    category_id, category_name, target, status
                ) VALUES (
                    :category_id, :category_name, :target, 0
                )
                """
                self.execute_query(insert_query, {
                    'category_id': category_id,
                    'category_name': category_name,
                    'target': target_per_category
                })
                created_count += 1
        
        return created_count
    
    def get_pending_douban_tasks(self, limit=None):
        """
        获取待处理的豆瓣任务
        
        Args:
            limit: 限制返回数量
        
        Returns:
            list: [(id, category_id, category_name, progress, target), ...]
        """
        query = """
        SELECT id, category_id, category_name, progress, target
        FROM douban_crawl_task
        WHERE status IN (0, 1)
        ORDER BY id
        """
        if limit:
            query += f" LIMIT {limit}"
        
        result = self.execute_query(query)
        return [(row[0], row[1], row[2], row[3], row[4]) for row in result.fetchall()]
    
    def update_douban_task_status(self, task_id, status, progress=None, error_msg=None):
        """
        更新豆瓣任务状态
        
        Args:
            task_id: 任务ID
            status: 状态 (0-待处理 / 1-处理中 / 2-已完成 / 3-失败)
            progress: 进度
            error_msg: 错误信息
        """
        updates = ['status = :status']
        params = {'task_id': task_id, 'status': status}
        
        if progress is not None:
            updates.append('progress = :progress')
            params['progress'] = progress
        
        if error_msg is not None:
            updates.append('error_msg = :error_msg')
            params['error_msg'] = error_msg
        
        query = f"""
        UPDATE douban_crawl_task 
        SET {', '.join(updates)}
        WHERE id = :task_id
        """
        self.execute_query(query, params)
    
    # ==========================================
    # 作者爬取任务管理
    # ==========================================
    
    def create_author_crawl_task(self, author_id, author_name, douban_author_url=None):
        """
        创建作者爬取任务
        
        Args:
            author_id: 作者ID
            author_name: 作者姓名
            douban_author_url: 豆瓣作者页面URL（可选）
        
        Returns:
            bool: 是否创建成功
        """
        # 检查任务是否已存在
        check_query = """
        SELECT COUNT(*) FROM author_crawl_task 
        WHERE author_id = :author_id
        """
        result = self.execute_query(check_query, {'author_id': author_id})
        
        if result.fetchone()[0] > 0:
            return False  # 任务已存在
        
        # 创建新任务
        insert_query = """
        INSERT INTO author_crawl_task (
            author_id, author_name, douban_author_url, status
        ) VALUES (
            :author_id, :author_name, :douban_author_url, 0
        )
        """
        self.execute_query(insert_query, {
            'author_id': author_id,
            'author_name': author_name,
            'douban_author_url': douban_author_url
        })
        return True
    
    # ==========================================
    # ZLibrary 下载任务管理
    # ==========================================
    
    def create_zlibrary_task_for_resource(self, resource_id):
        """
        为单个资源创建 ZLibrary 下载任务
        
        Args:
            resource_id: 资源ID
        
        Returns:
            bool: 是否创建成功
        """
        # 获取资源信息
        query = """
        SELECT resource_id, isbn, title
        FROM resource
        WHERE resource_id = :resource_id
        AND isbn IS NOT NULL 
        AND isbn != ''
        AND deleted = 0
        """
        result = self.execute_query(query, {'resource_id': resource_id})
        row = result.fetchone()
        
        if not row:
            return False
        
        resource_id, isbn, title = row[0], row[1], row[2]
        
        # 检查任务是否已存在
        check_query = """
        SELECT COUNT(*) FROM zlibrary_download_task 
        WHERE resource_id = :resource_id
        """
        check_result = self.execute_query(check_query, {'resource_id': resource_id})
        
        if check_result.fetchone()[0] > 0:
            return False  # 任务已存在
        
        # 创建新任务
        insert_query = """
        INSERT INTO zlibrary_download_task (
            resource_id, isbn, title, status
        ) VALUES (
            :resource_id, :isbn, :title, 0
        )
        """
        self.execute_query(insert_query, {
            'resource_id': resource_id,
            'isbn': isbn,
            'title': title
        })
        return True
    
    def init_zlibrary_tasks(self):
        """
        初始化 ZLibrary 下载任务（基于已有图书）
        
        Returns:
            int: 创建的任务数量
        """
        # 获取所有没有文件的图书
        query = """
        SELECT r.resource_id, r.isbn, r.title
        FROM resource r
        WHERE r.isbn IS NOT NULL 
        AND r.isbn != ''
        AND r.deleted = 0
        AND NOT EXISTS (
            SELECT 1 FROM resource_file rf 
            WHERE rf.resource_id = r.resource_id 
            AND rf.deleted = 0
        )
        """
        result = self.execute_query(query)
        resources = [(row[0], row[1], row[2]) for row in result.fetchall()]
        
        created_count = 0
        
        for resource_id, isbn, title in resources:
            # 检查任务是否已存在
            check_query = """
            SELECT COUNT(*) FROM zlibrary_download_task 
            WHERE resource_id = :resource_id
            """
            check_result = self.execute_query(check_query, {'resource_id': resource_id})
            
            if check_result.fetchone()[0] == 0:
                # 创建新任务
                insert_query = """
                INSERT INTO zlibrary_download_task (
                    resource_id, isbn, title, status
                ) VALUES (
                    :resource_id, :isbn, :title, 0
                )
                """
                self.execute_query(insert_query, {
                    'resource_id': resource_id,
                    'isbn': isbn,
                    'title': title
                })
                created_count += 1
        
        return created_count
    
    def get_pending_zlibrary_tasks(self, limit=None):
        """
        获取待处理的 ZLibrary 任务
        
        Args:
            limit: 限制返回数量
        
        Returns:
            list: [(id, resource_id, isbn, title), ...]
        """
        query = """
        SELECT id, resource_id, isbn, title
        FROM zlibrary_download_task
        WHERE status IN (0, 1)
        ORDER BY id
        """
        if limit:
            query += f" LIMIT {limit}"
        
        result = self.execute_query(query)
        return [(row[0], row[1], row[2], row[3]) for row in result.fetchall()]
    
    def update_zlibrary_task_status(self, task_id, status, error_msg=None, 
                                   pdf_downloaded=None, epub_downloaded=None, mobi_downloaded=None):
        """
        更新 ZLibrary 任务状态
        
        Args:
            task_id: 任务ID
            status: 状态 (0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源)
            error_msg: 错误信息
            pdf_downloaded: PDF 是否已下载
            epub_downloaded: EPUB 是否已下载
            mobi_downloaded: MOBI 是否已下载
        """
        updates = ['status = :status']
        params = {'task_id': task_id, 'status': status}
        
        if error_msg is not None:
            updates.append('error_msg = :error_msg')
            params['error_msg'] = error_msg
        
        if pdf_downloaded is not None:
            updates.append('pdf_downloaded = :pdf_downloaded')
            params['pdf_downloaded'] = pdf_downloaded
        
        if epub_downloaded is not None:
            updates.append('epub_downloaded = :epub_downloaded')
            params['epub_downloaded'] = epub_downloaded
        
        if mobi_downloaded is not None:
            updates.append('mobi_downloaded = :mobi_downloaded')
            params['mobi_downloaded'] = mobi_downloaded
        
        query = f"""
        UPDATE zlibrary_download_task 
        SET {', '.join(updates)}
        WHERE id = :task_id
        """
        self.execute_query(query, params)
