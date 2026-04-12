"""
推荐系统配置文件

@author JacoryCyJin
@date 2025/04/11
"""
import os
from urllib.parse import quote_plus
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()


class RecommendConfig:
    """推荐系统配置类"""
    
    # 数据库配置（复用爬虫配置）
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = int(os.getenv('DB_PORT', 3306))
    DB_USER = os.getenv('DB_USER', 'root')
    DB_PASSWORD = os.getenv('DB_PASSWORD', '')
    DB_NAME = os.getenv('DB_NAME', 'smart_library')
    
    # 推荐算法参数
    SIMILARITY_THRESHOLD = float(os.getenv('SIMILARITY_THRESHOLD', 0.1))  # 相似度阈值
    TOP_N_SIMILAR = int(os.getenv('TOP_N_SIMILAR', 20))  # 计算相似度时考虑的Top-N物品
    TOP_N_RECOMMEND = int(os.getenv('TOP_N_RECOMMEND', 10))  # 推荐结果数量
    
    # 行为权重配置
    WEIGHT_BROWSE = float(os.getenv('WEIGHT_BROWSE', 1.0))  # 浏览权重
    WEIGHT_FAVORITE = float(os.getenv('WEIGHT_FAVORITE', 3.0))  # 收藏权重
    WEIGHT_COMMENT = float(os.getenv('WEIGHT_COMMENT', 5.0))  # 评分权重
    
    # 评分归一化参数
    MAX_BROWSE_COUNT = int(os.getenv('MAX_BROWSE_COUNT', 10))  # 浏览次数上限
    COMMENT_SCORE_SCALE = float(os.getenv('COMMENT_SCORE_SCALE', 10.0))  # 评分满分（0-10）
    
    # Redis 缓存配置
    REDIS_HOST = os.getenv('REDIS_HOST', '127.0.0.1')
    REDIS_PORT = int(os.getenv('REDIS_PORT', 6379))
    REDIS_DB = int(os.getenv('REDIS_DB', 1))  # 使用 DB1 避免与后端冲突
    REDIS_PASSWORD = os.getenv('REDIS_PASSWORD', None)
    CACHE_EXPIRE_SECONDS = int(os.getenv('CACHE_EXPIRE_SECONDS', 86400))  # 缓存过期时间（24小时）
    
    @classmethod
    def get_db_url(cls):
        """获取数据库连接 URL"""
        encoded_password = quote_plus(cls.DB_PASSWORD)
        return f"mysql+pymysql://{cls.DB_USER}:{encoded_password}@{cls.DB_HOST}:{cls.DB_PORT}/{cls.DB_NAME}?charset=utf8mb4"
