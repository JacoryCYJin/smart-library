-- =========================================================
-- 评分重算脚本
-- 说明: 用于重新计算所有资源的用户评分和综合评分
-- 适用场景: 数据库已有评论数据，需要初始化评分字段
-- 
-- 安全说明: 
-- 1. 此脚本会更新所有 resource 表的评分字段
-- 2. 只更新新添加的字段（user_score, user_score_count, final_score）
-- 3. 不会修改现有数据（title, author_name, source_score 等）
-- 4. 建议先备份数据库再执行
-- =========================================================

USE smart_library;

-- 开启安全模式检查
SET SQL_SAFE_UPDATES = 0;

-- 1. 计算每个资源的用户平均评分和评分人数
UPDATE resource r
LEFT JOIN (
    SELECT 
        resource_id,
        ROUND(AVG(score), 1) as avg_score,
        COUNT(*) as score_count
    FROM comment
    WHERE deleted = 0 
      AND audit_status = 1
      AND score > 0
    GROUP BY resource_id
) c ON r.resource_id = c.resource_id
SET 
    r.user_score = COALESCE(c.avg_score, 0),
    r.user_score_count = COALESCE(c.score_count, 0)
WHERE r.deleted = 0;  -- 添加 WHERE 条件，只更新未删除的资源

-- 2. 计算综合评分（动态权重算法）
UPDATE resource
SET final_score = CASE
    -- 两个评分都有：动态权重计算
    -- 用户评分权重 = min(0.7, 评分人数 / 150)
    -- 豆瓣评分权重 = 1 - 用户评分权重
    WHEN source_score > 0 AND user_score > 0 THEN
        ROUND(
            source_score * (1 - LEAST(0.7, user_score_count / 150.0)) +
            user_score * LEAST(0.7, user_score_count / 150.0),
            1
        )
    -- 只有豆瓣评分
    WHEN source_score > 0 AND user_score = 0 THEN
        source_score
    -- 只有用户评分
    WHEN (source_score = 0 OR source_score IS NULL) AND user_score > 0 THEN
        user_score
    -- 都没有
    ELSE 0
END
WHERE deleted = 0;  -- 添加 WHERE 条件，只更新未删除的资源

-- 恢复安全模式
SET SQL_SAFE_UPDATES = 1;

-- 3. 查看重算结果统计
SELECT 
    '总资源数' as metric,
    COUNT(*) as count
FROM resource
WHERE deleted = 0

UNION ALL

SELECT 
    '有豆瓣评分' as metric,
    COUNT(*) as count
FROM resource
WHERE deleted = 0 AND source_score > 0

UNION ALL

SELECT 
    '有用户评分' as metric,
    COUNT(*) as count
FROM resource
WHERE deleted = 0 AND user_score > 0

UNION ALL

SELECT 
    '有综合评分' as metric,
    COUNT(*) as count
FROM resource
WHERE deleted = 0 AND final_score > 0;

-- =========================================================
-- 执行说明:
-- 1. 在 MySQL 客户端或 Workbench 中执行此脚本
-- 2. 脚本会自动计算所有未删除资源的评分
-- 3. 执行完成后查看统计结果
-- 4. 如果担心数据安全，建议先备份：
--    mysqldump -u root -p smart_library > backup.sql
-- =========================================================
