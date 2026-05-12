"""
混合推荐算法（Hybrid Recommendation）
融合协同过滤和基于内容的推荐

@author JacoryCyJin
@date 2025/05/07
"""
import logging
from typing import Dict, List, Tuple
from sqlalchemy import create_engine, text
from config import RecommendConfig
from item_cf import ItemCFRecommender
from content_based import ContentBasedRecommender

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class HybridRecommender:
    """混合推荐器"""
    
    def __init__(self):
        self.config = RecommendConfig()
        self.engine = create_engine(self.config.get_db_url())
        
        # 初始化两个推荐器
        self.cf_recommender = ItemCFRecommender()
        self.content_recommender = ContentBasedRecommender()
        
        # 权重配置
        self.cf_weight = 0.6  # 协同过滤权重
        self.content_weight = 0.4  # 内容推荐权重
    
    def build_models(self):
        """构建推荐模型"""
        logger.info("=" * 60)
        logger.info("开始构建混合推荐模型")
        logger.info("=" * 60)
        
        # 1. 构建协同过滤模型
        logger.info("\n[1/2] 构建协同过滤模型...")
        if not self.cf_recommender.build_rating_matrix():
            logger.error("协同过滤模型构建失败！")
            return False
        
        if not self.cf_recommender.calculate_similarity():
            logger.error("协同过滤相似度计算失败！")
            return False
        
        # 2. 构建内容推荐模型
        logger.info("\n[2/2] 构建基于内容的推荐模型...")
        if not self.content_recommender.load_resources():
            logger.error("内容推荐模型构建失败！")
            return False
        
        if not self.content_recommender.calculate_tf_idf():
            logger.error("TF-IDF 计算失败！")
            return False
        
        logger.info("\n" + "=" * 60)
        logger.info("混合推荐模型构建完成！")
        logger.info("=" * 60)
        
        return True
    
    def recommend_for_user(self, user_id: str, top_n: int = 10) -> List[Tuple[str, float]]:
        """
        为用户生成混合推荐
        
        策略：
        1. 从协同过滤获取推荐（基于用户行为）
        2. 从内容推荐获取推荐（基于浏览历史的内容相似）
        3. 融合两种推荐结果
        
        Args:
            user_id: 用户ID
            top_n: 推荐数量
        
        Returns:
            [(resource_id, score), ...]
        """
        # 1. 协同过滤推荐
        cf_recommendations = self.cf_recommender.recommend_for_user(user_id, top_n=top_n * 2)
        
        # 2. 基于内容的推荐
        content_recommendations = self.content_recommender.recommend_for_user_by_history(
            user_id, top_n=top_n * 2
        )
        
        # 3. 融合推荐结果
        hybrid_scores = {}
        
        # 加权协同过滤推荐
        for resource_id, score in cf_recommendations:
            hybrid_scores[resource_id] = score * self.cf_weight
        
        # 加权内容推荐
        for resource_id, score in content_recommendations:
            if resource_id in hybrid_scores:
                hybrid_scores[resource_id] += score * self.content_weight
            else:
                hybrid_scores[resource_id] = score * self.content_weight
        
        # 4. 排序并返回 Top-N
        sorted_recommendations = sorted(hybrid_scores.items(), key=lambda x: x[1], reverse=True)
        
        return sorted_recommendations[:top_n]
    
    def save_recommendations_to_db(self, user_id: str, recommendations: List[Tuple[str, float]]):
        """
        将混合推荐结果保存到数据库
        
        Args:
            user_id: 用户ID
            recommendations: 推荐列表 [(resource_id, score), ...]
        """
        if not recommendations:
            logger.warning(f"用户 {user_id} 没有混合推荐结果")
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
                    "reason": "混合推荐（协同过滤+内容推荐）"
                })
            
            conn.commit()
        
        logger.info(f"已为用户 {user_id} 保存 {len(recommendations)} 条混合推荐")
    
    def generate_hybrid_recommendations_for_all_users(self):
        """为所有用户生成混合推荐（全量更新）"""
        # 1. 构建模型
        if not self.build_models():
            return
        
        # 2. 获取所有用户
        user_ids = self.cf_recommender.user_ids
        total_users = len(user_ids)
        
        logger.info(f"\n开始为 {total_users} 个用户生成混合推荐...")
        
        success_count = 0
        fail_count = 0
        
        for i, user_id in enumerate(user_ids, 1):
            try:
                recommendations = self.recommend_for_user(user_id)
                
                if recommendations:
                    self.save_recommendations_to_db(user_id, recommendations)
                    success_count += 1
                else:
                    fail_count += 1
                
                # 每10个用户显示一次进度
                if i % 10 == 0 or i == total_users:
                    progress = (i / total_users) * 100
                    logger.info(f"进度: {i}/{total_users} ({progress:.1f}%) | 成功: {success_count} | 失败: {fail_count}")
            
            except Exception as e:
                fail_count += 1
                logger.error(f"用户 {user_id} 混合推荐失败: {e}")
        
        logger.info("\n" + "=" * 60)
        logger.info(f"混合推荐生成完成！")
        logger.info(f"  总用户数: {total_users}")
        logger.info(f"  成功: {success_count}")
        logger.info(f"  失败: {fail_count}")
        logger.info(f"  成功率: {(success_count/total_users)*100:.1f}%")
        logger.info("=" * 60)
    
    def generate_content_similarity_matrix(self):
        """
        单独生成内容相似度矩阵（可选）
        用于后端直接查询内容相似的图书
        """
        logger.info("=" * 60)
        logger.info("生成内容相似度矩阵")
        logger.info("=" * 60)
        
        # 1. 加载资源并计算 TF-IDF
        if not self.content_recommender.load_resources():
            return
        
        if not self.content_recommender.calculate_tf_idf():
            return
        
        # 2. 保存相似度矩阵到数据库
        self.content_recommender.save_content_similarity_to_db()
        
        logger.info("=" * 60)
        logger.info("内容相似度矩阵生成完成！")
        logger.info("=" * 60)
    
    def get_statistics(self) -> Dict:
        """获取混合推荐系统统计信息"""
        cf_stats = self.cf_recommender.get_statistics()
        
        stats = {
            "cf_stats": cf_stats,
            "content_stats": {
                "total_resources": len(self.content_recommender.resources),
                "vocabulary_size": len(self.content_recommender.idf_scores)
            },
            "weights": {
                "cf_weight": self.cf_weight,
                "content_weight": self.content_weight
            }
        }
        
        return stats


def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='混合推荐系统')
    parser.add_argument('--mode', type=str, default='hybrid',
                       choices=['hybrid', 'cf-only', 'content-only', 'content-matrix'],
                       help='推荐模式: hybrid(混合), cf-only(仅协同过滤), content-only(仅内容), content-matrix(生成内容相似度矩阵)')
    
    args = parser.parse_args()
    
    recommender = HybridRecommender()
    
    if args.mode == 'hybrid':
        # 混合推荐（默认）
        recommender.generate_hybrid_recommendations_for_all_users()
    
    elif args.mode == 'cf-only':
        # 仅协同过滤
        logger.info("使用纯协同过滤推荐")
        recommender.cf_recommender.generate_recommendations_for_all_users()
    
    elif args.mode == 'content-only':
        # 仅基于内容（实验性）
        logger.info("使用纯内容推荐（实验性）")
        if recommender.content_recommender.load_resources():
            if recommender.content_recommender.calculate_tf_idf():
                # 为所有用户生成基于内容的推荐
                user_ids = []
                with recommender.engine.connect() as conn:
                    result = conn.execute(text("SELECT DISTINCT user_id FROM user_browse_history"))
                    user_ids = [row[0] for row in result]
                
                for user_id in user_ids:
                    recommendations = recommender.content_recommender.recommend_for_user_by_history(user_id)
                    if recommendations:
                        recommender.save_recommendations_to_db(user_id, recommendations)
    
    elif args.mode == 'content-matrix':
        # 生成内容相似度矩阵
        recommender.generate_content_similarity_matrix()


if __name__ == '__main__':
    main()
