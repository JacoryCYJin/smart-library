-- ==========================================
-- 为 resource_link 表添加 cover_url 字段
-- 用于存储视频封面 URL（B站/YouTube）
-- ==========================================

-- 添加 cover_url 字段
ALTER TABLE resource_link
ADD COLUMN cover_url VARCHAR(500) COMMENT '视频封面URL（B站/YouTube）' AFTER description;

-- 验证字段添加
DESC resource_link;
