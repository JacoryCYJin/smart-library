"""
清理作者名字脚本

清理 author 表和 resource 表中的作者/译者名字，
去掉国籍前缀（如 ［英］、[美]）和英文原名

@author JacoryCyJin
@date 2025/02/28
"""
import sys
import os
import re

# 添加父目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'crawler'))

from utils import DatabaseHelper


def clean_author_name(name):
    """
    清理作者名字，去掉国籍前缀和英文原名
    
    Args:
        name: 原始作者名字
    
    Returns:
        清理后的名字
    """
    if not name:
        return name
    
    # 步骤0: 先统一处理所有空格（包括全角空格、半角空格、不间断空格等）
    name = re.sub(r'[\s\u3000\xa0]+', ' ', name).strip()
    
    # 步骤1: 去掉国籍前缀
    # 匹配：[韩]、［英］、[美]、(英)、(英国)、【日】等
    pattern_prefix = r'^[\[［【\(（][^\]］】\)）]+[\]］】\)）]\s*'
    cleaned_name = re.sub(pattern_prefix, '', name).strip()
    
    # 步骤2: 去掉英文原名（括号中的内容）
    # 匹配：（Jessie Inchauspé）、(John Smith) 等
    pattern_english = r'[（\(][^）\)]+[）\)]'
    cleaned_name = re.sub(pattern_english, '', cleaned_name).strip()
    
    return cleaned_name if cleaned_name else name


def clean_author_table(db):
    """清理 author 表中的作者名字"""
    print("\n" + "=" * 60)
    print("清理 author 表")
    print("=" * 60)
    
    # 获取所有作者
    query = "SELECT author_id, name FROM author WHERE deleted = 0"
    result = db.execute_query(query)
    authors = [(row[0], row[1]) for row in result.fetchall()]
    
    print(f"共 {len(authors)} 个作者")
    
    updated_count = 0
    
    for author_id, name in authors:
        cleaned = clean_author_name(name)
        if cleaned != name:
            print(f"  更新: {name} → {cleaned}")
            update_query = "UPDATE author SET name = :name WHERE author_id = :author_id"
            db.execute_query(update_query, {'name': cleaned, 'author_id': author_id})
            updated_count += 1
    
    print(f"\n✓ author 表清理完成，更新了 {updated_count} 个作者")


def clean_resource_table(db):
    """清理 resource 表中的 author_name 和 translator_name"""
    print("\n" + "=" * 60)
    print("清理 resource 表")
    print("=" * 60)
    
    # 获取所有资源的作者和译者名字
    query = """
    SELECT resource_id, author_name, translator_name 
    FROM resource 
    WHERE deleted = 0 
    AND (author_name IS NOT NULL OR translator_name IS NOT NULL)
    """
    result = db.execute_query(query)
    resources = [(row[0], row[1], row[2]) for row in result.fetchall()]
    
    print(f"共 {len(resources)} 个资源")
    
    updated_count = 0
    
    for resource_id, author_name, translator_name in resources:
        # 清理作者名字
        cleaned_author = None
        if author_name:
            # 作者名字是逗号分隔的列表
            author_list = [clean_author_name(name.strip()) for name in author_name.split(',')]
            cleaned_author = ', '.join(author_list)
        
        # 清理译者名字
        cleaned_translator = None
        if translator_name:
            # 译者名字是逗号分隔的列表
            translator_list = [clean_author_name(name.strip()) for name in translator_name.split(',')]
            cleaned_translator = ', '.join(translator_list)
        
        # 检查是否需要更新
        need_update = False
        if author_name and cleaned_author != author_name:
            need_update = True
            print(f"  作者: {author_name} → {cleaned_author}")
        
        if translator_name and cleaned_translator != translator_name:
            need_update = True
            print(f"  译者: {translator_name} → {cleaned_translator}")
        
        if need_update:
            update_query = """
            UPDATE resource 
            SET author_name = :author_name, translator_name = :translator_name 
            WHERE resource_id = :resource_id
            """
            db.execute_query(update_query, {
                'author_name': cleaned_author,
                'translator_name': cleaned_translator,
                'resource_id': resource_id
            })
            updated_count += 1
    
    print(f"\n✓ resource 表清理完成，更新了 {updated_count} 个资源")


def main():
    """主函数"""
    print("=" * 60)
    print("开始清理作者名字")
    print("=" * 60)
    
    try:
        db = DatabaseHelper()
        
        # 清理 author 表
        clean_author_table(db)
        
        # 清理 resource 表
        clean_resource_table(db)
        
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
