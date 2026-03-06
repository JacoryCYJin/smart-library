-- Migration: 重命名作者头像 Bucket 从 library-avatars 到 library-author-avatars
-- Author: System
-- Date: 2026/03/06

-- 更新 author 表中的头像 URL
UPDATE author 
SET photo_url = REPLACE(photo_url, 'library-avatars', 'library-author-avatars')
WHERE photo_url LIKE '%library-avatars%';

-- 验证更新结果
SELECT 
    COUNT(*) as total_updated,
    COUNT(CASE WHEN photo_url LIKE '%library-author-avatars%' THEN 1 END) as new_bucket_count
FROM author 
WHERE photo_url IS NOT NULL;
