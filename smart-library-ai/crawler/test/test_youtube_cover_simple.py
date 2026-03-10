"""
简单测试 YouTube 封面是否可用

用法:
    python test/test_youtube_cover_simple.py
"""
import requests

# 测试几个真实的 YouTube 视频
test_videos = [
    {
        'url': 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
        'video_id': 'dQw4w9WgXcQ',
        'title': 'Rick Astley - Never Gonna Give You Up'
    },
    {
        'url': 'https://www.youtube.com/watch?v=9bZkp7q19f0',
        'video_id': '9bZkp7q19f0',
        'title': 'PSY - GANGNAM STYLE'
    },
    {
        'url': 'https://youtu.be/kJQP7kiw5Fk',
        'video_id': 'kJQP7kiw5Fk',
        'title': 'Luis Fonsi - Despacito'
    }
]

print("=" * 80)
print("测试 YouTube 封面直接生成")
print("=" * 80)
print()

for idx, video in enumerate(test_videos, 1):
    print(f"【测试 {idx}】")
    print(f"视频: {video['title']}")
    print(f"URL: {video['url']}")
    print("-" * 80)
    
    # 生成封面 URL
    cover_url = f"https://img.youtube.com/vi/{video['video_id']}/maxresdefault.jpg"
    print(f"封面 URL: {cover_url}")
    
    # 测试封面是否可访问
    try:
        response = requests.head(cover_url, timeout=10)
        if response.status_code == 200:
            print(f"✓ 封面可用 (状态码: {response.status_code})")
            print(f"✓ 你可以在浏览器中打开这个链接查看封面:")
            print(f"  {cover_url}")
        else:
            print(f"✗ 封面不可用 (状态码: {response.status_code})")
            # 尝试标清封面
            hq_cover = f"https://img.youtube.com/vi/{video['video_id']}/hqdefault.jpg"
            print(f"  尝试标清封面: {hq_cover}")
    except Exception as e:
        print(f"✗ 请求失败: {e}")
    
    print()

print("=" * 80)
print("总结:")
print("YouTube 封面 URL 格式:")
print("  高清: https://img.youtube.com/vi/{VIDEO_ID}/maxresdefault.jpg")
print("  标清: https://img.youtube.com/vi/{VIDEO_ID}/hqdefault.jpg")
print("=" * 80)
