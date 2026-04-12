"""
评分聚合器 - 将浏览、收藏、评分融合为统一评分

@author JacoryCyJin
@date 2025/04/11
"""
import logging
from typing import Dict, List, Tuple
import pandas as pd
from sqlalchemy import create_engine, text
from config import RecommendConfig

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class RatingAggregator:
    """评分聚合器"""
    
    def __init__(self):
        self.config = RecommendConfig()
        self.engine = create_engine(self.config.get_db_url())
    
    def fetch_browse_data(self) -> pd.DataFrame:
        """获取浏览历史数据"""
        query = """
        SELECT user_id, resource_id, view_count
        FROM user_browse_history
        WHERE view_count > 0
        """
        df = pd.read_sql(query, self.engine)
        logger.info(f"获取浏览数据: {len(df)} 条记录")
        return df
    
    def fetch_favorite_data(self) -> pd.DataFrame:
        """获取收藏数据"""
        query = """
        SELECT user_id, resource_id, 1 as is_favorited
        FROM user_favorite
        """
        df = pd.read_sql(query, self.engine)
        logger.info(f"获取收藏数据: {len(df)} 条记录")
        return df
    
    def fetch_comment_data(self) -> pd.DataFrame:
        """获取评分数据"""
        query = """
        SELECT user_id, resource_id, score
        FROM comment
        WHERE score > 0 AND deleted = 0 AND audit_status = 1
        """
        df = pd.read_sql(query, self.engine)
        logger.info(f"获取评分数据: {len(df)} 条记录")
        return df
    
    def normalize_browse_score(self, view_count: int) -> float:
        """
        归一化浏览次数为 0-1 分数
        使用对数函数平滑处理，避免极端值
        """
        import math
        # 使用对数函数: log(1 + x) / log(1 + max)
        normalized = math.log(1 + min(view_count, self.config.MAX_BROWSE_COUNT)) / \
                     math.log(1 + self.config.MAX_BROWSE_COUNT)
        return normalized
    
    def normalize_comment_score(self, score: float) -> float:
        """归一化评分为 0-1 分数"""
        return score / self.config.COMMENT_SCORE_SCALE
    
    def aggregate_ratings(self) -> pd.DataFrame:
        """
        聚合所有行为数据为统一评分矩阵
        
        Returns:
            DataFrame with columns: user_id, resource_id, rating
        """
        logger.info("开始聚合评分数据...")
        
        # 1. 获取原始数据
        browse_df = self.fetch_browse_data()
        favorite_df = self.fetch_favorite_data()
        comment_df = self.fetch_comment_data()
        
        # 2. 归一化浏览数据
        if not browse_df.empty:
            browse_df['normalized_score'] = browse_df['view_count'].apply(self.normalize_browse_score)
            browse_df['rating'] = browse_df['normalized_score'] * self.config.WEIGHT_BROWSE
            browse_df = browse_df[['user_id', 'resource_id', 'rating']]
        
        # 3. 处理收藏数据（收藏即为满分）
        if not favorite_df.empty:
            favorite_df['rating'] = self.config.WEIGHT_FAVORITE
            favorite_df = favorite_df[['user_id', 'resource_id', 'rating']]
        
        # 4. 归一化评分数据
        if not comment_df.empty:
            comment_df['normalized_score'] = comment_df['score'].apply(self.normalize_comment_score)
            comment_df['rating'] = comment_df['normalized_score'] * self.config.WEIGHT_COMMENT
            comment_df = comment_df[['user_id', 'resource_id', 'rating']]
        
        # 5. 合并所有数据
        all_ratings = []
        if not browse_df.empty:
            all_ratings.append(browse_df)
        if not favorite_df.empty:
            all_ratings.append(favorite_df)
        if not comment_df.empty:
            all_ratings.append(comment_df)
        
        if not all_ratings:
            logger.warning("没有任何评分数据！")
            return pd.DataFrame(columns=['user_id', 'resource_id', 'rating'])
        
        # 6. 合并并求和（同一用户对同一资源的多种行为累加）
        rating_matrix = pd.concat(all_ratings, ignore_index=True)
        rating_matrix = rating_matrix.groupby(['user_id', 'resource_id'], as_index=False)['rating'].sum()
        
        logger.info(f"聚合完成: {len(rating_matrix)} 条评分记录")
        logger.info(f"涉及用户数: {rating_matrix['user_id'].nunique()}")
        logger.info(f"涉及资源数: {rating_matrix['resource_id'].nunique()}")
        
        return rating_matrix
    
    def get_user_item_matrix(self) -> Tuple[pd.DataFrame, List[str], List[str]]:
        """
        构建用户-物品评分矩阵
        
        Returns:
            (matrix, user_ids, resource_ids)
        """
        rating_df = self.aggregate_ratings()
        
        if rating_df.empty:
            return pd.DataFrame(), [], []
        
        # 透视表：行为用户，列为资源
        matrix = rating_df.pivot_table(
            index='user_id',
            columns='resource_id',
            values='rating',
            fill_value=0
        )
        
        user_ids = matrix.index.tolist()
        resource_ids = matrix.columns.tolist()
        
        logger.info(f"评分矩阵维度: {matrix.shape[0]} 用户 × {matrix.shape[1]} 资源")
        
        return matrix, user_ids, resource_ids
