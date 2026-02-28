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
-- ZLibrary 文件下载任务表
-- 用于追踪 ZLibrary 电子书文件下载进度（按资源）
-- ==========================================

CREATE TABLE IF NOT EXISTS zlibrary_download_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- 资源信息
    resource_id VARCHAR(50) NOT NULL COMMENT '资源ID',
    isbn VARCHAR(20) COMMENT 'ISBN',
    title VARCHAR(255) COMMENT '图书标题（冗余，方便查看）',
    
    -- 任务状态
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源',
    
    -- 文件下载状态（位标记）
    pdf_downloaded TINYINT DEFAULT 0 COMMENT 'PDF 是否已下载: 0-未下载 / 1-已下载',
    epub_downloaded TINYINT DEFAULT 0 COMMENT 'EPUB 是否已下载: 0-未下载 / 1-已下载',
    mobi_downloaded TINYINT DEFAULT 0 COMMENT 'MOBI 是否已下载: 0-未下载 / 1-已下载',
    
    error_msg TEXT COMMENT '错误信息',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    
    ctime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_resource (resource_id),
    INDEX idx_status (status),
    INDEX idx_isbn (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ZLibrary 文件下载任务表';
