"""
测试解读页爬虫（B站 + YouTube）

@author JacoryCyJin
@date 2026/03/10
"""
import sys
import os

# 添加父目录到路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from crawlers.review_crawler import ReviewCrawler
import json


def test_review_crawler():
    """测试解读页爬虫"""
    
    # 初始化爬虫
    crawler = ReviewCrawler()
    
    # 测试书籍
    test_books = [
        {
            'resource_id': 'test_001',
            'isbn': '9787020008735',
            'title': '活着'
        },
        {
            'resource_id': 'test_002',
            'isbn': '9787544270878',
            'title': '三体'
        },
        {
            'resource_id': 'test_003',
            'isbn': '9787020125814',
            'title': '百年孤独'
        }
    ]
    
    print("="*60)
    print("测试解读页爬虫（B站 + YouTube）")
    print("="*60)
    
    for book in test_books:
        print(f"\n{'='*60}")
        print(f"测试书籍: {book['title']}")
        print(f"{'='*60}")
        
        # 搜索解读视频
        links = crawler.search_links(
            resource_id=book['resource_id'],
            isbn=book['isbn'],
            title=book['title']
        )
        
        print(f"\n找到 {len(links)} 个解读视频:")
        
        for idx, link in enumerate(links, 1):
            print(f"\n{idx}. {link['title']}")
            print(f"   平台: {link['platform']} ({'B站' if link['platform'] == 3 else 'YouTube'})")
            print(f"   链接: {link['url']}")
            print(f"   描述: {link['description']}")
            print(f"   封面: {link.get('cover_url', '无')}")
            print(f"   排序: {link['sort_order']}")
        
        # 输出 JSON 格式
        print(f"\nJSON 格式:")
        print(json.dumps(links, ensure_ascii=False, indent=2))
        
        print("\n" + "="*60)
        input("按回车继续测试下一本书...")


if __name__ == '__main__':
    test_review_crawler()
