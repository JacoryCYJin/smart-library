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
            resource_id, title, sub_title, summary, cover_url, type,
            isbn, publisher, pub_date, page_count, price,
            author_name, source_origin, source_url, source_score
        ) VALUES (
            :resource_id, :title, :sub_title, :summary, :cover_url, :type,
            :isbn, :publisher, :pub_date, :page_count, :price,
            :author_name, :source_origin, :source_url, :source_score
        )
        """
        return self.execute_query(query, resource_data)
    
    def insert_author(self, author_data):
        """插入作者数据"""
        query = """
        INSERT INTO author (
            author_id, name, description, photo_url
        ) VALUES (
            :author_id, :name, :description, :photo_url
        )
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            description = VALUES(description),
            photo_url = VALUES(photo_url)
        """
        return self.execute_query(query, author_data)
    
    def insert_resource_author_rel(self, resource_id, author_id, sort_order=0):
        """插入资源-作者关联"""
        query = """
        INSERT INTO resource_author_rel (
            resource_id, author_id, sort_order
        ) VALUES (
            :resource_id, :author_id, :sort_order
        )
        """
        return self.execute_query(query, {
            'resource_id': resource_id,
            'author_id': author_id,
            'sort_order': sort_order
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
