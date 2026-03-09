"""
测试电子书平台的 ISBN 搜索功能（实际访问测试）

只测试支持 ISBN 搜索的平台（不使用书名）

@author JacoryCyJin
@date 2026/03/09
"""
import urllib.parse
import requests
import time
from typing import Tuple


def test_url(url: str, platform_name: str, timeout: int = 10) -> Tuple[bool, str]:
    """
    测试 URL 是否可访问
    
    Args:
        url: 测试链接
        platform_name: 平台名称
        timeout: 超时时间（秒）
    
    Returns:
        (是否成功, 状态信息)
    """
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=timeout, allow_redirects=True)
        
        if response.status_code == 200:
            # 检查是否有实际内容（不是空页面）
            content_length = len(response.content)
            if content_length > 1000:  # 至少有 1KB 内容
                return True, f"✅ 可访问 (状态码: {response.status_code}, 内容: {content_length} bytes)"
            else:
                return False, f"⚠️  页面过小 (状态码: {response.status_code}, 内容: {content_length} bytes)"
        else:
            return False, f"❌ HTTP {response.status_code}"
            
    except requests.exceptions.SSLError as e:
        return False, f"❌ SSL 错误: {str(e)[:50]}"
    except requests.exceptions.Timeout:
        return False, f"❌ 超时 (>{timeout}秒)"
    except requests.exceptions.ConnectionError as e:
        return False, f"❌ 连接错误: {str(e)[:50]}"
    except Exception as e:
        return False, f"❌ 错误: {str(e)[:50]}"


def test_libgen_isbn_search():
    """测试 Library Genesis 的 ISBN 搜索"""
    isbn = "9787506365437"
    
    mirrors = [
        ("LibGen.li", f"https://libgen.li/index.php?req={urllib.parse.quote(isbn)}"),
        ("LibGen.is", f"https://libgen.is/search.php?req={urllib.parse.quote(isbn)}"),
        ("LibGen.rs", f"http://libgen.rs/search.php?req={urllib.parse.quote(isbn)}"),
    ]
    
    print("=" * 80)
    print("Library Genesis ISBN 搜索测试")
    print("=" * 80)
    print(f"测试 ISBN: {isbn}\n")
    
    for name, url in mirrors:
        print(f"测试 {name}...")
        print(f"  URL: {url}")
        success, status = test_url(url, name)
        print(f"  结果: {status}\n")
        time.sleep(1)  # 避免请求过快


def test_zlibrary_isbn_search():
    """测试 Z-Library 的 ISBN 搜索"""
    isbn = "9787506365437"
    
    urls = [
        ("Z-Library (zh)", f"https://zh.zlibrary.org/s/{urllib.parse.quote(isbn)}"),
        ("Z-Library (singlelogin)", f"https://singlelogin.re/s/{urllib.parse.quote(isbn)}"),
        ("Z-Library (z-lib.io)", f"https://z-lib.io/s/{urllib.parse.quote(isbn)}"),
    ]
    
    print("=" * 80)
    print("Z-Library ISBN 搜索测试")
    print("=" * 80)
    print(f"测试 ISBN: {isbn}\n")
    
    for name, url in urls:
        print(f"测试 {name}...")
        print(f"  URL: {url}")
        success, status = test_url(url, name)
        print(f"  结果: {status}\n")
        time.sleep(1)


def test_other_platforms():
    """测试其他可能支持 ISBN 搜索的平台"""
    isbn = "9787506365437"
    
    platforms = [
        ("Open Library", f"https://openlibrary.org/isbn/{isbn}"),
        ("Google Books", f"https://www.google.com/books/edition/_/{isbn}"),
        ("WorldCat", f"https://www.worldcat.org/isbn/{isbn}"),
    ]
    
    print("=" * 80)
    print("其他平台 ISBN 搜索测试")
    print("=" * 80)
    print(f"测试 ISBN: {isbn}\n")
    
    for name, url in platforms:
        print(f"测试 {name}...")
        print(f"  URL: {url}")
        success, status = test_url(url, name)
        print(f"  结果: {status}\n")
        time.sleep(1)


def print_summary():
    """打印测试总结"""
    print("=" * 80)
    print("测试说明")
    print("=" * 80)
    print("✅ = 可访问且有内容，可以使用")
    print("⚠️  = 可访问但内容异常，需要手动验证")
    print("❌ = 无法访问或出错，不建议使用")
    print()
    print("注意：")
    print("1. 所有测试都使用 ISBN 搜索（不使用书名）")
    print("2. 自动测试只能验证链接是否可访问，无法判断搜索结果是否正确")
    print("3. 建议对标记为 ✅ 的链接在浏览器中手动验证一次")
    print("=" * 80)


if __name__ == "__main__":
    print("\n开始测试电子书平台 ISBN 搜索功能...\n")
    
    test_libgen_isbn_search()
    test_zlibrary_isbn_search()
    test_other_platforms()
    print_summary()
