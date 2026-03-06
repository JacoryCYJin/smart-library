-- =========================================================
-- 智能电子图书分析与推荐管理系统 (Smart Library)
-- 版本: V3.2.1 回滚脚本 (移除 platform 字段)
-- 回滚内容:
--   1. 删除 platform 索引
--   2. 删除 platform 字段
--   3. 恢复 link_type 字段注释
-- 适用: Spring Boot 3.5.6 + MySQL 8.0
-- 执行时机: 需要回滚 V3.2.1 升级时执行
-- =========================================================

USE smart_library;

-- ==========================================
-- Step 1: 删除 platform 索引
-- ==========================================
DROP INDEX idx_platform ON resource_link;

-- ==========================================
-- Step 2: 删除 platform 字段
-- ==========================================
ALTER TABLE resource_link
    DROP COLUMN platform;

-- ==========================================
-- Step 3: 恢复 link_type 字段注释
-- ==========================================
ALTER TABLE resource_link
    MODIFY COLUMN link_type TINYINT NOT NULL COMMENT '链接类型: 1-豆瓣主页 / 2-ZLibrary书籍页 / 3-ZLibrary下载 / 4-B站解读 / 5-YouTube解读 / 99-其他';

-- ==========================================
-- 回滚完成提示
-- ==========================================
SELECT '✅ V3.2.1 回滚完成：已移除 platform 字段' AS status;
