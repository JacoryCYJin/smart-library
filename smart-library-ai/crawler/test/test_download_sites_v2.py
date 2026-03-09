"""
测试电子书下载站点 - 验证ISBN vs 书名搜索

@author JacoryCyJin
@date 2026/03/09
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import requests
import time
from urllib.parse import quote


def test_search(url, search_type, keyword):
    """测试搜索"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
            'Accept-Language': 'zh-CN,zh;q=0.9'
        }
        
        response = requests.get(url, headers=headers, timeout=10)
        
        if response.status_code == 200:
            # 检查是否有"没有找到"、"not found"等提示
            text_lower = response.text.lower()
            
            has_no_result = any([
                '没有找到' in response.text,
                'not found' in text_lower,
                'no results' in text_lower,
                '0 results' in text_lower
            ])
            
            if has_no_result:
                print(f"  ✗ {search_type}搜索: 没有结果")
                return False
            else:
                print(f"  ✓ {search_type}搜索: 有结果")
                return True
        else:
            print(f"  ✗ {search_type}搜索: HTTP {response.status_code}")
            return False
            
    except Exception as e:
        print(f"  ✗ {search_type}搜索: {e}")
        return False


def test_libgen():
    """测试 Library Genesis"""
    print("\n" + "="*60)
    print("测试 Library Genesis")
    print("="*60)
    
    isbn = "9787506365437"
    title = "活着"
    
    print(f"\n测试图书: {title} (ISBN: {isbn})")
    
    # 测试ISBN搜索
    isbn_url = f"https://libgen.li/index.php?req={quote(isbn)}"
    print(f"\nISBN搜索: {isbn_url}")
    isbn_result = test_search(isbn_url, "ISBN", isbn)
    
    time.sleep(2)
    
    # 测试书名搜索
    title_url = f"https://libgen.li/index.php?req={quote(title)}"
    print(f"\n书名搜索: {title_url}")
    title_result = test_search(title_url, "书名", title)
    
    print(f"\n推荐: {'ISBN搜索' if isbn_result else '书名搜索' if title_result else '都不可用'}")
    return isbn_result, title_result


def test_jiumo():
    """测试鸠摩搜书"""
    print("\n" + "="*60)
    print("测试鸠摩搜书")
    print("="*60)
    
    isbn = "9787506365437"
    title = "活着"
    
    print(f"\n测试图书: {title} (ISBN: {isbn})")
    
    # 测试ISBN搜索
    isbn_url = f"https://www.jiumodiary.com/?s={quote(isbn)}"
    print(f"\nISBN搜索: {isbn_url}")
    isbn_result = test_search(isbn_url, "ISBN", isbn)
    
    time.sleep(2)
    
    # 测试书名搜索
    title_url = f"https://www.jiumodiary.com/?s={quote(title)}"
    print(f"\n书名搜索: {title_url}")
    title_result = test_search(title_url, "书名", title)
    
    print(f"\n推荐: {'ISBN搜索' if isbn_result else '书名搜索' if title_result else '都不可用'}")
    return isbn_result, title_result


def main():
    """主测试"""
    print("\n" + "="*60)
    print("电子书站点搜索方式测试（ISBN vs 书名）")
    print("="*60)
    
    # 测试 Library Genesis
    libgen_isbn, libgen_title = test_libgen()
    time.sleep(3)
    
    # 测试鸠摩搜书
    jiumo_isbn, jiumo_title = test_jiumo()
    
    # 总结
    print("\n\n" + "="*60)
    print("测试结果汇总")
    print("="*60)
    
    print("\nLibrary Genesis:")
    print(f"  - ISBN搜索: {'✓ 可用' if libgen_isbn else '✗ 不可用'}")
    print(f"  - 书名搜索: {'✓ 可用' if libgen_title else '✗ 不可用'}")
    print(f"  - 推荐方式: {'ISBN' if libgen_isbn else '书名' if libgen_title else '都不可用'}")
    
    print("\n鸠摩搜书:")
    print(f"  - ISBN搜索: {'✓ 可用' if jiumo_isbn else '✗ 不可用'}")
    print(f"  - 书名搜索: {'✓ 可用' if jiumo_title else '✗ 不可用'}")
    print(f"  - 推荐方式: {'ISBN' if jiumo_isbn else '书名' if jiumo_title else '都不可用'}")
    
    print("\n" + "="*60)


if __name__ == '__main__':
    main()
