-- ==========================================
-- 验证 bookmark 中的 resource_id 是否存在于 resource 表
-- ==========================================

-- 检查哪些 bookmark 的 resource_id 在 resource 表中不存在
SELECT 
    b.bookmark_id,
    b.resource_id,
    b.content,
    b.author_note,
    CASE 
        WHEN r.resource_id IS NULL THEN '❌ 不存在'
        ELSE '✅ 存在'
    END as status,
    r.title as resource_title
FROM bookmark b
LEFT JOIN resource r ON b.resource_id = r.resource_id
ORDER BY status DESC, b.bookmark_id;

-- 统计结果
SELECT 
    COUNT(*) as total_bookmarks,
    SUM(CASE WHEN r.resource_id IS NOT NULL THEN 1 ELSE 0 END) as valid_bookmarks,
    SUM(CASE WHEN r.resource_id IS NULL THEN 1 ELSE 0 END) as invalid_bookmarks
FROM bookmark b
LEFT JOIN resource r ON b.resource_id = r.resource_id;
