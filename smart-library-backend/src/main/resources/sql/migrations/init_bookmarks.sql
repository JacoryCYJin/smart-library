-- ==========================================
-- 漂流书签初始化数据
-- 基于真实图书数据创建书签
-- ==========================================

-- 清空现有书签数据（如果需要重新初始化）
-- TRUNCATE TABLE bookmark;

-- 插入书签数据（根据真实数据库中的 resource 数据）
-- 表结构：bookmark_id, resource_id, content, author_name, book_title, click_count, status, ctime, mtime, deleted
INSERT INTO bookmark (bookmark_id, resource_id, content, author_name, book_title, click_count, status, ctime, mtime, deleted) VALUES
('bm49536bf1a75744988f2f6edaca563bc2', '49536bf1a75744988f2f6edaca563bc2', '世上许多玩笑，注定要流着泪把它开完。', '刘震云', '咸的玩笑', 0, 1, NOW(), NOW(), 0),
('bm5d3a9bce639049fb99d13227696e0c8f', '5d3a9bce639049fb99d13227696e0c8f', '一出一走，延宕百年。', '刘震云', '一句顶一万句', 0, 1, NOW(), NOW(), 0),
('bme10b1881834342aab848bb96cf3ab98e', 'e10b1881834342aab848bb96cf3ab98e', '这一次，欢乐100%留给读者！', '余华', '卢克明的偷偷一笑', 0, 1, NOW(), NOW(), 0),
('bm3768b4f009114855ab106baaa2991b74', '3768b4f009114855ab106baaa2991b74', '桃花源里住的都是妖怪，鸡毛蒜皮，纷争不断。', '马伯庸', '桃花源没事儿', 0, 1, NOW(), NOW(), 0),
('bm597b3bfb204445afbae055122920cc3c', '597b3bfb204445afbae055122920cc3c', '当大闹天宫的真相重新浮出水面，牵扯出无数因果。', '马伯庸', '太白金星有点烦', 0, 1, NOW(), NOW(), 0),
('bmc21e5db6e00e4d40ae24db3ffba32c59', 'c21e5db6e00e4d40ae24db3ffba32c59', '死亡面前人人平等，活着才是一场盛大的抗争。', '郑智我', '父亲的解放日志', 0, 1, NOW(), NOW(), 0),
('bm1ec0ea75067945d4ab052512efada786', '1ec0ea75067945d4ab052512efada786', '就算失败，我也想知道，自己倒在距离终点多远的地方。', '马伯庸', '长安的荔枝', 0, 1, NOW(), NOW(), 0),
('bm6973936128e243db9258b57aae7baa4e', '6973936128e243db9258b57aae7baa4e', '女人们不再是仅供同情、怜悯的角色，也不再是装饰男人壮丽生活的配角。', '崔恩荣', '明亮的夜晚', 0, 1, NOW(), NOW(), 0),
('bmd2e1abfa108a42048854aa18d0de3991', 'd2e1abfa108a42048854aa18d0de3991', '我想通过《素食者》刻画一个誓死不愿加入人类群体的女性。', '韩江', '素食者', 0, 1, NOW(), NOW(), 0),
('bmf460d8a2fdf64b2d83443293c7f2eb8d', 'f460d8a2fdf64b2d83443293c7f2eb8d', '美食不会骗人，每个人在它面前，都会露出本性。', '马伯庸', '食南之徒', 0, 1, NOW(), NOW(), 0);

-- 验证插入结果
SELECT COUNT(*) as bookmark_count FROM bookmark;

-- 验证 resource_id 是否存在（可选，用于检查数据完整性）
-- SELECT b.bookmark_id, b.resource_id, r.title 
-- FROM bookmark b 
-- LEFT JOIN resource r ON b.resource_id = r.resource_id 
-- WHERE r.resource_id IS NULL;
