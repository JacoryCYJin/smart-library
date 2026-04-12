"""
推荐系统命令行入口

@author JacoryCyJin
@date 2025/04/11
"""
import argparse
import logging
from item_cf import ItemCFRecommender

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def main():
    parser = argparse.ArgumentParser(description='Smart Library 协同过滤推荐系统')
    parser.add_argument('action', choices=['generate', 'incremental', 'stats', 'test'],
                       help='操作类型: generate-全量生成 / incremental-增量更新 / stats-统计信息 / test-测试推荐')
    parser.add_argument('--user-id', type=str, help='测试推荐时指定用户ID')
    parser.add_argument('--top-n', type=int, default=10, help='推荐数量')
    parser.add_argument('--hours', type=int, default=1, help='增量更新时查询最近N小时的活跃用户')
    
    args = parser.parse_args()
    
    recommender = ItemCFRecommender()
    
    if args.action == 'generate':
        logger.info("=" * 60)
        logger.info("开始全量生成推荐...")
        logger.info("=" * 60)
        recommender.generate_recommendations_for_all_users()
        logger.info("=" * 60)
        logger.info("推荐生成完成！")
        logger.info("=" * 60)
    
    elif args.action == 'incremental':
        logger.info("=" * 60)
        logger.info(f"开始增量更新推荐（最近 {args.hours} 小时）...")
        logger.info("=" * 60)
        recommender.generate_recommendations_incremental(args.hours)
        logger.info("=" * 60)
        logger.info("增量更新完成！")
        logger.info("=" * 60)
    
    elif args.action == 'stats':
        logger.info("=" * 60)
        logger.info("构建评分矩阵...")
        recommender.build_rating_matrix()
        
        stats = recommender.get_statistics()
        logger.info("=" * 60)
        logger.info("推荐系统统计信息:")
        logger.info(f"  总用户数: {stats['total_users']}")
        logger.info(f"  总资源数: {stats['total_resources']}")
        logger.info(f"  矩阵稀疏度: {stats['matrix_sparsity']:.2%}")
        logger.info(f"  平均每用户交互数: {stats['avg_interactions_per_user']:.2f}")
        logger.info("=" * 60)
    
    elif args.action == 'test':
        if not args.user_id:
            logger.error("测试推荐需要指定 --user-id 参数")
            return
        
        logger.info("=" * 60)
        logger.info(f"为用户 {args.user_id} 生成测试推荐...")
        logger.info("=" * 60)
        
        recommender.build_rating_matrix()
        recommender.calculate_similarity()
        
        recommendations = recommender.recommend_for_user(args.user_id, args.top_n)
        
        if recommendations:
            logger.info(f"\n推荐结果 (Top {len(recommendations)}):")
            for i, (resource_id, score) in enumerate(recommendations, 1):
                logger.info(f"  {i}. 资源ID: {resource_id}, 预测评分: {score:.4f}")
        else:
            logger.warning("没有生成推荐结果")
        
        logger.info("=" * 60)


if __name__ == '__main__':
    main()
