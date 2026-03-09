-- ==========================================
-- 安全迁移步骤：从 zlibrary_download_task 迁移到 resource_link_crawl_task
-- 说明：分步执行，可随时回滚，不影响现有业务数据（resource、author等表）
-- ==========================================

USE smart_library;

-- ==========================================
-- 步骤 1: 验证旧表是否存在并检查数据
-- ==========================================
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('✓ zlibrary_download_task 表存在，包含 ', 
            (SELECT COUNT(*) FROM zlibrary_download_task), ' 条记录')
        ELSE '✗ zlibrary_download_task 表不存在，跳过迁移'
    END AS check_result
FROM information_schema.tables 
WHERE table_schema = 'smart_library' 
  AND table_name = 'zlibrary_download_task';

-- 查看旧表数据分布
SELECT 
    '旧表数据统计' AS step,
    COUNT(*) AS total_records,
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS pending,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS processing,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS failed,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS no_resource,
    SUM(CASE WHEN pdf_downloaded = 1 THEN 1 ELSE 0 END) AS pdf_count,
    SUM(CASE WHEN epub_downloaded = 1 THEN 1 ELSE 0 END) AS epub_count,
    SUM(CASE WHEN mobi_downloaded = 1 THEN 1 ELSE 0 END) AS mobi_count
FROM zlibrary_download_task
WHERE EXISTS (
    SELECT 1 FROM information_schema.tables 
    WHERE table_schema = 'smart_library' 
      AND table_name = 'zlibrary_download_task'
);

-- ==========================================
-- 步骤 2: 创建新表（不影响旧表和现有业务数据）
-- ==========================================
CREATE TABLE IF NOT EXISTS resource_link_crawl_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- 资源信息
    resource_id VARCHAR(50) NOT NULL UNIQUE COMMENT '资源ID（一本书一条记录）',
    isbn VARCHAR(20) COMMENT 'ISBN（用于搜索）',
    title VARCHAR(255) COMMENT '图书标题（冗余，方便查看）',
    
    -- 书籍页（豆瓣等）
    info_page_count INT DEFAULT 0 COMMENT '书籍页链接数量',
    info_page_json JSON COMMENT '书籍页链接列表 [{"platform": 1, "url": "...", "title": "...", "description": "..."}]',
    info_page_status TINYINT DEFAULT 0 COMMENT '书籍页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源',
    
    -- 下载页（ZLibrary等）
    download_page_count INT DEFAULT 0 COMMENT '下载页链接数量',
    download_page_json JSON COMMENT '下载页链接列表 [{"platform": 2, "url": "...", "title": "ZLibrary - 书名", "file_format": "PDF", "file_size": "2.5MB"}]',
    download_page_status TINYINT DEFAULT 0 COMMENT '下载页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源',
    
    -- 解读页（B站/YouTube等）
    review_page_count INT DEFAULT 0 COMMENT '解读页链接数量',
    review_page_json JSON COMMENT '解读页链接列表 [{"platform": 3, "url": "...", "title": "...", "description": "UP主：..."}]',
    review_page_status TINYINT DEFAULT 0 COMMENT '解读页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源',
    
    -- 整体任务状态
    overall_status TINYINT DEFAULT 0 COMMENT '整体状态: 0-待处理 / 1-部分完成 / 2-全部完成 / 3-全部失败',
    
    error_msg TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    
    ctime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_overall_status (overall_status),
    INDEX idx_isbn (isbn),
    INDEX idx_info_status (info_page_status),
    INDEX idx_download_status (download_page_status),
    INDEX idx_review_status (review_page_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源链接爬取任务表（一本书一条记录）';

-- ==========================================
-- 步骤 3: 迁移数据（只迁移 ZLibrary 下载任务到 download_page 字段）
-- 说明：将旧表的下载任务状态迁移到新表，不影响 resource、author 等业务表
-- ==========================================
INSERT INTO resource_link_crawl_task (
    resource_id, 
    isbn, 
    title, 
    download_page_status,
    overall_status,
    error_msg, 
    retry_count,
    ctime,
    mtime
)
SELECT 
    resource_id,
    isbn,
    title,
    CASE 
        WHEN status = 2 THEN 2  -- 已完成
        WHEN status = 4 THEN 4  -- 无资源
        WHEN status = 3 THEN 3  -- 失败
        ELSE 0                   -- 待处理/处理中 -> 待处理
    END AS download_page_status,
    CASE 
        WHEN status = 2 THEN 1  -- 部分完成（只有下载页完成）
        ELSE 0                   -- 待处理
    END AS overall_status,
    error_msg,
    retry_count,
    ctime,
    mtime
FROM zlibrary_download_task
WHERE EXISTS (
    SELECT 1 FROM information_schema.tables 
    WHERE table_schema = 'smart_library' 
      AND table_name = 'zlibrary_download_task'
)
ON DUPLICATE KEY UPDATE
    download_page_status = VALUES(download_page_status),
    overall_status = VALUES(overall_status),
    error_msg = VALUES(error_msg),
    retry_count = VALUES(retry_count),
    mtime = VALUES(mtime);

-- ==========================================
-- 步骤 4: 验证迁移结果
-- ==========================================
SELECT 
    '✓ 迁移验证' AS step,
    COUNT(*) AS total_migrated,
    SUM(CASE WHEN download_page_status = 0 THEN 1 ELSE 0 END) AS pending,
    SUM(CASE WHEN download_page_status = 2 THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN download_page_status = 3 THEN 1 ELSE 0 END) AS failed,
    SUM(CASE WHEN download_page_status = 4 THEN 1 ELSE 0 END) AS no_resource
FROM resource_link_crawl_task;

-- ==========================================
-- 步骤 5: 对比旧表数据（确认数据一致性）
-- ==========================================
SELECT 
    '✓ 数据一致性检查' AS step,
    (SELECT COUNT(*) FROM zlibrary_download_task 
     WHERE EXISTS (SELECT 1 FROM information_schema.tables 
                   WHERE table_schema = 'smart_library' 
                   AND table_name = 'zlibrary_download_task')) AS old_table_count,
    (SELECT COUNT(*) FROM resource_link_crawl_task) AS new_table_count,
    CASE 
        WHEN (SELECT COUNT(*) FROM zlibrary_download_task 
              WHERE EXISTS (SELECT 1 FROM information_schema.tables 
                            WHERE table_schema = 'smart_library' 
                            AND table_name = 'zlibrary_download_task')) = 
             (SELECT COUNT(*) FROM resource_link_crawl_task)
        THEN '✓ 数据量一致'
        ELSE '✗ 数据量不一致，请检查'
    END AS consistency_check;

-- ==========================================
-- 步骤 6: 备份旧表（推荐执行）
-- 执行前请确认步骤4和步骤5的验证结果正确
-- ==========================================
-- RENAME TABLE zlibrary_download_task TO zlibrary_download_task_backup_20260308;
-- SELECT '✓ 旧表已备份为 zlibrary_download_task_backup_20260308' AS result;

-- ==========================================
-- 步骤 7: 删除旧表（谨慎操作）
-- 只有在确认新表数据完整且系统运行正常后才执行
-- 建议：保留备份表至少1个月
-- ==========================================
-- DROP TABLE IF EXISTS zlibrary_download_task_backup_20260308;
-- SELECT '✓ 备份表已删除' AS result;

-- ==========================================
-- 回滚方案（如果迁移出现问题）
-- ==========================================
-- 1. 如果只执行了步骤1-3，直接删除新表：
--    DROP TABLE IF EXISTS resource_link_crawl_task;
--
-- 2. 如果执行了步骤6（备份），恢复旧表：
--    RENAME TABLE zlibrary_download_task_backup_20260308 TO zlibrary_download_task;
--
-- 3. 旧表数据完全不受影响，可以随时回滚
