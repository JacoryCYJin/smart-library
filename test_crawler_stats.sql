-- 测试豆瓣任务统计
SELECT
    COALESCE(SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END), 0) AS pending,
    COALESCE(SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END), 0) AS processing,
    COALESCE(SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END), 0) AS completed,
    COALESCE(SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END), 0) AS failed
FROM douban_crawl_task;

-- 测试作者任务统计
SELECT
    COALESCE(SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END), 0) AS pending,
    COALESCE(SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END), 0) AS processing,
    COALESCE(SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END), 0) AS completed,
    COALESCE(SUM(CASE WHEN status = 3 OR status = 4 THEN 1 ELSE 0 END), 0) AS failed
FROM author_crawl_task;

-- 测试资源链接任务统计
SELECT
    COALESCE(SUM(CASE WHEN overall_status = 0 THEN 1 ELSE 0 END), 0) AS pending,
    COALESCE(SUM(CASE WHEN overall_status = 1 THEN 1 ELSE 0 END), 0) AS processing,
    COALESCE(SUM(CASE WHEN overall_status = 2 THEN 1 ELSE 0 END), 0) AS completed,
    COALESCE(SUM(CASE WHEN overall_status = 3 THEN 1 ELSE 0 END), 0) AS failed
FROM resource_link_crawl_task;

-- 查看实际的状态分布
SELECT status, COUNT(*) as count FROM douban_crawl_task GROUP BY status;
SELECT status, COUNT(*) as count FROM author_crawl_task GROUP BY status;
SELECT overall_status, COUNT(*) as count FROM resource_link_crawl_task GROUP BY overall_status;
