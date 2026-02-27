"""
MinIO 文件上传工具类

@author JacoryCyJin
@date 2025/02/27
"""
from minio import Minio
from minio.error import S3Error
from config import Config
import requests
import io
import logging
import uuid

logger = logging.getLogger(__name__)


class MinioHelper:
    """MinIO 操作助手"""
    
    def __init__(self):
        self.client = Minio(
            Config.MINIO_ENDPOINT,
            access_key=Config.MINIO_ACCESS_KEY,
            secret_key=Config.MINIO_SECRET_KEY,
            secure=False
        )
        self._ensure_buckets()
    
    def _ensure_buckets(self):
        """确保 bucket 存在"""
        buckets = [Config.MINIO_BUCKET_COVERS, Config.MINIO_BUCKET_ATTACHMENTS]
        for bucket in buckets:
            try:
                if not self.client.bucket_exists(bucket):
                    self.client.make_bucket(bucket)
                    logger.info(f"创建 bucket: {bucket}")
            except S3Error as e:
                logger.error(f"检查/创建 bucket 失败: {e}")
    
    def upload_from_url(self, image_url, bucket_name=None):
        """
        从 URL 下载图片并上传到 MinIO
        
        Args:
            image_url: 图片 URL
            bucket_name: bucket 名称，默认为封面 bucket
        
        Returns:
            上传后的文件名，失败返回 None
        """
        if not bucket_name:
            bucket_name = Config.MINIO_BUCKET_COVERS
        
        try:
            # 下载图片
            response = requests.get(
                image_url,
                headers={'User-Agent': Config.USER_AGENT},
                timeout=10
            )
            response.raise_for_status()
            
            # 获取文件扩展名
            content_type = response.headers.get('Content-Type', 'image/jpeg')
            ext = self._get_extension(content_type, image_url)
            
            # 生成唯一文件名
            file_name = f"{uuid.uuid4().hex}{ext}"
            
            # 上传到 MinIO
            image_data = io.BytesIO(response.content)
            self.client.put_object(
                bucket_name,
                file_name,
                image_data,
                length=len(response.content),
                content_type=content_type
            )
            
            logger.info(f"图片上传成功: {file_name}")
            return file_name
            
        except Exception as e:
            logger.error(f"图片上传失败 {image_url}: {e}")
            return None
    
    def _get_extension(self, content_type, url):
        """获取文件扩展名"""
        # 从 Content-Type 获取
        ext_map = {
            'image/jpeg': '.jpg',
            'image/jpg': '.jpg',
            'image/png': '.png',
            'image/gif': '.gif',
            'image/webp': '.webp'
        }
        
        if content_type in ext_map:
            return ext_map[content_type]
        
        # 从 URL 获取
        if '.' in url:
            ext = url.split('.')[-1].split('?')[0].lower()
            if ext in ['jpg', 'jpeg', 'png', 'gif', 'webp']:
                return f'.{ext}'
        
        return '.jpg'
    
    def upload_file(self, file_content, file_name, bucket_name=None, content_type='application/octet-stream'):
        """
        上传文件到 MinIO
        
        Args:
            file_content: 文件内容（bytes）
            file_name: 文件名
            bucket_name: bucket 名称
            content_type: 文件 MIME 类型
        
        Returns:
            上传后的文件名，失败返回 None
        """
        if not bucket_name:
            bucket_name = Config.MINIO_BUCKET_ATTACHMENTS
        
        try:
            import io
            
            # 上传到 MinIO
            file_data = io.BytesIO(file_content)
            self.client.put_object(
                bucket_name,
                file_name,
                file_data,
                length=len(file_content),
                content_type=content_type
            )
            
            logger.info(f"文件上传成功: {file_name}")
            return file_name
            
        except Exception as e:
            logger.error(f"文件上传失败 {file_name}: {e}")
            return None
    
    def get_file_url(self, file_name, bucket_name=None):
        """
        获取文件访问 URL
        
        Args:
            file_name: 文件名
            bucket_name: bucket 名称
        
        Returns:
            文件访问 URL
        """
        if not bucket_name:
            bucket_name = Config.MINIO_BUCKET_COVERS
        
        return f"http://{Config.MINIO_ENDPOINT}/{bucket_name}/{file_name}"
