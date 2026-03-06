-- =========================================================
-- 智能电子图书分析与推荐管理系统 (Smart Library)
-- 版本: V3.2 回滚脚本 (Rollback to V3.1)
-- 回滚内容:
--   1. 删除 resource_link 表
--   2. 删除 resource_character_graph 表
--   3. 恢复 resource_file 表
--   4. 删除 resource 表的 has_graph 字段
-- 适用: Spring Boot 3.5.6 + MySQL 8.0
-- 执行时机: 当 V3.2 升级出现问题需要回退时
-- =========================================================

USE smart_library;

-- ==========================================
-- Step 1: 删除 V3.2 新增的表
-- ==========================================
DROP TABLE IF EXISTS resource_link;
DROP TABLE IF EXISTS resource_character_graph;

-- ==========================================
-- Step 2: 恢复 resource_file 表 (V3.1 原版)
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
-- Step 3: 删除 resource 表的 has_graph 字段
-- ==========================================
ALTER TABLE resource
    DROP INDEX idx_has_graph;

ALTER TABLE resource
    DROP COLUMN has_graph;

-- ==========================================
-- 回滚完成提示
-- ==========================================
-- 执行完成后，请执行以下操作:
-- 1. 检查表结构: SHOW TABLES LIKE 'resource%';
-- 2. 检查字段: DESC resource;
-- 3. 恢复 Java Entity 类到 V3.1 版本
-- 4. 恢复 Mapper/Service/Controller 层代码
-- 5. 恢复前端 API 调用逻辑
-- 6. 如有备份数据，恢复 resource_file 表数据
