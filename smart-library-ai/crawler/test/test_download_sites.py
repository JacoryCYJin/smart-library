"""
测试电子书下载站点的搜索链接

实际访问各个网站，验证搜索链接是否正确

@author JacoryCyJin
@date 2026/03/09
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import requests
import time
from urllib.parse import quote


def test_annas_archive(isbn):
    """测试 Anna's Archive"""
    print("\n" + "="*60)
    print("测试 Anna's Archive")
    print("="*60)
    
    # 尝试多个可能的域名
    domains = [
        "https://annas-archive.org",
        "https://annas-archive.se",
        "https://annas-archive.gs",
    ]
    
    for domain in domains:
        try:
            url = f"{domain}/search?q={quote(isbn)}"
            print(f"\n尝试: {url}")
            
            headers = {
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
            }
            
            response = requests.get(url, headers=headers, timeout=10)
            print(f"  状态码: {response.status_code}")
            
            if response.status_code == 200:
                # 检查是否有搜索结果
                if 'search' in response.text.lower() or isbn in response.text:
                    print(f"  ✓ 成功！找到搜索结果")
                    print(f"  推荐URL: {url}")
                    return url
                else:
                    print(f"  ⚠ 页面加载成功，但未找到搜索结果")
            
        except Exception as e:
            print(f"  ✗ 失败: {e}")
    
    print("\n  ⚠ 所有域名都无法访问")
    return None


def test_libgen(isbn):
    """测试 Library Genesis"""
    print("\n" + "="*60)
    print("测试 Library Genesis")
    print("="*60)
    
    # 尝试多个可能的搜索路径
    test_urls = [
        f"https://libgen.is/search.php?req={quote(isbn)}",
        f"http://libgen.rs/search.php?req={quote(isbn)}",
        f"https://libgen.li/index.php?req={quote(isbn)}",
    ]
    
    for url in test_urls:
        try:
            print(f"\n尝试: {url}")
            
            headers = {
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
            }
            
            response = requests.get(url, headers=headers, timeout=10)
            print(f"  状态码: {response.status_code}")
            
            if response.status_code == 200:
                # 检查是否有搜索结果
                if 'search' in response.text.lower() or isbn in response.text:
                    print(f"  ✓ 成功！找到搜索结果")
                    print(f"  推荐URL: {url}")
                    return url
                else:
                    print(f"  ⚠ 页面加载成功，但未找到搜索结果")
            
        except Exception as e:
            print(f"  ✗ 失败: {e}")
    
    print("\n  ⚠ 所有URL都无法访问")
    return None


def test_jiumo(isbn):
    """测试鸠摩搜书"""
    print("\n" + "="*60)
    print("测试鸠摩搜书")
    print("="*60)
    
    # 尝试多个可能的搜索路径
    test_urls = [
        f"https://www.jiumodiary.com/?s={quote(isbn)}",
        f"https://www.jiumodiary.com/search.php?q={quote(isbn)}",
    ]
    
    for url in test_urls:
        try:
            print(f"\n尝试: {url}")
            
            headers = {
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
                'Accept-Language': 'zh-CN,zh;q=0.9'
            }
            
            response = requests.get(url, headers=headers, timeout=10)
            print(f"  状态码: {response.status_code}")
            
            if response.status_code == 200:
                # 检查是否有搜索结果
                if '搜索' in response.text or isbn in response.text:
                    print(f"  ✓ 成功！找到搜索结果")
                    print(f"  推荐URL: {url}")
                    return url
                else:
                    print(f"  ⚠ 页面加载成功，但未找到搜索结果")
            
        except Exception as e:
            print(f"  ✗ 失败: {e}")
    
    print("\n  ⚠ 所有URL都无法访问")
    return None


def main():
    """主测试函数"""
    print("\n" + "="*60)
    print("电子书下载站点搜索链接测试")
    print("="*60)
    
    # 测试用例
    test_cases = [
        {
            'name': '活着',
            'isbn': '9787506365437'
        },
        {
            'name': '红楼梦',
            'isbn': '9787020002207'
        }
    ]
    
    results = {
        'annas_archive': None,
        'libgen': None,
        'jiumo': None
    }
    
    for test_case in test_cases:
        print(f"\n\n{'#'*60}")
        print(f"测试图书: {test_case['name']} (ISBN: {test_case['isbn']})")
        print(f"{'#'*60}")
        
        # 测试 Anna's Archive
        if not results['annas_archive']:
            results['annas_archive'] = test_annas_archive(test_case['isbn'])
            time.sleep(2)
        
        # 测试 Library Genesis
        if not results['libgen']:
            results['libgen'] = test_libgen(test_case['isbn'])
            time.sleep(2)
        
        # 测试鸠摩搜书
        if not results['jiumo']:
            results['jiumo'] = test_jiumo(test_case['isbn'])
            time.sleep(2)
        
        # 如果都找到了，就不用继续测试了
        if all(results.values()):
            break
    
    # 输出最终结果
    print("\n\n" + "="*60)
    print("测试结果汇总")
    print("="*60)
    
    print("\n推荐的URL格式：")
    if results['annas_archive']:
        print(f"\n1. Anna's Archive:")
        print(f"   {results['annas_archive']}")
    else:
        print(f"\n1. Anna's Archive: ⚠ 无法访问")
    
    if results['libgen']:
        print(f"\n2. Library Genesis:")
        print(f"   {results['libgen']}")
    else:
        print(f"\n2. Library Genesis: ⚠ 无法访问")
    
    if results['jiumo']:
        print(f"\n3. 鸠摩搜书:")
        print(f"   {results['jiumo']}")
    else:
        print(f"\n3. 鸠摩搜书: ⚠ 无法访问")
    
    print("\n" + "="*60)


if __name__ == '__main__':
    main()
