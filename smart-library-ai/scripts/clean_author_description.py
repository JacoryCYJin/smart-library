#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清理作者简介中的引用标注 [xx]

Usage:
    python clean_author_description.py [--dry-run]
    
Options:
    --dry-run    只显示将要修改的内容，不实际修改数据库
"""

import re
import sys
import os
import pymysql

# 添加父目录到路径，以便导入 crawler 模块
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'crawler'))

from config import Config


def get_db_connection():
    """获取数据库连接"""
    return pymysql.connect(
        host=Config.DB_HOST,
        port=Config.DB_PORT,
        user=Config.DB_USER,
        password=Config.DB_PASSWORD,
        database=Config.DB_NAME,
        charset='utf8mb4',
        cursorclass=pymysql.cursors.Cursor
    )


def clean_description(text):
    """
    清理文本中的引用标注
    
    Args:
        text: 原始文本
        
    Returns:
        清理后的文本
    """
    if not text:
        return text
    
    # 移除 [数字] 或 [数字-数字] 格式的引用标注
    # 例如: [3], [4-5], [95], [99]
    cleaned = re.sub(r'\[\d+(?:-\d+)?\]', '', text)
    
    # 移除可能的多余空格
    cleaned = re.sub(r'\s+', ' ', cleaned)
    
    return cleaned.strip()


def main():
    """主函数"""
    # 检查是否为 dry-run 模式
    dry_run = '--dry-run' in sys.argv
    
    if dry_run:
        print("=" * 60)
        print("🔍 DRY-RUN 模式：只显示将要修改的内容，不会实际修改数据库")
        print("=" * 60)
        print()
    
    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        # 查询所有有简介的作者
        cursor.execute("""
            SELECT author_id, name, description 
            FROM author 
            WHERE description IS NOT NULL 
            AND description != ''
            AND description LIKE '%[%]%'
            ORDER BY author_id
        """)
        
        authors = cursor.fetchall()
        total = len(authors)
        
        if total == 0:
            print("✓ 没有需要清理的作者简介")
            return
        
        print(f"找到 {total} 个需要清理的作者简介")
        print("-" * 60)
        
        # 显示前 3 个示例
        print("\n📋 清理示例（前 3 个）：\n")
        for i, (author_id, name, description) in enumerate(authors[:3]):
            cleaned_description = clean_description(description)
            print(f"示例 {i+1}: {name} (ID: {author_id})")
            print(f"  原文: {description[:100]}{'...' if len(description) > 100 else ''}")
            print(f"  清理后: {cleaned_description[:100]}{'...' if len(cleaned_description) > 100 else ''}")
            print()
        
        # 询问用户确认
        if not dry_run:
            print("-" * 60)
            response = input(f"\n⚠️  确认要更新 {total} 个作者简介吗？(yes/no): ").strip().lower()
            
            if response not in ['yes', 'y']:
                print("❌ 操作已取消")
                return
            
            print()
        
        updated_count = 0
        
        for author_id, name, description in authors:
            # 清理简介
            cleaned_description = clean_description(description)
            
            # 如果清理后有变化，则更新数据库
            if cleaned_description != description:
                if not dry_run:
                    cursor.execute("""
                        UPDATE author 
                        SET description = %s 
                        WHERE author_id = %s
                    """, (cleaned_description, author_id))
                
                updated_count += 1
                
                if dry_run:
                    print(f"[DRY-RUN] 将清理: {name} (ID: {author_id})")
                else:
                    print(f"[{updated_count}/{total}] 已清理: {name} (ID: {author_id})")
        
        if not dry_run:
            # 提交事务
            conn.commit()
            print()
            print("-" * 60)
            print(f"✓ 清理完成！共更新 {updated_count} 个作者简介")
        else:
            print()
            print("-" * 60)
            print(f"🔍 DRY-RUN 完成！将会更新 {updated_count} 个作者简介")
            print(f"💡 运行 'python {os.path.basename(__file__)}' 执行实际更新")
        
    except Exception as e:
        if not dry_run:
            conn.rollback()
            print(f"✗ 清理失败，已回滚: {e}")
        else:
            print(f"✗ DRY-RUN 失败: {e}")
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == '__main__':
    main()
