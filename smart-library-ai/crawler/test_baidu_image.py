"""
测试百度百科图片下载

@author JacoryCyJin
@date 2025/03/02
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from utils import MinioHelper
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)

def test_baidu_image():
    """测试百度百科图片下载"""
    
    # 测试图片 URL（从你的错误日志中获取）
    test_urls = [
        'https://bkimg.cdn.bcebos.com/pic/b58f8c5494eef01f3a29da7265a88e25bc315c60d777?x-bce-process=image/resize,m_lfit,w_536,limit_1/quality,Q_70',
        'https://bkimg.cdn.bcebos.com/pic/b3fb43166d224f4a20a484d057ba87529822730e7be8?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1'
    ]
    
    minio = MinioHelper()
    
    for idx, url in enumerate(test_urls, 1):
        logger.info(f"\n测试 {idx}: {url[:80]}...")
        
        result = minio.upload_from_url(
            url,
            bucket_name='library-avatars'
        )
        
        if result:
            logger.info(f"✓ 上传成功: {result}")
        else:
            logger.error(f"✗ 上传失败")

if __name__ == '__main__':
    test_baidu_image()
