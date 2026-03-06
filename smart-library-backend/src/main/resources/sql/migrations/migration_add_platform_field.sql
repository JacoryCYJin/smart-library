-- =========================================================
-- 智能电子图书分析与推荐管理系统 (Smart Library)
-- 版本: V3.2.1 升级脚本 (添加 platform 字段)
-- 升级内容:
--   1. 在 resource_link 表中添加 platform 字段
--   2. 更新 link_type 字段注释
--   3. 添加 platform 索引
-- 适用: Spring Boot 3.5.6 + MySQL 8.0
-- 执行时机: 在 V3.2 基础上执行
-- =========================================================

USE smart_library;

-- ==========================================
-- Step 1: 添加 platform 字段
-- ==========================================
ALTER TABLE resource_link
    ADD COLUMN platform TINYINT NOT NULL DEFAULT 99 COMMENT '平台: 1-豆瓣 / 2-ZLibrary / 3-B站 / 4-YouTube / 99-其他'
    AFTER link_type;

-- ==========================================
-- Step 2: 添加 platform 索引
-- ==========================================
CREATE INDEX idx_platform ON resource_link(platform);

-- ==========================================
-- Step 3: 更新 link_type 字段注释
-- ==========================================
ALTER TABLE resource_link
    MODIFY COLUMN link_type TINYINT NOT NULL COMMENT '链接类型: 1-书籍页 / 2-下载页 / 3-解读页';

-- ==========================================
-- Step 4: 数据迁移（可选）
-- 说明: 如果已有数据，需要根据 link_type 推断 platform
-- ==========================================

-- 示例：根据旧的 link_type 推断 platform
-- 注意：这需要根据你的实际数据情况调整

-- 如果 link_type = 1 (豆瓣主页) -> platform = 1 (豆瓣)
-- UPDATE resource_link SET platform = 1 WHERE link_type = 1;

-- 如果 link_type = 2 (ZLibrary书籍页) -> platform = 2 (ZLibrary)
-- UPDATE resource_link SET platform = 2 WHERE link_type = 2;

-- 如果 link_type = 3 (ZLibrary下载) -> platform = 2 (ZLibrary)
-- UPDATE resource_link SET platform = 2 WHERE link_type = 3;

-- 如果 link_type = 4 (B站解读) -> platform = 3 (B站)
-- UPDATE resource_link SET platform = 3 WHERE link_type = 4;

-- 如果 link_type = 5 (YouTube解读) -> platform = 4 (YouTube)
-- UPDATE resource_link SET platform = 4 WHERE link_type = 5;

-- ==========================================
-- 升级完成提示
-- ==========================================
-- 执行完成后，请执行以下操作:
-- 1. 检查表结构: DESC resource_link;
-- 2. 检查索引: SHOW INDEX FROM resource_link;
-- 3. 如果有旧数据，取消注释上面的 UPDATE 语句并执行数据迁移
-- 4. 重启 Spring Boot 应用
-- 5. 测试链接分组功能

SELECT '✅ V3.2.1 升级完成：已添加 platform 字段' AS status;
