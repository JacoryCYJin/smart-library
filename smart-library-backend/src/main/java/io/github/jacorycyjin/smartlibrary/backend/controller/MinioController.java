package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.config.MinioConfig;
import io.github.jacorycyjin.smartlibrary.backend.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO 文件操作接口
 * 
 * @author JacoryCyJin
 * @date 2025/02/27
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class MinioController {

    private final MinioService minioService;
    private final MinioConfig minioConfig;

    public MinioController(MinioService minioService, MinioConfig minioConfig) {
        this.minioService = minioService;
        this.minioConfig = minioConfig;
    }

    /**
     * 上传图书封面
     * 
     * @param file 文件
     * @return 文件访问 URL
     */
    @PostMapping("/upload/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        log.info("收到封面上传请求，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        String url = minioService.uploadFile(file, minioConfig.getBuckets().getCovers());
        log.info("封面上传成功，URL: {}", url);
        return Result.success(url);
    }

    /**
     * 上传附件
     * 
     * @param file 文件
     * @return 文件访问 URL
     */
    @PostMapping("/upload/attachment")
    public Result<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
        log.info("收到附件上传请求，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        String url = minioService.uploadFile(file, minioConfig.getBuckets().getAttachments());
        log.info("附件上传成功，URL: {}", url);
        return Result.success(url);
    }
}