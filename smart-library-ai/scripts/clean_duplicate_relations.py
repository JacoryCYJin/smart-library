"""
清理重复的关联关系脚本

清理 resource_category_rel 表中的重复关联

@author JacoryCyJin
@date 2025/03/01
"""
import sys
import os

# 添加父目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'crawler'))

from utils import DatabaseHelper


def clean_duplicate_category_relations(db):
    """清理重复的资源-分类关联"""
    print("\n" + "=" * 60)
    print("检查资源-分类关联表")
    print("=" * 60)
    
    # 统计总数
    count_query = "SELECT COUNT(*) FROM resource_category_rel"
    result = db.execute_query(count_query)
    total_count = result.fetchone()[0]
    print(f"总关联数: {total_count}")
    
    # 统计唯一关联数
    unique_query = "SELECT COUNT(DISTINCT resource_id, category_id) FROM resource_category_rel"
    result = db.execute_query(unique_query)
    unique_count = result.fetchone()[0]
    print(f"唯一关联数: {unique_count}")
    
    duplicate_count = total_count - unique_count
    if duplicate_count > 0:
        print(f"⚠ 发现 {duplicate_count} 个重复关联")
        
        # 删除重复的关联（保留 ID 最小的）
        delete_query = """
        DELETE t1 FROM resource_category_rel t1
        INNER JOIN resource_category_rel t2 
        WHERE t1.id > t2.id 
        AND t1.resource_id = t2.resource_id 
        AND t1.category_id = t2.category_id
        """
        db.execute_query(delete_query)
        print(f"✓ 已删除 {duplicate_count} 个重复关联")
    else:
        print("✓ 没有重复关联")
    
    # 检查是否有关联到已删除资源的记录
    orphan_query = """
    SELECT COUNT(*) 
    FROM resource_category_rel rcr
    LEFT JOIN resource r ON rcr.resource_id = r.resource_id
    WHERE r.resource_id IS NULL OR r.deleted = 1
    """
    result = db.execute_query(orphan_query)
    orphan_count = result.fetchone()[0]
    
    if orphan_count > 0:
        print(f"⚠ 发现 {orphan_count} 个孤立关联（关联到已删除的资源）")
        
        # 删除孤立关联
        delete_orphan_query = """
        DELETE rcr FROM resource_category_rel rcr
        LEFT JOIN resource r ON rcr.resource_id = r.resource_id
        WHERE r.resource_id IS NULL OR r.deleted = 1
        """
        db.execute_query(delete_orphan_query)
        print(f"✓ 已删除 {orphan_count} 个孤立关联")
    else:
        print("✓ 没有孤立关联")


def clean_duplicate_author_relations(db):
    """清理重复的资源-作者关联"""
    print("\n" + "=" * 60)
    print("检查资源-作者关联表")
    print("=" * 60)
    
    # 统计总数
    count_query = "SELECT COUNT(*) FROM resource_author_rel"
    result = db.execute_query(count_query)
    total_count = result.fetchone()[0]
    print(f"总关联数: {total_count}")
    
    # 统计唯一关联数
    unique_query = "SELECT COUNT(DISTINCT resource_id, author_id) FROM resource_author_rel"
    result = db.execute_query(unique_query)
    unique_count = result.fetchone()[0]
    print(f"唯一关联数: {unique_count}")
    
    duplicate_count = total_count - unique_count
    if duplicate_count > 0:
        print(f"⚠ 发现 {duplicate_count} 个重复关联")
        
        # 删除重复的关联（保留 ID 最小的）
        delete_query = """
        DELETE t1 FROM resource_author_rel t1
        INNER JOIN resource_author_rel t2 
        WHERE t1.id > t2.id 
        AND t1.resource_id = t2.resource_id 
        AND t1.author_id = t2.author_id
        """
        db.execute_query(delete_query)
        print(f"✓ 已删除 {duplicate_count} 个重复关联")
    else:
        print("✓ 没有重复关联")
    
    # 检查是否有关联到已删除资源的记录
    orphan_query = """
    SELECT COUNT(*) 
    FROM resource_author_rel rar
    LEFT JOIN resource r ON rar.resource_id = r.resource_id
    WHERE r.resource_id IS NULL OR r.deleted = 1
    """
    result = db.execute_query(orphan_query)
    orphan_count = result.fetchone()[0]
    
    if orphan_count > 0:
        print(f"⚠ 发现 {orphan_count} 个孤立关联（关联到已删除的资源）")
        
        # 删除孤立关联
        delete_orphan_query = """
        DELETE rar FROM resource_author_rel rar
        LEFT JOIN resource r ON rar.resource_id = r.resource_id
        WHERE r.resource_id IS NULL OR r.deleted = 1
        """
        db.execute_query(delete_orphan_query)
        print(f"✓ 已删除 {orphan_count} 个孤立关联")
    else:
        print("✓ 没有孤立关联")


def main():
    """主函数"""
    print("=" * 60)
    print("开始清理重复关联")
    print("=" * 60)
    
    try:
        db = DatabaseHelper()
        
        # 清理资源-分类关联
        clean_duplicate_category_relations(db)
        
        # 清理资源-作者关联
        clean_duplicate_author_relations(db)
        
        print("\n" + "=" * 60)
        print("✓ 所有清理完成！")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n✗ 清理失败: {e}")
        import traceback
        traceback.print_exc()
        return 1
    
    return 0


if __name__ == '__main__':
    sys.exit(main())
