"""
爬虫配置文件

@author JacoryCyJin
@date 2025/02/27
"""
import os
from urllib.parse import quote_plus
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()


class Config:
    """配置类"""
    
    # 数据库配置
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = int(os.getenv('DB_PORT', 3306))
    DB_USER = os.getenv('DB_USER', 'root')
    DB_PASSWORD = os.getenv('DB_PASSWORD', '')
    DB_NAME = os.getenv('DB_NAME', 'smart_library')
    
    # MinIO 配置
    MINIO_ENDPOINT = os.getenv('MINIO_ENDPOINT', '127.0.0.1:9000')
    MINIO_ACCESS_KEY = os.getenv('MINIO_ACCESS_KEY', 'minioadmin')
    MINIO_SECRET_KEY = os.getenv('MINIO_SECRET_KEY', 'minioadmin')
    MINIO_BUCKET_COVERS = os.getenv('MINIO_BUCKET_COVERS', 'library-covers')
    MINIO_BUCKET_ATTACHMENTS = os.getenv('MINIO_BUCKET_ATTACHMENTS', 'library-attachments')
    
    # 爬虫配置
    USER_AGENT = os.getenv('USER_AGENT', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36')
    REQUEST_DELAY = float(os.getenv('REQUEST_DELAY', 1))
    MAX_RETRIES = int(os.getenv('MAX_RETRIES', 3))
    
    @classmethod
    def get_db_url(cls):
        """获取数据库连接 URL（密码进行 URL 编码）"""
        # 对密码进行 URL 编码，处理特殊字符（如 @）
        encoded_password = quote_plus(cls.DB_PASSWORD)
        return f"mysql+pymysql://{cls.DB_USER}:{encoded_password}@{cls.DB_HOST}:{cls.DB_PORT}/{cls.DB_NAME}?charset=utf8mb4"
