-- =========================================================
-- 添加评论唯一约束迁移脚本
-- 版本: V3.3
-- 说明: 防止用户对同一资源重复评论
-- =========================================================

USE smart_library;

-- 关闭安全模式
SET SQL_SAFE_UPDATES = 0;

-- 1. 检查并删除重复评论（保留最新的一条）
DELETE c1 FROM comment c1
INNER JOIN comment c2 
WHERE c1.user_id = c2.user_id 
  AND c1.resource_id = c2.resource_id
  AND c1.deleted = c2.deleted
  AND c1.id < c2.id;

-- 2. 添加唯一约束
ALTER TABLE comment
    ADD UNIQUE KEY uk_user_resource (user_id, resource_id, deleted);

-- 恢复安全模式
SET SQL_SAFE_UPDATES = 1;

-- 3. 查看结果
SELECT 
    '当前评论总数' as metric,
    COUNT(*) as count
FROM comment
WHERE deleted = 0;

-- =========================================================
-- 注意事项:
-- 1. 此脚本会删除重复评论，只保留每个用户对每本书的最新评论
-- 2. 建议先备份数据库再执行
-- 3. 执行后用户无法对同一本书重复评论
-- =========================================================
