"""
解读视频爬虫（B站 + YouTube，纯网页爬取）

@author JacoryCyJin
@date 2026/03/08
"""
import requests
import time
import logging
import re
import json
import urllib.parse
from typing import List, Dict, Optional
from bs4 import BeautifulSoup
from .base_link_crawler import BaseLinkCrawler

logger = logging.getLogger(__name__)


class ReviewCrawler(BaseLinkCrawler):
    """解读视频爬虫（B站 + YouTube，纯网页爬取）"""
    
    # 播放量阈值（过滤低质量视频）
    MIN_PLAY_COUNT_BILIBILI = 10000   # B站最低1万播放
    MIN_VIEW_COUNT_YOUTUBE = 1000     # YouTube最低1千观看
    
    # 每个平台返回的视频数量
    MAX_BILIBILI_VIDEOS = 4  # B站返回4个
    MAX_YOUTUBE_VIDEOS = 2   # YouTube返回2个
    
    # 搜索关键词后缀
    BILIBILI_KEYWORDS_SUFFIX = ['解读', '书评', '推荐']
    YOUTUBE_KEYWORDS_SUFFIX = ['review', 'book review']
    
    def __init__(self):
        # 初始化基类：link_type=3(解读页), platform=3(B站)
        super().__init__(link_type=self.LINK_TYPE_REVIEW, platform=self.PLATFORM_BILIBILI)
    
    def search_links(self, resource_id: str, isbn: Optional[str] = None, 
                    title: Optional[str] = None) -> List[Dict]:
        """
        搜索解读视频链接（B站 + YouTube，使用书名搜索）
        
        说明：
        - 使用书名而非ISBN搜索，因为不同版本的书内容相同，解读视频通用
        - B站返回4个，YouTube返回2个，共6个视频
        
        Args:
            resource_id: 资源ID
            isbn: ISBN号（不使用）
            title: 标题（必需）
        
        Returns:
            List[Dict]: 解读视频链接列表（B站4个 + YouTube2个 = 共6个）
        """
        # 使用书名搜索（不用ISBN，因为不同版本内容相同）
        if not title:
            logger.warning(f"资源 {resource_id} 缺少标题，无法搜索解读视频")
            return []
        
        search_keyword = title
        
        try:
            all_links = []
            
            # 1. 搜索 B站视频（网页爬取）
            bilibili_links = self._search_bilibili_videos(search_keyword)
            all_links.extend(bilibili_links)
            
            time.sleep(2)
            
            # 2. 搜索 YouTube 视频（网页爬取）
            youtube_links = self._search_youtube_videos(search_keyword)
            all_links.extend(youtube_links)
            
            # 3. 每个平台只保留播放量最高的视频（B站4个，YouTube2个）
            bilibili_links_filtered = sorted(
                [link for link in all_links if link['platform'] == self.PLATFORM_BILIBILI],
                key=lambda x: x.get('_play_count', 0),
                reverse=True
            )[:self.MAX_BILIBILI_VIDEOS]  # B站取4个
            
            youtube_links_filtered = sorted(
                [link for link in all_links if link['platform'] == self.PLATFORM_YOUTUBE],
                key=lambda x: x.get('_play_count', 0),
                reverse=True
            )[:self.MAX_YOUTUBE_VIDEOS]  # YouTube取2个
            
            # 4. 合并并按播放量排序
            final_links = bilibili_links_filtered + youtube_links_filtered
            final_links.sort(key=lambda x: x.get('_play_count', 0), reverse=True)
            
            # 5. 移除内部字段并设置 sort_order
            for idx, link in enumerate(final_links):
                link.pop('_play_count', None)
                link.pop('_bvid', None)
                link['sort_order'] = idx + 1
            
            return final_links  # 最多6个（B站4个+YouTube2个）
            
        except Exception as e:
            logger.error(f"搜索解读视频失败 (resource_id={resource_id}): {e}")
            return []
    
    def _search_bilibili_videos(self, keyword: str) -> List[Dict]:
        """
        搜索 B站视频（网页爬取，只返回高播放量视频）
        
        Args:
            keyword: 搜索关键词
        
        Returns:
            List[Dict]: B站视频链接列表
        """
        try:
            # 构造搜索关键词
            keywords = [f"{keyword} {suffix}" for suffix in self.BILIBILI_KEYWORDS_SUFFIX]
            all_videos = []
            
            for search_keyword in keywords:
                encoded_keyword = urllib.parse.quote(search_keyword)
                search_url = f"https://search.bilibili.com/all?keyword={encoded_keyword}"
                
                try:
                    headers = {
                        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                        'Referer': 'https://www.bilibili.com',
                        'Accept-Language': 'zh-CN,zh;q=0.9'
                    }
                    
                    response = requests.get(search_url, headers=headers, timeout=15)
                    response.raise_for_status()
                    
                    videos = self._parse_bilibili_search_page(response.text)
                    all_videos.extend(videos)
                    time.sleep(2)
                    
                except Exception as e:
                    logger.warning(f"搜索 B站关键词 '{search_keyword}' 失败: {e}")
                    continue
            
            # 去重并过滤
            unique_videos = self._deduplicate_by_bvid(all_videos)
            filtered_videos = [
                v for v in unique_videos 
                if v.get('_play_count', 0) >= self.MIN_PLAY_COUNT_BILIBILI
            ]
            
            logger.info(f"B站：找到 {len(unique_videos)} 个视频，过滤后 {len(filtered_videos)} 个")
            return filtered_videos
            
        except Exception as e:
            logger.error(f"搜索 B站视频失败: {e}")
            return []
    
    def _parse_bilibili_search_page(self, html: str) -> List[Dict]:
        """
        解析 B站搜索结果页面
        
        Args:
            html: 页面HTML
        
        Returns:
            List[Dict]: 视频列表
        """
        try:
            soup = BeautifulSoup(html, 'html.parser')
            video_cards = soup.find_all('div', class_='bili-video-card')
            
            if not video_cards:
                logger.warning("未找到 B站视频卡片")
                return []
            
            videos = []
            
            for card in video_cards[:6]:  # 最多取6个
                try:
                    # 提取链接和 bvid
                    link_tag = card.find('a', href=re.compile(r'/video/BV'))
                    if not link_tag:
                        continue
                    
                    url = link_tag.get('href', '')
                    if url.startswith('//'):
                        url = 'https:' + url
                    
                    bvid_match = re.search(r'BV[\w]+', url)
                    bvid = bvid_match.group(0) if bvid_match else None
                    if not bvid:
                        continue
                    
                    # 提取标题
                    title_tag = card.find('h3', class_='bili-video-card__info--tit')
                    if not title_tag:
                        continue
                    video_title = title_tag.get('title', '')
                    
                    # 提取播放量
                    stats_items = card.find_all('span', class_='bili-video-card__stats--item')
                    play_count = 0
                    if stats_items:
                        play_text = stats_items[0].find('span')
                        if play_text:
                            play_count = self._parse_count(play_text.text.strip())
                    
                    # 提取 UP主
                    author_tag = card.find('span', class_='bili-video-card__info--author')
                    author = author_tag.text.strip() if author_tag else '未知UP主'
                    
                    videos.append({
                        'platform': self.PLATFORM_BILIBILI,
                        'url': url,
                        'title': video_title,
                        'description': f'UP主：{author}',
                        '_play_count': play_count,
                        '_bvid': bvid,
                        'sort_order': 0
                    })
                    
                except Exception as e:
                    logger.debug(f"解析单个视频卡片失败: {e}")
                    continue
            
            return videos
            
        except Exception as e:
            logger.error(f"解析 B站页面失败: {e}")
            return []
    
    def _search_youtube_videos(self, keyword: str) -> List[Dict]:
        """
        搜索 YouTube 视频（网页爬取，只返回高观看量视频）
        
        Args:
            keyword: 搜索关键词
        
        Returns:
            List[Dict]: YouTube视频链接列表
        """
        try:
            # 构造搜索关键词
            keywords = [f"{keyword} {suffix}" for suffix in self.YOUTUBE_KEYWORDS_SUFFIX]
            all_videos = []
            
            for search_keyword in keywords:
                encoded_keyword = urllib.parse.quote(search_keyword)
                search_url = f"https://www.youtube.com/results?search_query={encoded_keyword}"
                
                try:
                    headers = {
                        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                        'Accept-Language': 'en-US,en;q=0.9',
                        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'
                    }
                    
                    response = requests.get(search_url, headers=headers, timeout=15)
                    response.raise_for_status()
                    
                    videos = self._parse_youtube_search_page(response.text)
                    all_videos.extend(videos)
                    time.sleep(2)
                    
                except Exception as e:
                    logger.warning(f"搜索 YouTube 关键词 '{search_keyword}' 失败: {e}")
                    continue
            
            # 过滤低观看量视频
            filtered_videos = [
                v for v in all_videos 
                if v.get('_play_count', 0) >= self.MIN_VIEW_COUNT_YOUTUBE
            ]
            
            logger.info(f"YouTube：找到 {len(all_videos)} 个视频，过滤后 {len(filtered_videos)} 个")
            return filtered_videos
            
        except Exception as e:
            logger.error(f"搜索 YouTube 视频失败: {e}")
            return []
    
    def _parse_youtube_search_page(self, html: str) -> List[Dict]:
        """
        解析 YouTube 搜索结果页面
        
        Args:
            html: 页面HTML
        
        Returns:
            List[Dict]: 视频列表
        """
        try:
            # 从页面中提取 ytInitialData
            match = re.search(r'var ytInitialData = ({.*?});', html)
            if not match:
                logger.warning("未找到 YouTube 页面数据")
                return []
            
            data = json.loads(match.group(1))
            
            # 导航到视频列表
            contents = (data.get('contents', {})
                       .get('twoColumnSearchResultsRenderer', {})
                       .get('primaryContents', {})
                       .get('sectionListRenderer', {})
                       .get('contents', []))
            
            videos = []
            
            for section in contents:
                items = section.get('itemSectionRenderer', {}).get('contents', [])
                
                for item in items:
                    video_renderer = item.get('videoRenderer', {})
                    if not video_renderer:
                        continue
                    
                    video_id = video_renderer.get('videoId')
                    if not video_id:
                        continue
                    
                    # 提取标题
                    title_runs = video_renderer.get('title', {}).get('runs', [])
                    video_title = ''.join([run.get('text', '') for run in title_runs])
                    
                    # 提取频道名
                    channel_name = (video_renderer.get('ownerText', {})
                                   .get('runs', [{}])[0]
                                   .get('text', '未知频道'))
                    
                    # 提取观看量
                    view_count_text = (video_renderer.get('viewCountText', {})
                                      .get('simpleText', '0'))
                    view_count = self._parse_count(view_count_text)
                    
                    videos.append({
                        'platform': self.PLATFORM_YOUTUBE,
                        'url': f"https://www.youtube.com/watch?v={video_id}",
                        'title': video_title,
                        'description': f'频道：{channel_name}',
                        '_play_count': view_count,
                        'sort_order': 0
                    })
                    
                    if len(videos) >= 3:
                        break
                
                if len(videos) >= 3:
                    break
            
            return videos
            
        except Exception as e:
            logger.error(f"解析 YouTube 页面失败: {e}")
            return []
    
    def _parse_count(self, text: str) -> int:
        """
        解析播放量/观看量文本为数字（统一处理B站和YouTube格式）
        
        Args:
            text: 播放量文本（如 "1.2万", "50K views", "1.2M views"）
        
        Returns:
            int: 播放量数字
        """
        try:
            # 移除常见文字
            text = text.lower().replace('views', '').replace('次观看', '').strip()
            
            # 匹配数字和单位
            match = re.search(r'([\d.]+)\s*([kmb万亿]?)', text)
            if not match:
                return 0
            
            number = float(match.group(1))
            unit = match.group(2).lower()
            
            # 转换单位
            multipliers = {
                'k': 1000,
                'm': 1000000,
                'b': 1000000000,
                '万': 10000,
                '亿': 100000000
            }
            
            multiplier = multipliers.get(unit, 1)
            return int(number * multiplier)
            
        except:
            return 0
    
    def _deduplicate_by_bvid(self, videos: List[Dict]) -> List[Dict]:
        """
        基于 bvid 去重 B站视频
        
        Args:
            videos: 视频列表
        
        Returns:
            List[Dict]: 去重后的视频列表
        """
        seen_bvids = set()
        unique_videos = []
        
        for video in videos:
            bvid = video.get('_bvid')
            if bvid and bvid not in seen_bvids:
                seen_bvids.add(bvid)
                unique_videos.append(video)
        
        return unique_videos
