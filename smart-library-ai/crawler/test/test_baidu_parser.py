"""
百度百科解析测试脚本

用于测试百度百科页面解析逻辑，验证能否正确提取作者信息

@author JacoryCyJin
@date 2025/03/02
"""
import requests
from bs4 import BeautifulSoup
from urllib.parse import quote
import logging
import json

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def extract_baidu_json_data(soup):
    """
    从百度百科页面提取 JSON 数据
    
    Args:
        soup: BeautifulSoup 对象
    
    Returns:
        dict: 解析后的 JSON 数据，失败返回 None
    """
    try:
        # 查找包含 window.PAGE_DATA 的 script 标签
        scripts = soup.find_all('script')
        for script in scripts:
            if script.string and 'window.PAGE_DATA' in script.string:
                # 提取 JSON 数据
                script_text = script.string
                # 找到 window.PAGE_DATA= 后面的 JSON
                start_idx = script_text.find('window.PAGE_DATA=')
                if start_idx == -1:
                    continue
                
                # 跳过 'window.PAGE_DATA='
                start_idx += len('window.PAGE_DATA=')
                
                # 找到 JSON 结束位置（使用括号匹配）
                json_str = script_text[start_idx:].strip()
                
                # 移除末尾的分号和其他内容
                brace_count = 0
                in_string = False
                escape = False
                for i, char in enumerate(json_str):
                    if escape:
                        escape = False
                        continue
                    if char == '\\':
                        escape = True
                        continue
                    if char == '"' and not escape:
                        in_string = not in_string
                    if not in_string:
                        if char == '{':
                            brace_count += 1
                        elif char == '}':
                            brace_count -= 1
                            if brace_count == 0:
                                json_str = json_str[:i+1]
                                break
                
                # 解析 JSON
                data = json.loads(json_str)
                return data
        
        return None
    except Exception as e:
        logger.error(f"提取百度百科 JSON 数据失败: {e}")
        import traceback
        logger.error(traceback.format_exc())
        return None


def normalize_country_name(country):
    """
    统一国家名称
    
    Args:
        country: 原始国家名称
    
    Returns:
        str: 标准化后的国家名称
    """
    if not country:
        return country
    
    # 国家名称映射表
    country_mapping = {
        '中华人民共和国': '中国',
        '中華人民共和國': '中国',
        'People\'s Republic of China': '中国',
        'PRC': '中国',
        '美利坚合众国': '美国',
        'United States of America': '美国',
        'USA': '美国',
        '大不列颠及北爱尔兰联合王国': '英国',
        'United Kingdom': '英国',
        'UK': '英国',
        '俄罗斯联邦': '俄罗斯',
        'Russian Federation': '俄罗斯',
        '大韩民国': '韩国',
        'Republic of Korea': '韩国',
        'South Korea': '韩国',
    }
    
    # 查找映射
    for key, value in country_mapping.items():
        if key in country:
            return value
    
    return country


def test_baidu_author(author_name):
    """
    测试百度百科作者信息提取
    
    Args:
        author_name: 作者姓名
    """
    logger.info("=" * 60)
    logger.info(f"测试百度百科解析: {author_name}")
    logger.info("=" * 60)
    
    try:
        # 创建 Session
        session = requests.Session()
        session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
            'Connection': 'keep-alive'
        })
        
        # 构建 URL
        search_url = f"https://baike.baidu.com/item/{quote(author_name)}"
        logger.info(f"访问 URL: {search_url}")
        
        # 发送请求
        response = session.get(search_url, timeout=10)
        
        if response.status_code == 404:
            logger.warning(f"百度百科未找到: {author_name}")
            return
        
        response.raise_for_status()
        logger.info(f"✓ 页面请求成功 (状态码: {response.status_code})")
        
        # 解析 HTML
        soup = BeautifulSoup(response.text, 'lxml')
        
        # 提取页面标题
        title = soup.select_one('title')
        if title:
            logger.info(f"页面标题: {title.get_text(strip=True)}")
        
        # ========== 测试 JSON 数据提取 ==========
        logger.info("\n" + "=" * 60)
        logger.info("测试 JSON 数据提取")
        logger.info("=" * 60)
        
        json_data = extract_baidu_json_data(soup)
        
        if json_data:
            logger.info("✓ 成功提取 JSON 数据")
            
            # 提取结果汇总
            extracted_data = {
                'description': None,
                'original_name': None,
                'country': None,
                'photo_url': None
            }
            
            # 测试提取简介
            if 'description' in json_data:
                description = json_data['description']
                extracted_data['description'] = description
                logger.info(f"\n✓ 简介: {description[:100]}...")
            else:
                logger.warning("\n⚠ 未找到简介字段")
            
            # 测试提取基本信息卡片
            if 'card' in json_data:
                card = json_data['card']
                logger.info(f"\n基本信息卡片:")
                
                # 合并 left 和 right
                all_items = []
                if 'left' in card:
                    all_items.extend(card['left'])
                if 'right' in card:
                    all_items.extend(card['right'])
                
                logger.info(f"共 {len(all_items)} 个字段")
                
                for item in all_items:
                    key = item.get('key', '')
                    title_text = item.get('title', '')
                    data_list = item.get('data', [])
                    
                    if not data_list:
                        continue
                    
                    # 提取值
                    value_parts = []
                    for data_item in data_list:
                        if 'text' in data_item:
                            text_parts = data_item['text']
                            for text_part in text_parts:
                                if isinstance(text_part, dict) and 'text' in text_part:
                                    value_parts.append(text_part['text'])
                                elif isinstance(text_part, str):
                                    value_parts.append(text_part)
                    
                    value = ''.join(value_parts)
                    
                    if value:
                        logger.info(f"  {title_text} ({key}): {value}")
                        
                        # 匹配外文名/原名
                        if key == 'foreignName' or any(keyword in title_text for keyword in ['外文名', '原名', '本名', '别名', '英文名']):
                            extracted_data['original_name'] = value
                            logger.info(f"    ✓✓ 匹配到原名字段")
                        
                        # 匹配国籍
                        if key == 'nationality' or any(keyword in title_text for keyword in ['国籍', '國籍', '国家']):
                            # 统一国家名称
                            country = normalize_country_name(value)
                            extracted_data['country'] = country
                            logger.info(f"    ✓✓ 匹配到国籍字段（标准化后: {country}）")
            else:
                logger.warning("\n⚠ 未找到基本信息卡片")
            
            # 测试提取照片
            if 'albums' in json_data and len(json_data['albums']) > 0:
                logger.info(f"\n照片:")
                first_album = json_data['albums'][0]
                if 'coverPic' in first_album:
                    cover_pic = first_album['coverPic']
                    if 'url' in cover_pic:
                        photo_url = cover_pic['url']
                        extracted_data['photo_url'] = photo_url
                        logger.info(f"  ✓ 照片 URL: {photo_url}")
            else:
                logger.warning("\n⚠ 未找到照片")
            
            # 打印提取结果汇总
            logger.info("\n" + "=" * 60)
            logger.info("JSON 提取结果汇总")
            logger.info("=" * 60)
            logger.info(f"简介: {'✓ 已提取' if extracted_data['description'] else '✗ 未提取'}")
            logger.info(f"原名: {extracted_data['original_name'] if extracted_data['original_name'] else '✗ 未提取'}")
            logger.info(f"国籍: {extracted_data['country'] if extracted_data['country'] else '✗ 未提取'}")
            logger.info(f"照片: {'✓ 已提取' if extracted_data['photo_url'] else '✗ 未提取'}")
            
            # 判断是否可用
            has_useful_data = any([
                extracted_data['description'],
                extracted_data['original_name'],
                extracted_data['country']
            ])
            
            if has_useful_data:
                logger.info("\n✓✓✓ JSON 提取方案可行！")
            else:
                logger.warning("\n⚠⚠⚠ JSON 提取方案未获取到有效数据")
        else:
            logger.error("✗ 未能提取 JSON 数据")
        
        # ========== 原有的 HTML 解析测试（保留作为对比） ==========
        logger.info("\n" + "=" * 60)
        logger.info("HTML 解析测试（对比）")
        logger.info("=" * 60)
        
        # 测试简介提取
        logger.info("\n测试简介提取")
        logger.info("-" * 60)
        
        summary_selectors = [
            '.lemma-summary',
            '.lemma_desc',
            '.para',
            '[data-module="lemma-summary"]',
            '.J-summary',
            '.summary-content'
        ]
        
        summary_found = False
        for selector in summary_selectors:
            summary = soup.select_one(selector)
            if summary:
                text = summary.get_text(strip=True)
                if text and len(text) > 20:
                    logger.info(f"✓ 找到简介 ({selector})")
                    logger.info(f"  内容: {text[:100]}...")
                    summary_found = True
                    break
        
        if not summary_found:
            logger.warning("⚠ 未能提取到简介")
        
        logger.info("\n" + "=" * 60)
        logger.info("测试完成")
        logger.info("=" * 60)
        
    except Exception as e:
        logger.error(f"测试失败: {e}")
        import traceback
        logger.error(traceback.format_exc())


if __name__ == '__main__':
    # 测试几个常见作者
    test_authors = [
        '刘震云',
        '余华',
        '马伯庸',
        '莫言',
        '金庸'
    ]
    
    print("\n百度百科解析测试")
    print("=" * 60)
    print("将测试以下作者:")
    for idx, author in enumerate(test_authors, 1):
        print(f"  {idx}. {author}")
    print("=" * 60)
    
    choice = input("\n请选择要测试的作者编号 (1-5)，或输入作者名称: ").strip()
    
    if choice.isdigit() and 1 <= int(choice) <= len(test_authors):
        author_name = test_authors[int(choice) - 1]
    else:
        author_name = choice
    
    if author_name:
        test_baidu_author(author_name)
    else:
        print("未输入作者名称，退出测试")
