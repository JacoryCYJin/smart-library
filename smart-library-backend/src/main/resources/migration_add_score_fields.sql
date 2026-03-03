-- =========================================================
-- 评分系统升级迁移脚本
-- 版本: V3.2
-- 说明: 添加用户评分和综合评分字段，修改评分范围为0-10分
-- =========================================================

USE smart_library;

-- 1. 修改 resource 表，添加用户评分和综合评分字段
ALTER TABLE resource
    ADD COLUMN user_score DECIMAL(3, 1) DEFAULT 0.0 COMMENT '本站用户平均评分(0-10分)' AFTER source_score,
    ADD COLUMN user_score_count INT DEFAULT 0 COMMENT '本站评分人数' AFTER user_score,
    ADD COLUMN final_score DECIMAL(3, 1) DEFAULT 0.0 COMMENT '综合评分(动态权重计算)' AFTER user_score_count;

-- 2. 修改 source_score 注释，明确是豆瓣 0-10 分
ALTER TABLE resource
    MODIFY COLUMN source_score DECIMAL(3, 1) DEFAULT 0.0 COMMENT '原站评分(豆瓣0-10分)';

-- 3. 修改 comment 表的 score 字段，改为 0-10 分
ALTER TABLE comment
    MODIFY COLUMN score DECIMAL(3, 1) DEFAULT 0.0 COMMENT '评分0至10';

-- 4. 初始化 final_score（如果 source_score 有值，先用豆瓣评分填充）
UPDATE resource
SET final_score = source_score
WHERE source_score > 0;

-- 5. 添加索引优化查询性能
CREATE INDEX idx_final_score ON resource(final_score DESC);

-- =========================================================
-- 注意事项:
-- 1. 此脚本不会影响现有的 1900 条数据
-- 2. 新增字段默认值为 0，不会破坏现有数据
-- 3. 如果已有用户评论，需要运行评分重算脚本
-- 4. 动态权重算法：评分人数越多，用户评分权重越高（最高70%）
-- =========================================================
