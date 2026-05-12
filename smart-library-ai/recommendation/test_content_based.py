"""
测试基于内容的推荐算法

@author JacoryCyJin
@date 2025/05/07
"""
import logging
from content_based import ContentBasedRecommender

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def test_content_based():
    """测试基于内容的推荐"""
    logger.info("=" * 60)
    logger.info("测试基于内容的推荐算法")
    logger.info("=" * 60)
    
    # 1. 初始化推荐器
    recommender = ContentBasedRecommender()
    
    # 2. 加载资源
    if not recommender.load_resources():
        logger.error("加载资源失败！")
        return
    
    logger.info(f"\n加载了 {len(recommender.resources)} 个资源")
    
    # 3. 显示部分资源的关键词
    logger.info("\n示例资源的关键词提取结果:")
    for i, (resource_id, data) in enumerate(list(recommender.resources.items())[:3], 1):
        logger.info(f"\n资源 {i}: {data['title']}")
        logger.info(f"  作者: {data['author_name']}")
        logger.info(f"  关键词数量: {len(data['keywords'])}")
        logger.info(f"  关键词示例: {data['keywords'][:20]}")
    
    # 4. 计算 TF-IDF
    if not recommender.calculate_tf_idf():
        logger.error("TF-IDF 计算失败！")
        return
    
    # 5. 测试相似度计算
    logger.info("\n" + "=" * 60)
    logger.info("测试相似度计算")
    logger.info("=" * 60)
    
    # 随机选择一个资源
    test_resource_id = list(recommender.resources.keys())[0]
    test_resource = recommender.resources[test_resource_id]
    
    logger.info(f"\n测试资源: {test_resource['title']}")
    logger.info(f"作者: {test_resource['author_name']}")
    
    # 获取相似资源
    similar_items = recommender.get_similar_items(test_resource_id, top_n=5)
    
    logger.info(f"\n找到 {len(similar_items)} 个相似资源:")
    for i, (similar_id, similarity) in enumerate(similar_items, 1):
        similar_resource = recommender.resources[similar_id]
        logger.info(f"\n{i}. {similar_resource['title']}")
        logger.info(f"   作者: {similar_resource['author_name']}")
        logger.info(f"   相似度: {similarity:.4f}")
        
        # 显示相似度计算细节
        cosine_sim = recommender.calculate_cosine_similarity(test_resource_id, similar_id)
        jaccard_sim = recommender.calculate_jaccard_similarity(test_resource_id, similar_id)
        logger.info(f"   余弦相似度: {cosine_sim:.4f}")
        logger.info(f"   Jaccard相似度: {jaccard_sim:.4f}")
    
    # 6. 测试基于浏览历史的推荐
    logger.info("\n" + "=" * 60)
    logger.info("测试基于浏览历史的推荐")
    logger.info("=" * 60)
    
    # 查询一个有浏览历史的用户
    from sqlalchemy import create_engine, text
    from config import RecommendConfig
    
    config = RecommendConfig()
    engine = create_engine(config.get_db_url())
    
    with engine.connect() as conn:
        result = conn.execute(text("""
            SELECT user_id, COUNT(*) as count
            FROM user_browse_history
            WHERE deleted = 0
            GROUP BY user_id
            ORDER BY count DESC
            LIMIT 1
        """))
        
        row = result.fetchone()
        if row:
            test_user_id = row[0]
            browse_count = row[1]
            
            logger.info(f"\n测试用户: {test_user_id}")
            logger.info(f"浏览历史数量: {browse_count}")
            
            # 获取推荐
            recommendations = recommender.recommend_for_user_by_history(test_user_id, top_n=5)
            
            logger.info(f"\n为该用户生成了 {len(recommendations)} 条推荐:")
            for i, (resource_id, score) in enumerate(recommendations, 1):
                resource = recommender.resources.get(resource_id)
                if resource:
                    logger.info(f"\n{i}. {resource['title']}")
                    logger.info(f"   作者: {resource['author_name']}")
                    logger.info(f"   推荐分数: {score:.4f}")
        else:
            logger.warning("没有找到有浏览历史的用户")
    
    logger.info("\n" + "=" * 60)
    logger.info("测试完成！")
    logger.info("=" * 60)


if __name__ == '__main__':
    test_content_based()
