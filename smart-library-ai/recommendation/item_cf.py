"""
Item-CF 协同过滤推荐算法

@author JacoryCyJin
@date 2025/04/11
"""
import logging
import numpy as np
import pandas as pd
from typing import Dict, List, Tuple
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy import create_engine, text
from config import RecommendConfig
from rating_aggregator import RatingAggregator

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ItemCFRecommender:
    """基于物品的协同过滤推荐器"""
    
    def __init__(self):
        self.config = RecommendConfig()
        self.engine = create_engine(self.config.get_db_url())
        self.aggregator = RatingAggregator()
        
        # 缓存数据
        self.rating_matrix = None
        self.user_ids = []
        self.resource_ids = []
        self.similarity_matrix = None
    
    def build_rating_matrix(self):
        """构建评分矩阵"""
        logger.info("构建评分矩阵...")
        self.rating_matrix, self.user_ids, self.resource_ids = \
            self.aggregator.get_user_item_matrix()
        
        if self.rating_matrix.empty:
            logger.error("评分矩阵为空，无法进行推荐！")
            return False
        
        return True
    
    def calculate_similarity(self):
        """
        计算物品相似度矩阵
        使用余弦相似度
        """
        if self.rating_matrix is None or self.rating_matrix.empty:
            logger.error("评分矩阵未构建，无法计算相似度！")
            return False
        
        logger.info("计算物品相似度矩阵...")
        
        # 转置矩阵：行为资源，列为用户
        item_matrix = self.rating_matrix.T
        
        # 计算余弦相似度
        similarity = cosine_similarity(item_matrix)
        
        # 转换为 DataFrame
        self.similarity_matrix = pd.DataFrame(
            similarity,
            index=self.resource_ids,
            columns=self.resource_ids
        )
        
        logger.info(f"相似度矩阵维度: {self.similarity_matrix.shape}")
        
        return True
    
    def get_similar_items(self, resource_id: str, top_n: int = None) -> List[Tuple[str, float]]:
        """
        获取与指定资源最相似的物品
        
        Args:
            resource_id: 资源ID
            top_n: 返回Top-N个相似物品（默认使用配置）
        
        Returns:
            [(resource_id, similarity_score), ...]
        """
        if self.similarity_matrix is None:
            logger.error("相似度矩阵未计算！")
            return []
        
        if resource_id not in self.similarity_matrix.index:
            logger.warning(f"资源 {resource_id} 不在相似度矩阵中")
            return []
        
        if top_n is None:
            top_n = self.config.TOP_N_SIMILAR
        
        # 获取相似度分数
        similarities = self.similarity_matrix[resource_id].sort_values(ascending=False)
        
        # 排除自己，过滤低相似度
        similarities = similarities[similarities.index != resource_id]
        similarities = similarities[similarities >= self.config.SIMILARITY_THRESHOLD]
        
        # 返回 Top-N
        top_similar = similarities.head(top_n)
        
        return list(zip(top_similar.index, top_similar.values))
    
    def recommend_for_user(self, user_id: str, top_n: int = None) -> List[Tuple[str, float]]:
        """
        为指定用户生成推荐列表
        
        Args:
            user_id: 用户ID
            top_n: 推荐数量（默认使用配置）
        
        Returns:
            [(resource_id, predicted_score), ...]
        """
        if self.rating_matrix is None or self.similarity_matrix is None:
            logger.error("评分矩阵或相似度矩阵未准备好！")
            return []
        
        if user_id not in self.user_ids:
            logger.warning(f"用户 {user_id} 没有历史行为数据")
            return []
        
        if top_n is None:
            top_n = self.config.TOP_N_RECOMMEND
        
        # 获取用户已交互的资源
        user_ratings = self.rating_matrix.loc[user_id]
        interacted_items = user_ratings[user_ratings > 0].index.tolist()
        
        if not interacted_items:
            logger.warning(f"用户 {user_id} 没有交互记录")
            return []
        
        # 计算推荐分数
        scores = {}
        for item_id in self.resource_ids:
            # 跳过已交互的资源
            if item_id in interacted_items:
                continue
            
            # 计算预测评分：加权求和
            numerator = 0.0
            denominator = 0.0
            
            for interacted_item in interacted_items:
                if item_id in self.similarity_matrix.index and \
                   interacted_item in self.similarity_matrix.columns:
                    similarity = self.similarity_matrix.loc[item_id, interacted_item]
                    
                    if similarity >= self.config.SIMILARITY_THRESHOLD:
                        user_rating = user_ratings[interacted_item]
                        numerator += similarity * user_rating
                        denominator += abs(similarity)
            
            if denominator > 0:
                scores[item_id] = numerator / denominator
        
        # 排序并返回 Top-N
        sorted_scores = sorted(scores.items(), key=lambda x: x[1], reverse=True)
        
        return sorted_scores[:top_n]
    
    def save_recommendations_to_db(self, user_id: str, recommendations: List[Tuple[str, float]]):
        """
        将推荐结果保存到数据库
        
        Args:
            user_id: 用户ID
            recommendations: 推荐列表 [(resource_id, score), ...]
        """
        if not recommendations:
            logger.warning(f"用户 {user_id} 没有推荐结果")
            return
        
        with self.engine.connect() as conn:
            # 清空该用户的旧推荐
            conn.execute(text("DELETE FROM recommend_result WHERE user_id = :user_id"), 
                        {"user_id": user_id})
            
            # 插入新推荐
            for resource_id, score in recommendations:
                conn.execute(text("""
                    INSERT INTO recommend_result (user_id, resource_id, score, reason, ctime)
                    VALUES (:user_id, :resource_id, :score, :reason, NOW())
                """), {
                    "user_id": user_id,
                    "resource_id": resource_id,
                    "score": float(score),
                    "reason": "基于协同过滤"
                })
            
            conn.commit()
        
        logger.info(f"已为用户 {user_id} 保存 {len(recommendations)} 条推荐")
    
    def generate_recommendations_for_all_users(self):
        """为所有用户生成推荐（全量更新）"""
        if not self.build_rating_matrix():
            return
        
        if not self.calculate_similarity():
            return
        
        total_users = len(self.user_ids)
        logger.info(f"开始为 {total_users} 个用户生成推荐...")
        
        success_count = 0
        fail_count = 0
        
        for i, user_id in enumerate(self.user_ids, 1):
            try:
                recommendations = self.recommend_for_user(user_id)
                if recommendations:
                    self.save_recommendations_to_db(user_id, recommendations)
                    success_count += 1
                else:
                    fail_count += 1
                    # 详细分析失败原因
                    self._log_failure_reason(user_id)
                
                # 每10个用户显示一次进度
                if i % 10 == 0 or i == total_users:
                    progress = (i / total_users) * 100
                    logger.info(f"进度: {i}/{total_users} ({progress:.1f}%) | 成功: {success_count} | 失败: {fail_count}")
            
            except Exception as e:
                fail_count += 1
                logger.error(f"❌ 用户 {user_id} 失败: 异常 - {str(e)}")
        
        logger.info("=" * 60)
        logger.info(f"推荐生成完成！")
        logger.info(f"  总用户数: {total_users}")
        logger.info(f"  成功: {success_count}")
        logger.info(f"  失败: {fail_count}")
        logger.info(f"  成功率: {(success_count/total_users)*100:.1f}%")
        logger.info("=" * 60)
    
    def generate_recommendations_incremental(self, hours=1):
        """
        增量更新推荐（只更新有新行为的用户）
        
        Args:
            hours: 查询最近N小时有行为的用户
        """
        logger.info(f"开始增量更新推荐（最近 {hours} 小时有行为的用户）...")
        
        # 1. 查询最近有行为的用户
        active_users = self._get_active_users(hours)
        
        if not active_users:
            logger.info("没有活跃用户，跳过增量更新")
            return
        
        logger.info(f"发现 {len(active_users)} 个活跃用户")
        
        # 2. 构建评分矩阵（全量，因为相似度需要全局计算）
        if not self.build_rating_matrix():
            return
        
        if not self.calculate_similarity():
            return
        
        # 3. 只为活跃用户生成推荐
        success_count = 0
        fail_count = 0
        
        for i, user_id in enumerate(active_users, 1):
            try:
                recommendations = self.recommend_for_user(user_id)
                if recommendations:
                    self.save_recommendations_to_db(user_id, recommendations)
                    success_count += 1
                else:
                    fail_count += 1
                
                if i % 10 == 0 or i == len(active_users):
                    progress = (i / len(active_users)) * 100
                    logger.info(f"进度: {i}/{len(active_users)} ({progress:.1f}%) | 成功: {success_count} | 失败: {fail_count}")
            
            except Exception as e:
                fail_count += 1
                logger.error(f"为用户 {user_id} 生成推荐失败: {e}")
        
        logger.info("=" * 60)
        logger.info(f"增量更新完成！")
        logger.info(f"  活跃用户数: {len(active_users)}")
        logger.info(f"  成功: {success_count}")
        logger.info(f"  失败: {fail_count}")
        logger.info("=" * 60)
    
    def _get_active_users(self, hours):
        """
        获取最近N小时有行为的用户
        
        Args:
            hours: 小时数
        
        Returns:
            活跃用户ID列表
        """
        with self.engine.connect() as conn:
            # 查询最近有浏览、收藏、评分行为的用户
            query = text("""
                SELECT DISTINCT user_id FROM (
                    SELECT user_id FROM user_browse_history 
                    WHERE mtime >= DATE_SUB(NOW(), INTERVAL :hours HOUR)
                    UNION
                    SELECT user_id FROM user_favorite 
                    WHERE ctime >= DATE_SUB(NOW(), INTERVAL :hours HOUR)
                    UNION
                    SELECT user_id FROM comment 
                    WHERE ctime >= DATE_SUB(NOW(), INTERVAL :hours HOUR)
                ) AS active_users
            """)
            
            result = conn.execute(query, {"hours": hours})
            active_users = [row[0] for row in result]
        
        return active_users
    
    def _log_failure_reason(self, user_id: str):
        """
        详细分析并记录推荐失败的原因
        
        Args:
            user_id: 用户ID
        """
        try:
            # 1. 检查用户是否在评分矩阵中
            if user_id not in self.user_ids:
                logger.warning(f"❌ 用户 {user_id} 失败: 用户不在评分矩阵中（没有任何行为数据）")
                return
            
            # 2. 获取用户的交互数据
            user_idx = self.user_ids.index(user_id)
            user_ratings = self.rating_matrix.iloc[user_idx]
            interacted_items = user_ratings[user_ratings > 0].index.tolist()
            interacted_count = len(interacted_items)
            
            if interacted_count == 0:
                logger.warning(f"❌ 用户 {user_id} 失败: 交互资源数=0（评分矩阵中没有正值）")
                return
            
            # 3. 检查交互资源的相似度情况
            similar_items_count = 0
            for item_id in interacted_items:
                if item_id in self.similarity_matrix.index:
                    # 获取与该资源相似的物品（排除自己）
                    similarities = self.similarity_matrix[item_id]
                    similar_items = similarities[
                        (similarities.index != item_id) & 
                        (similarities >= self.config.SIMILARITY_THRESHOLD)
                    ]
                    similar_items_count += len(similar_items)
            
            if similar_items_count == 0:
                logger.warning(
                    f"❌ 用户 {user_id} 失败: 交互资源数={interacted_count}, "
                    f"但这些资源都没有相似物品（相似度阈值={self.config.SIMILARITY_THRESHOLD}）"
                )
                logger.warning(f"   交互的资源ID: {interacted_items[:5]}{'...' if len(interacted_items) > 5 else ''}")
            else:
                # 4. 检查是否所有候选资源都已被用户交互过
                candidate_count = len(self.resource_ids) - interacted_count
                logger.warning(
                    f"❌ 用户 {user_id} 失败: 交互资源数={interacted_count}, "
                    f"找到 {similar_items_count} 个相似物品，但可能都已被用户交互过"
                )
                logger.warning(f"   候选资源数={candidate_count}, 相似度阈值={self.config.SIMILARITY_THRESHOLD}")
        
        except Exception as e:
            logger.error(f"❌ 用户 {user_id} 失败原因分析异常: {str(e)}")
    
    def get_statistics(self) -> Dict:
        """获取推荐系统统计信息"""
        stats = {
            "total_users": len(self.user_ids),
            "total_resources": len(self.resource_ids),
            "matrix_sparsity": 0.0,
            "avg_interactions_per_user": 0.0
        }
        
        if self.rating_matrix is not None and not self.rating_matrix.empty:
            total_cells = self.rating_matrix.shape[0] * self.rating_matrix.shape[1]
            non_zero_cells = (self.rating_matrix > 0).sum().sum()
            stats["matrix_sparsity"] = 1 - (non_zero_cells / total_cells)
            stats["avg_interactions_per_user"] = non_zero_cells / self.rating_matrix.shape[0]
        
        return stats
