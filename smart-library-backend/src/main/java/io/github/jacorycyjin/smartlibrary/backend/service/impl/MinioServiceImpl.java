package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.config.MinioConfig;
import io.github.jacorycyjin.smartlibrary.backend.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 文件存储服务实现
 * 
 * @author JacoryCyJin
 * @date 2025/02/27
 */
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {
    
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    
    public MinioServiceImpl(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        // 初始化所有 bucket
        initBuckets();
    }
    
    /**
     * 初始化所有 bucket
     */
    private void initBuckets() {
        ensureBucketExists(minioConfig.getBuckets().getCovers());
        ensureBucketExists(minioConfig.getBuckets().getAttachments());
        ensureBucketExists(minioConfig.getBuckets().getNlpCorpus());
    }
    
    @Override
    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            // 验证文件
            validateFile(file);
            
            String originalFilename = file.getOriginalFilename();
            String fileName = generateFileName(originalFilename);
            
            return uploadFile(
                file.getInputStream(),
                fileName,
                file.getContentType(),
                bucketName
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "文件上传失败");
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, String bucketName) {
        try {
            ensureBucketExists(bucketName);
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build()
            );
            
            return getFileUrl(fileName, bucketName);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "文件上传失败");
        }
    }
    
    @Override
    public void deleteFile(String fileName, String bucketName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "文件删除失败");
        }
    }
    
    @Override
    public String getFileUrl(String fileName, String bucketName) {
        try {
            // 生成 7 天有效期的预签名 URL
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(7, TimeUnit.DAYS)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取文件 URL 失败: {}", e.getMessage(), e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "获取文件 URL 失败");
        }
    }
    
    @Override
    public void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
            
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                log.info("创建 bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("检查/创建 bucket 失败: {}", e.getMessage(), e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "Bucket 初始化失败");
        }
    }
    
    /**
     * 生成唯一文件名
     * 
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
    
    /**
     * 验证文件
     * 
     * @param file 文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "文件名不能为空");
        }
        
        // 验证文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "文件大小不能超过 10MB");
        }
    }
}
