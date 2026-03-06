-- Rollback: 回滚作者头像 Bucket 从 library-author-avatars 到 library-avatars
-- Author: System
-- Date: 2026/03/06

-- 回滚 author 表中的头像 URL
UPDATE author 
SET photo_url = REPLACE(photo_url, 'library-author-avatars', 'library-avatars')
WHERE photo_url LIKE '%library-author-avatars%';

-- 验证回滚结果
SELECT 
    COUNT(*) as total_rollback,
    COUNT(CASE WHEN photo_url LIKE '%library-avatars%' THEN 1 END) as old_bucket_count
FROM author 
WHERE photo_url IS NOT NULL;
