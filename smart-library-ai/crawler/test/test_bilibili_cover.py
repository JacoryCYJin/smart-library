"""
测试从 B站 和 YouTube 视频链接获取封面

用法:
    python test/test_bilibili_cover.py

@author JacoryCyJin
@date 2026/03/10
"""
import requests
import re
import json


def get_bilibili_cover_from_url(video_url: str) -> dict:
    """
    从 B站 视频链接获取封面
    
    Args:
        video_url: B站视频链接（如 https://www.bilibili.com/video/BV1xx411c7XZ/）
    
    Returns:
        dict: 包含封面信息的字典
    """
    try:
        # 1. 提取 BV 号
        bv_match = re.search(r'BV[\w]+', video_url)
        if not bv_match:
            return {'error': '无法提取 BV 号'}
        
        bvid = bv_match.group(0)
        print(f"✓ 提取到 BV 号: {bvid}")
        
        # 2. 调用 B站 API 获取视频信息
        api_url = f"https://api.bilibili.com/x/web-interface/view?bvid={bvid}"
        
        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Referer': 'https://www.bilibili.com'
        }
        
        print(f"✓ 请求 API: {api_url}")
        response = requests.get(api_url, headers=headers, timeout=10)
        response.raise_for_status()
        
        data = response.json()
        
        # 3. 检查响应
        if data.get('code') != 0:
            return {
                'error': f"API 返回错误: {data.get('message', '未知错误')}",
                'code': data.get('code')
            }
        
        # 4. 提取封面信息
        video_data = data.get('data', {})
        
        result = {
            'bvid': bvid,
            'title': video_data.get('title', ''),
            'cover': video_data.get('pic', ''),  # 封面 URL
            'author': video_data.get('owner', {}).get('name', ''),
            'view': video_data.get('stat', {}).get('view', 0),
            'duration': video_data.get('duration', 0),
            'pubdate': video_data.get('pubdate', 0)
        }
        
        return result
        
    except requests.RequestException as e:
        return {'error': f'网络请求失败: {e}'}
    except Exception as e:
        return {'error': f'解析失败: {e}'}


def get_youtube_cover_from_url(video_url: str) -> dict:
    """
    从 YouTube 视频链接获取封面（直接生成，无需 API）
    
    Args:
        video_url: YouTube视频链接
    
    Returns:
        dict: 包含封面信息的字典
    """
    try:
        # 1. 提取视频 ID
        video_id = None
        
        # 标准格式: youtube.com/watch?v=VIDEO_ID
        standard_match = re.search(r'[?&]v=([^&]+)', video_url)
        if standard_match:
            video_id = standard_match.group(1)
        else:
            # 短链接格式: youtu.be/VIDEO_ID
            short_match = re.search(r'youtu\.be\/([^?]+)', video_url)
            if short_match:
                video_id = short_match.group(1)
        
        if not video_id:
            return {'error': '无法提取视频 ID'}
        
        print(f"✓ 提取到视频 ID: {video_id}")
        
        # 2. 生成封面 URL（YouTube 提供多种分辨率）
        covers = {
            'maxresdefault': f"https://img.youtube.com/vi/{video_id}/maxresdefault.jpg",  # 1920x1080
            'sddefault': f"https://img.youtube.com/vi/{video_id}/sddefault.jpg",          # 640x480
            'hqdefault': f"https://img.youtube.com/vi/{video_id}/hqdefault.jpg",          # 480x360
            'mqdefault': f"https://img.youtube.com/vi/{video_id}/mqdefault.jpg",          # 320x180
            'default': f"https://img.youtube.com/vi/{video_id}/default.jpg"               # 120x90
        }
        
        # 3. 测试最高清封面是否存在
        print(f"✓ 测试封面可用性...")
        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        }
        
        response = requests.head(covers['maxresdefault'], headers=headers, timeout=10)
        
        if response.status_code == 200:
            best_cover = 'maxresdefault'
        else:
            # 如果高清封面不存在，使用标清
            best_cover = 'hqdefault'
        
        result = {
            'video_id': video_id,
            'cover': covers[best_cover],
            'cover_quality': best_cover,
            'all_covers': covers
        }
        
        return result
        
    except Exception as e:
        return {'error': f'解析失败: {e}'}


def test_multiple_videos():
    """测试多个视频链接"""
    
    print("=" * 80)
    print("测试从视频链接获取封面")
    print("=" * 80)
    print()
    
    # 测试 B站
    print("\n【B站视频测试】")
    print("=" * 80)
    bilibili_urls = [
        "https://www.bilibili.com/video/BV1xx411c7XZ/",
    ]
    
    for idx, url in enumerate(bilibili_urls, 1):
        print(f"\n测试 {idx}:")
        print(f"视频链接: {url}")
        print("-" * 80)
        
        result = get_bilibili_cover_from_url(url)
        
        if 'error' in result:
            print(f"❌ 失败: {result['error']}")
        else:
            print(f"✓ 标题: {result['title']}")
            print(f"✓ UP主: {result['author']}")
            print(f"✓ 播放量: {result['view']:,}")
            print(f"✓ 封面 URL: {result['cover']}")
        
        print()
    
    # 测试 YouTube
    print("\n【YouTube视频测试】")
    print("=" * 80)
    youtube_urls = [
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",  # 标准格式
        "https://youtu.be/dQw4w9WgXcQ",                 # 短链接格式
    ]
    
    for idx, url in enumerate(youtube_urls, 1):
        print(f"\n测试 {idx}:")
        print(f"视频链接: {url}")
        print("-" * 80)
        
        result = get_youtube_cover_from_url(url)
        
        if 'error' in result:
            print(f"❌ 失败: {result['error']}")
        else:
            print(f"✓ 视频 ID: {result['video_id']}")
            print(f"✓ 封面质量: {result['cover_quality']}")
            print(f"✓ 封面 URL: {result['cover']}")
            print(f"\n所有可用封面:")
            for quality, cover_url in result['all_covers'].items():
                print(f"  - {quality}: {cover_url}")
        
        print()


if __name__ == '__main__':
    test_multiple_videos()
