-- =========================================================
-- 智能电子图书分析与推荐管理系统 (Smart Library)
-- 版本: V3.1 (Schema Update: Author Sorting)
-- 适用: Spring Boot 3.5.6 + MySQL 8.0
-- 字符集: 统一使用 utf8mb4_unicode_ci
-- =========================================================

CREATE DATABASE IF NOT EXISTS smart_library
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_library;

-- ==========================================
-- 0. 清理旧表 (Drop Tables)
-- 注意：顺序很重要，先删依赖表（子表），再删主表
-- ==========================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS recommend_result; -- 推荐算法缓存
DROP TABLE IF EXISTS daily_stat; -- 每日流量统计(周热门)
DROP TABLE IF EXISTS bookmark; -- 物理引擎书签
DROP TABLE IF EXISTS comment; -- 评论
DROP TABLE IF EXISTS user_favorite; -- 收藏
DROP TABLE IF EXISTS user_browse_history; -- 浏览历史
DROP TABLE IF EXISTS resource_author_rel; -- 资源-作者关联
DROP TABLE IF EXISTS resource_category_rel;-- 资源-分类关联
DROP TABLE IF EXISTS resource_tag_rel; -- 资源-标签关联
DROP TABLE IF EXISTS resource_file; -- 资源-文件关联
DROP TABLE IF EXISTS resource; -- [核心] 资源总表
DROP TABLE IF EXISTS author; -- 作者
DROP TABLE IF EXISTS category; -- 分类
DROP TABLE IF EXISTS tag; -- 标签
DROP TABLE IF EXISTS user; -- 用户

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. 用户表 (User)
-- ==========================================
CREATE TABLE user
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户ID(UUID)',
    username   VARCHAR(50)  NOT NULL COMMENT '用户名',
    password   VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    phone      VARCHAR(11) COMMENT '手机号',
    email      VARCHAR(50) COMMENT '邮箱',
    avatar_url VARCHAR(255) COMMENT '头像URL',

    role       TINYINT  DEFAULT 0 COMMENT '角色: 0-普通读者 / 1-系统管理员',
    bio        VARCHAR(255) COMMENT '个人简介',
    status     TINYINT  DEFAULT 0 COMMENT '状态: 0-正常 / 1-封禁',

    ctime      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    mtime      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted    TINYINT  DEFAULT 0 COMMENT '逻辑删除: 0-未删 / 1-已删'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- 预置管理员数据 (密码建议加密后入库，此处为示例)
INSERT INTO user (user_id, username, password, role, bio)
VALUES ('admin_001', 'admin', '123456', 1, 'Super Admin');

-- ==========================================
-- 2. 核心资源表 (Resource) - [关键表]
-- 说明: 合并了图书(Book)与文献(Literature)，字段采用"宽表"设计
-- ==========================================
CREATE TABLE resource
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id     VARCHAR(50)  NOT NULL UNIQUE COMMENT '资源业务标识',
    type            TINYINT       DEFAULT 1 COMMENT '资源类型: 1-图书 / 2-文献期刊',

    title           VARCHAR(255) NOT NULL COMMENT '标题',
    author_name     VARCHAR(255) COMMENT '作者姓名快照',
    translator_name VARCHAR(255) COMMENT '译者姓名快照',
    cover_url       VARCHAR(500) COMMENT '封面图片路径',
    summary         TEXT COMMENT '摘要与简介',
    pub_date        DATE COMMENT '出版日期',

    isbn            VARCHAR(20) COMMENT '国际标准书号',
    publisher       VARCHAR(255) COMMENT '出版社',
    price           DECIMAL(10, 2) COMMENT '价格',
    page_count      INT COMMENT '页数',

    doi             VARCHAR(100) COMMENT '数字对象唯一标识符',
    journal         VARCHAR(100) COMMENT '期刊名称',

    source_origin   TINYINT DEFAULT 1 COMMENT '数据来源: 1-豆瓣读书 / 2-Z-Library / 99-手动录入',
    source_url      VARCHAR(500) COMMENT '原站链接',
    source_score    DECIMAL(3, 1) DEFAULT 0.0 COMMENT '原站评分',
    sentiment_score DECIMAL(5, 4) COMMENT '情感分析得分',

    view_count      INT           DEFAULT 0 COMMENT '总浏览量',
    comment_count   INT           DEFAULT 0 COMMENT '总评论数',
    star_count      INT           DEFAULT 0 COMMENT '总收藏数',

    ctime           DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime           DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       DEFAULT 0 COMMENT '逻辑删除标识',

    INDEX idx_type (type),
    INDEX idx_title (title),
    INDEX idx_view_count (view_count DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='核心资源总表';

-- ==========================================
-- 2.1 资源多格式文件附属表
-- ==========================================
CREATE TABLE resource_file
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id VARCHAR(50)  NOT NULL COMMENT '关联核心资源标识',
    file_type   TINYINT      NOT NULL COMMENT '文件格式: 1-PDF / 2-EPUB / 3-MOBI',
    file_url    VARCHAR(500) NOT NULL COMMENT '对象存储绝对路径',
    file_size   BIGINT COMMENT '文件体积字节数',

    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传入库时间',
    deleted     TINYINT  DEFAULT 0 COMMENT '逻辑删除标识',

    INDEX idx_resource (resource_id),
    UNIQUE KEY uk_res_type (resource_id, file_type) COMMENT '防抖约束同类型文件唯一'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源文件池';

-- ==========================================
-- 3. 基础元数据 (Tag/Category/Author)
-- ==========================================
CREATE TABLE author
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_id     VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) COMMENT '外文原名',
    country       VARCHAR(100) COMMENT '国籍',
    photo_url     VARCHAR(500) COMMENT '作者头像',
    description   TEXT COMMENT '生平简介',

    source_origin TINYINT DEFAULT 1 COMMENT '数据来源: 1-豆瓣读书 / 2-Z-Library / 99-手动录入',
    source_url    VARCHAR(500) COMMENT '原站链接',

    ctime         DATETIME DEFAULT CURRENT_TIMESTAMP,
    mtime         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT  DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='作者库';

CREATE TABLE category
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    parent_id   VARCHAR(50) COMMENT '父分类ID',
    level       TINYINT  DEFAULT 1 COMMENT '层级',
    sort_order  INT      DEFAULT 0 COMMENT '排序权重',

    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP,
    mtime       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT  DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源分类表';

CREATE TABLE tag
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_id  VARCHAR(50) NOT NULL UNIQUE,
    name    VARCHAR(50) NOT NULL UNIQUE,
    type    TINYINT  DEFAULT 0 COMMENT '0-NLP提取 / 1-人工 / 2-爬虫抓取',

    ctime   DATETIME DEFAULT CURRENT_TIMESTAMP,
    mtime   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT  DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='标签库(用于计算Jaccard相似度)';

-- ==========================================
-- 4. 关联关系表 (Many-to-Many)
-- ==========================================
CREATE TABLE resource_author_rel
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id VARCHAR(50) NOT NULL COMMENT '资源ID',
    author_id   VARCHAR(50) NOT NULL COMMENT '作者ID',

    -- [UPDATE] 新增排序字段
    sort        TINYINT     DEFAULT 1 COMMENT '排序权重: 1为一作, 2为二作...',

    role        VARCHAR(20) DEFAULT '作者' COMMENT '角色(作者/译者/编者)',

    -- [UPDATE] 新增唯一键约束，防止排序冲突（作者和译者分别排序）
    UNIQUE KEY uk_res_role_sort (resource_id, role, sort),
    UNIQUE KEY uk_res_auth (resource_id, author_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源-作者关联表';

CREATE TABLE resource_category_rel
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id VARCHAR(50) NOT NULL,
    category_id VARCHAR(50) NOT NULL,
    UNIQUE KEY uk_res_cat (resource_id, category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源-分类关联表';

CREATE TABLE resource_tag_rel
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id VARCHAR(50) NOT NULL,
    tag_id      VARCHAR(50) NOT NULL,
    weight      DECIMAL(5, 4) DEFAULT 1.0 COMMENT 'TF-IDF权重值(核心算法字段)',
    UNIQUE KEY uk_res_tag (resource_id, tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源-标签关联表(NLP核心)';

-- ==========================================
-- 5. 用户交互表 (Interactions) - 结构优化版
-- ==========================================

CREATE TABLE comment
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_id       VARCHAR(50) NOT NULL UNIQUE COMMENT '评论业务标识',
    user_id          VARCHAR(50) NOT NULL,
    resource_id      VARCHAR(50) NOT NULL,
    content          TEXT        NOT NULL COMMENT '评论内容',
    score            DECIMAL(2, 1) DEFAULT 0.0 COMMENT '评分1至5',

    like_count       INT           DEFAULT 0 COMMENT '点赞数',
    audit_status     TINYINT       DEFAULT 0 COMMENT '审核状态: 0待审 1通过 2驳回',
    rejection_reason VARCHAR(255) COMMENT '驳回理由',

    ctime            DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改与审核时间',
    deleted          TINYINT       DEFAULT 0 COMMENT '逻辑删除标识',

    INDEX idx_res_audit (resource_id, audit_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='评论与评分表';

CREATE TABLE user_favorite
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     VARCHAR(50) NOT NULL,
    resource_id VARCHAR(50) NOT NULL,

    -- 仅保留创建时间，取消收藏直接进行物理删除
    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',

    UNIQUE KEY uk_fav (user_id, resource_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户收藏表';

CREATE TABLE user_browse_history
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     VARCHAR(50) NOT NULL,
    resource_id VARCHAR(50) NOT NULL,
    view_count  INT      DEFAULT 1 COMMENT '累计浏览次数',

    -- 保留双时间戳，清空历史记录时直接进行物理删除
    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次触达时间',
    mtime       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活跃时间',

    UNIQUE KEY uk_hist (user_id, resource_id),
    INDEX idx_mtime (mtime) COMMENT '配合定时任务清理过期冷数据'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户浏览历史表';

-- ==========================================
-- 6. 流量统计与算法 (Statistics & Algo)
-- ==========================================
CREATE TABLE daily_stat
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id VARCHAR(50) NOT NULL COMMENT '资源ID',
    date        DATE        NOT NULL COMMENT '统计日期',
    view_count  INT      DEFAULT 0 COMMENT '当日增量',

    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_date_res (date, resource_id),
    INDEX idx_date (date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='每日流量统计表(用于计算周热门/趋势图)';

CREATE TABLE recommend_result
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     VARCHAR(50) NOT NULL COMMENT '目标用户',
    resource_id VARCHAR(50) NOT NULL COMMENT '推荐资源',
    score       DECIMAL(6, 4) COMMENT '推荐匹配度',
    reason      VARCHAR(50) COMMENT '推荐理由(如: 看了《三体》/ 内容相似)',
    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='推荐结果缓存表(Java/Python计算结果存此处)';

-- ==========================================
-- 7. 前端特效支持 (Frontend Support)
-- ==========================================
CREATE TABLE bookmark
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    bookmark_id VARCHAR(50)  NOT NULL UNIQUE,
    resource_id VARCHAR(50)  NOT NULL COMMENT '点击跳转目标',
    content     VARCHAR(500) NOT NULL COMMENT '金句内容',
    author_note VARCHAR(100) COMMENT '作者/出处',

    click_count INT      DEFAULT 0 COMMENT '引流次数',
    status      TINYINT  DEFAULT 1 COMMENT '1-上架 / 0-下架',

    ctime       DATETIME DEFAULT CURRENT_TIMESTAMP,
    mtime       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT  DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='书签表(用于Matter.js物理引擎掉落)';