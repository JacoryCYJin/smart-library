-- ==========================================
-- 迁移脚本：拆分 bookmark 表的 author_note 字段
-- 将 author_note 拆分为 author_name 和 book_title
-- 适用场景：bookmark 表为空，直接修改表结构
-- 执行时间：2026-03-XX
-- ==========================================

-- 添加新字段
ALTER TABLE bookmark
ADD COLUMN author_name VARCHAR(100) COMMENT '作者姓名' AFTER content,
ADD COLUMN book_title VARCHAR(255) COMMENT '书籍标题' AFTER author_name;

-- 删除旧字段
ALTER TABLE bookmark DROP COLUMN author_note;

-- 验证表结构
DESC bookmark;
