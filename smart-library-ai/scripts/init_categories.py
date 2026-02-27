"""
初始化分类数据脚本

将预定义的分类和标签插入到数据库中

@author JacoryCyJin
@date 2025/02/27
"""
import sys
import os
import uuid
import logging

# 添加父目录到路径，以便导入 crawler 模块
sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'crawler'))

from utils import DatabaseHelper

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)


# 分类数据结构
CATEGORIES_DATA = {
    '文学': [
        '小说', '文学', '外国文学', '经典', '中国文学', '随笔', '日本文学', 
        '散文', '诗歌', '童话', '儿童文学', '名著', '古典文学', '当代文学', 
        '杂文', '外国名著', '诗词', '港台'
    ],
    '流行': [
        '漫画', '推理', '绘本', '悬疑', '科幻', '青春', '言情', '推理小说', 
        '奇幻', '日本漫画', '武侠', '耽美', '科幻小说', '网络小说', '穿越', 
        '轻小说', '魔幻', '青春文学', '校园'
    ],
    '文化': [
        '历史', '心理学', '哲学', '社会学', '传记', '文化', '艺术', '社会', 
        '政治', '设计', '政治学', '宗教', '中国历史', '电影', '建筑', '数学', 
        '回忆录', '思想', '人物传记', '艺术史', '国学', '人文', '音乐', '绘画', 
        '戏剧', '西方哲学', '近代史', '二战', '军事', '佛教', '考古', '美术', 
        '自由主义'
    ],
    '生活': [
        '爱情', '成长', '生活', '女性', '心理', '旅行', '励志', '教育', 
        '摄影', '职场', '美食', '游记', '健康', '灵修', '情感', '人际关系', 
        '两性', '养生', '手工', '家居', '自助游'
    ],
    '经管': [
        '经济学', '管理', '经济', '商业', '金融', '投资', '营销', '理财', 
        '创业', '股票', '广告', '企业史', '策划'
    ],
    '科技': [
        '科普', '互联网', '科学', '编程', '交互设计', '算法', '用户体验', 
        '科技', 'web', '交互', '通信', 'UE', '神经网络', 'UCD', '程序'
    ]
}


def generate_uuid():
    """生成无连字符的完整 UUID（32位）"""
    return uuid.uuid4().hex


class CategoryInitializer:
    """分类初始化器"""
    
    def __init__(self):
        self.db = DatabaseHelper()
        self.stats = {
            'categories_created': 0,
            'categories_skipped': 0
        }
    
    def init_all(self):
        """初始化所有分类和标签"""
        logger.info("=" * 60)
        logger.info("开始初始化分类数据")
        logger.info("=" * 60)
        
        # 一级分类映射（用于生成 category_id）
        main_category_map = {
            '文学': 'literature',
            '流行': 'popular',
            '文化': 'culture',
            '生活': 'lifestyle',
            '经管': 'business',
            '科技': 'technology'
        }
        
        # 获取一个持久的数据库会话
        session = self.db.get_session()
        
        try:
            for idx, (main_category_name, sub_categories) in enumerate(CATEGORIES_DATA.items()):
                logger.info(f"\n处理一级分类: {main_category_name}")
                
                # 创建一级分类（使用完整 UUID）
                main_category_id = generate_uuid()
                
                self._create_category_with_session(
                    session,
                    category_id=main_category_id,
                    name=main_category_name,
                    parent_id=None,
                    level=1,
                    sort_order=idx
                )
                logger.info(f"  ✓ 创建一级分类: {main_category_name}")
                
                # 提交一级分类
                session.commit()
                
                # 创建二级分类
                for sub_idx, sub_category_name in enumerate(sub_categories):
                    # 创建二级分类（使用完整 UUID）
                    sub_category_id = generate_uuid()
                    
                    self._create_category_with_session(
                        session,
                        category_id=sub_category_id,
                        name=sub_category_name,
                        parent_id=main_category_id,
                        level=2,
                        sort_order=sub_idx
                    )
                    logger.info(f"    ✓ 创建二级分类: {sub_category_name}")
                
                # 每处理完一个一级分类，就提交一次
                session.commit()
                logger.info(f"  ✓ 已提交 {main_category_name} 分类的所有数据")
            
            logger.info("\n所有数据已提交到数据库")
            
        except Exception as e:
            session.rollback()
            logger.error(f"初始化失败，已回滚: {e}")
            raise
        finally:
            session.close()
        
        # 输出统计
        self._print_stats()
    
    def _create_category_with_session(self, session, category_id, name, parent_id, level, sort_order):
        """使用指定会话创建分类（直接插入，category_id 是唯一键）"""
        try:
            query = """
            INSERT INTO category (
                category_id, name, parent_id, level, sort_order
            ) VALUES (
                :category_id, :name, :parent_id, :level, :sort_order
            )
            """
            from sqlalchemy import text
            session.execute(text(query), {
                'category_id': category_id,
                'name': name,
                'parent_id': parent_id,
                'level': level,
                'sort_order': sort_order
            })
            self.stats['categories_created'] += 1
        except Exception as e:
            logger.error(f"创建分类失败 {name}: {e}")
            self.stats['categories_skipped'] += 1
            raise
    
    def _print_stats(self):
        """打印统计信息"""
        logger.info("\n" + "=" * 60)
        logger.info("初始化完成！统计信息：")
        logger.info("=" * 60)
        logger.info(f"分类创建: {self.stats['categories_created']}")
        logger.info(f"分类跳过: {self.stats['categories_skipped']}")
        logger.info("=" * 60)


def main():
    """主函数"""
    try:
        initializer = CategoryInitializer()
        initializer.init_all()
        return 0
    except Exception as e:
        logger.error(f"初始化失败: {e}")
        import traceback
        traceback.print_exc()
        return 1


if __name__ == '__main__':
    sys.exit(main())
