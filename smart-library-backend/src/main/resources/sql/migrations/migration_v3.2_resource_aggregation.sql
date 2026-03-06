-- =========================================================
-- 智能电子图书分析与推荐管理系统 (Smart Library)
-- 版本: V3.2 升级脚本 (Resource Aggregation & AI Graph)
-- 升级内容:
--   1. 删除 resource_file 表 (废弃实体文件存储)
--   2. 新增 resource_link 表 (资源聚合链接)
--   3. 新增 resource_character_graph 表 (AI人物图谱)
--   4. 修改 resource 表 (新增 has_graph 字段)
-- 适用: Spring Boot 3.5.6 + MySQL 8.0
-- 字符集: 统一使用 utf8mb4_unicode_ci
-- 执行时机: 在 V3.1 基础上执行
-- =========================================================

USE smart_library;

-- ==========================================
-- Step 1: 删除冗余表 (废弃实体文件存储模式)
-- ==========================================
DROP TABLE IF EXISTS resource_file;

-- ==========================================
-- Step 2: 新增资源聚合链接表 (Resource Link)
-- 说明: 作为"导航枢纽"，聚合外部资源链接
-- ==========================================
CREATE TABLE resource_link
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    link_id     VARCHAR(50)  NOT NULL UNIQUE COMMENT '链接业务标识(UUID)',
    resource_id VARCHAR(50)  NOT NULL COMMENT '关联资源ID',
    
    link_type   TINYINT      NOT NULL COMMENT '链接类型: 1-ZLibrary下载 / 2-B站解读 / 3-YouTube解读 / 4-豆瓣主页 / 5-其他',
    url         VARCHAR(1000) NOT NULL COMMENT '外部链接地址',
    title       VARCHAR(255) COMMENT '链接展示标题',
    description VARCHAR(500) COMMENT '链接描述/备注',
    
    sort_order  INT          DEFAULT 0 COMMENT '排序权重(同类型链接按此排序)',
    click_count INT          DEFAULT 0 COMMENT '点击统计',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1-有效 / 0-失效',
    
    ctime       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除: 0-未删 / 1-已删',
    
    INDEX idx_resource_id (resource_id),
    INDEX idx_link_type (link_type),
    INDEX idx_sort_order (sort_order)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='资源聚合链接表(导航枢纽)';

-- ==========================================
-- Step 3: 新增AI人物图谱表 (Character Graph)
-- 说明: 存储大模型生成的人物关系图谱JSON数据
-- ==========================================
CREATE TABLE resource_character_graph
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    graph_id         VARCHAR(50) NOT NULL UNIQUE COMMENT '图谱业务标识(UUID)',
    resource_id      VARCHAR(50) NOT NULL UNIQUE COMMENT '关联资源ID(一对一关系)',
    
    graph_json       JSON COMMENT 'ECharts图谱数据(包含nodes和edges)',
    
    generate_status  TINYINT     DEFAULT 0 COMMENT '生成状态: 0-未生成 / 1-生成中 / 2-生成成功 / -1-生成失败',
    error_message    VARCHAR(500) COMMENT '错误信息(失败时记录)',
    
    ai_model         VARCHAR(50) COMMENT 'AI模型标识(如: gpt-4, claude-3等)',
    token_usage      INT COMMENT 'Token消耗量',
    generate_time    INT COMMENT '生成耗时(毫秒)',
    
    ctime            DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    mtime            DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT     DEFAULT 0 COMMENT '逻辑删除: 0-未删 / 1-已删',
    
    INDEX idx_resource_id (resource_id),
    INDEX idx_generate_status (generate_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='AI人物关系图谱表';

-- ==========================================
-- Step 4: 修改资源主表 (添加图谱标识字段)
-- 说明: 新增 has_graph 字段，提升前端列表查询效率
-- ==========================================
ALTER TABLE resource
    ADD COLUMN has_graph TINYINT DEFAULT 0 COMMENT 'AI图谱标识: 0-无图谱 / 1-已生成图谱' AFTER sentiment_score;

-- 为 has_graph 字段添加索引，优化查询性能
CREATE INDEX idx_has_graph ON resource(has_graph);

-- ==========================================
-- 升级完成提示
-- ==========================================
-- 执行完成后，请执行以下操作:
-- 1. 检查表结构: SHOW CREATE TABLE resource_link;
-- 2. 检查表结构: SHOW CREATE TABLE resource_character_graph;
-- 3. 检查字段: DESC resource;
-- 4. 更新 Java Entity 类: Resource, ResourceLink, ResourceCharacterGraph
-- 5. 更新 Mapper/Service/Controller 层代码
-- 6. 更新前端 API 调用逻辑
-- 7. 测试数据迁移和新功能
