package io.github.jacorycyjin.smartlibrary.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO 文件存储服务接口
 * 
 * @author JacoryCyJin
 * @date 2025/02/27
 */
public interface MinioService {
    
    /**
     * 上传文件到指定 bucket
     * 
     * @param file 文件
     * @param bucketName bucket 名称
     * @return 文件访问 URL
     */
    String uploadFile(MultipartFile file, String bucketName);
    
    /**
     * 上传文件流到指定 bucket
     * 
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @param bucketName bucket 名称
     * @return 文件访问 URL
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType, String bucketName);
    
    /**
     * 删除文件
     * 
     * @param fileName 文件名
     * @param bucketName bucket 名称
     */
    void deleteFile(String fileName, String bucketName);
    
    /**
     * 获取文件访问 URL
     * 
     * @param fileName 文件名
     * @param bucketName bucket 名称
     * @return 文件访问 URL
     */
    String getFileUrl(String fileName, String bucketName);
    
    /**
     * 检查 bucket 是否存在，不存在则创建
     * 
     * @param bucketName bucket 名称
     */
    void ensureBucketExists(String bucketName);
}
