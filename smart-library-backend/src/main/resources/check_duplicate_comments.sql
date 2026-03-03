-- =========================================================
-- 检查重复评论脚本
-- 说明: 在执行迁移前，先检查是否有重复评论
-- =========================================================

USE smart_library;

-- 1. 查看是否有重复评论
SELECT 
    user_id,
    resource_id,
    COUNT(*) as comment_count,
    GROUP_CONCAT(id ORDER BY id) as comment_ids,
    GROUP_CONCAT(ctime ORDER BY id) as comment_times
FROM comment
WHERE deleted = 0
GROUP BY user_id, resource_id
HAVING COUNT(*) > 1
ORDER BY comment_count DESC;

-- 2. 统计重复评论数量
SELECT 
    '总评论数' as metric,
    COUNT(*) as count
FROM comment
WHERE deleted = 0

UNION ALL

SELECT 
    '有重复的用户-资源组合' as metric,
    COUNT(*) as count
FROM (
    SELECT user_id, resource_id
    FROM comment
    WHERE deleted = 0
    GROUP BY user_id, resource_id
    HAVING COUNT(*) > 1
) t

UNION ALL

SELECT 
    '将被删除的重复评论数' as metric,
    COUNT(*) - COUNT(DISTINCT CONCAT(user_id, '-', resource_id)) as count
FROM comment
WHERE deleted = 0;

-- =========================================================
-- 说明:
-- 1. 如果第一个查询没有结果，说明没有重复评论，可以安全执行迁移
-- 2. 如果有结果，会显示哪些用户对哪些资源有重复评论
-- 3. 第二个查询会统计将被删除的评论数量
-- =========================================================
