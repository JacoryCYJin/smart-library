-- ==========================================
-- 豆瓣图书爬取任务表
-- 用于追踪豆瓣图书爬取进度（按分类）
-- ==========================================

CREATE TABLE IF NOT EXISTS douban_crawl_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- 分类信息
    category_id VARCHAR(50) NOT NULL COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    
    -- 任务状态
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败',
    progress INT DEFAULT 0 COMMENT '进度（已爬取数量）',
    target INT DEFAULT 20 COMMENT '目标数量',
    
    error_msg TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    
    ctime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='豆瓣图书爬取任务表';

-- ==========================================
-- 作者信息爬取任务表
-- 用于追踪作者详细信息的爬取进度
-- ==========================================

CREATE TABLE IF NOT EXISTS author_crawl_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- 作者信息
    author_id VARCHAR(50) NOT NULL COMMENT '作者ID',
    author_name VARCHAR(255) NOT NULL COMMENT '作者姓名',
    douban_author_url VARCHAR(500) COMMENT '豆瓣作者页面URL',
    
    -- 任务状态
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源',
    
    error_msg TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    
    ctime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_author (author_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作者信息爬取任务表';

-- ==========================================
-- 资源链接爬取任务表（一本书一条记录）
-- 用于追踪单个资源的所有平台链接爬取进度
-- 支持：豆瓣书籍页、ZLibrary下载页、B站/YouTube解读页
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
-- 使用说明
-- ==========================================
-- 1. 首次使用：直接运行本文件创建所有表
--    mysql -u root -p smart_library < crawler_task.sql
--
-- 2. 如果已有旧表 zlibrary_download_task：
--    先运行 safe_migration_steps.sql 进行安全迁移
--
-- 3. 初始化任务：
--    python init_link_tasks.py
--
-- 4. 执行爬取：
--    python crawl_links.py --type all --limit 10
