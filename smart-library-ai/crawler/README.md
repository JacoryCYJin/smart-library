# Smart Library 爬虫系统

基于任务队列的智能爬虫系统，支持断点续爬和进度追踪。

## 功能特性

- ✅ 基于任务队列的豆瓣图书爬取
- ✅ 基于任务队列的 Z-Library 电子书下载
- ✅ 支持断点续爬（中断后可继续）
- ✅ 进度追踪和状态管理
- ✅ ISBN 自动去重
- ✅ 文件自动上传到 MinIO

## 使用流程

### 1. 初始化数据库

首先需要初始化分类数据和爬虫任务表：

```bash
# 进入爬虫目录
cd smart-library-ai/crawler

# 初始化分类数据（只需运行一次）
python ../scripts/init_categories.py

# 创建爬虫任务表
mysql -u root -p smart_library < schema/crawler_task.sql
```

### 2. 初始化爬虫任务

#### 初始化豆瓣任务（推荐先做）

```bash
# 初始化豆瓣任务（每个分类爬取 20 本图书）
python crawler.py init-douban --books-per-category 20

# 或使用默认值 20
python crawler.py init-douban
```

这会为每个二级分类创建一个豆瓣爬取任务（目标 20 本）。

#### 初始化 ZLibrary 任务（可选，等豆瓣爬完后再做）

```bash
# 初始化 ZLibrary 任务
python crawler.py init-zlibrary
```

这会为所有没有文件的图书创建 ZLibrary 下载任务。

### 3. 爬取豆瓣图书

```bash
# 先测试 3 个分类
python crawler.py douban --limit 3

# 如果正常，爬取所有待处理的分类
python crawler.py douban
```

特性：
- 自动跳过已完成的分类
- 支持断点续爬（中断后再次运行会继续）
- 自动 ISBN 去重
- 自动建立图书-分类关联

### 4. 下载电子书文件（可选）

等豆瓣爬取了一些图书后，再运行：

```bash
# 先测试 5 本
python crawler.py zlibrary --limit 5

# 如果正常，下载所有
python crawler.py zlibrary
```

特性：
- 自动跳过已有文件的图书
- 支持 PDF、EPUB、MOBI 格式
- 自动上传到 MinIO
- 自动保存文件记录到数据库

## 数据库表结构

### 1. 豆瓣爬取任务表 (`douban_crawl_task`)

用于追踪豆瓣图书爬取进度（按分类）。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| category_id | VARCHAR(50) | 分类ID |
| category_name | VARCHAR(100) | 分类名称 |
| status | TINYINT | 状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 |
| progress | INT | 进度（已爬取数量） |
| target | INT | 目标数量 |
| error_msg | TEXT | 错误信息 |
| retry_count | INT | 重试次数 |
| ctime | DATETIME | 创建时间 |
| mtime | DATETIME | 更新时间 |

### 2. ZLibrary 下载任务表 (`zlibrary_download_task`)

用于追踪 ZLibrary 电子书文件下载进度（按资源）。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| resource_id | VARCHAR(50) | 资源ID |
| isbn | VARCHAR(20) | ISBN |
| title | VARCHAR(255) | 图书标题 |
| status | TINYINT | 状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源 |
| pdf_downloaded | TINYINT | PDF 是否已下载 |
| epub_downloaded | TINYINT | EPUB 是否已下载 |
| mobi_downloaded | TINYINT | MOBI 是否已下载 |
| error_msg | TEXT | 错误信息 |
| retry_count | INT | 重试次数 |
| ctime | DATETIME | 创建时间 |
| mtime | DATETIME | 更新时间 |

## 任务状态说明

### 豆瓣任务状态 (`douban_crawl_task.status`)
- `0` - 待处理：任务已创建，等待执行
- `1` - 处理中：任务正在执行
- `2` - 已完成：任务成功完成
- `3` - 失败：任务执行失败

### ZLibrary 任务状态 (`zlibrary_download_task.status`)
- `0` - 待处理：任务已创建，等待执行
- `1` - 处理中：任务正在执行
- `2` - 已完成：任务成功完成
- `3` - 失败：任务执行失败
- `4` - 无资源：ZLibrary 上找不到该图书

## 常见场景

### 场景 1：首次爬取（推荐流程）

```bash
# 1. 初始化分类（只需一次）
python ../scripts/init_categories.py

# 2. 创建任务表
mysql -u root -p smart_library < schema/crawler_task.sql

# 3. 初始化豆瓣任务（每个分类 20 本）
python crawler.py init-douban --books-per-category 20

# 4. 测试爬取 3 个分类
python crawler.py douban --limit 3

# 5. 如果正常，爬取所有分类
python crawler.py douban

# 6. 等豆瓣爬完后，初始化 ZLibrary 任务
python crawler.py init-zlibrary

# 7. 下载电子书
python crawler.py zlibrary --limit 5
python crawler.py zlibrary
```

### 场景 2：断点续爬

如果爬取过程中中断（Ctrl+C 或网络问题），直接重新运行即可：

```bash
# 继续爬取豆瓣（自动跳过已完成的任务）
python crawler.py douban

# 继续下载电子书
python crawler.py zlibrary
```

### 场景 3：增量爬取

如果想增加某个分类的图书数量：

```sql
-- 修改目标数量（例如从 20 改为 50）
UPDATE crawler_task 
SET target = 50, status = 0 
WHERE task_type = 1 AND category_name = '小说';
```

然后重新运行：

```bash
python crawler.py douban
```

### 场景 4：只测试豆瓣爬虫

```bash
# 1. 初始化豆瓣任务
python crawler.py init-douban --books-per-category 20

# 2. 测试爬取 3 个分类
python crawler.py douban --limit 3

# 3. 查看结果
mysql -u root -p smart_library -e "
SELECT category_name, progress, target 
FROM crawler_task 
WHERE task_type = 1 
ORDER BY mtime DESC 
LIMIT 10;
"
```

### 场景 4：重新爬取失败的任务

```sql
-- 将失败的任务重置为待处理
UPDATE crawler_task 
SET status = 0, error_msg = NULL 
WHERE status = 3;
```

然后重新运行爬虫。

## 配置说明

配置文件：`config.py`

```python
# 数据库配置
DB_HOST = 'localhost'
DB_PORT = 3306
DB_USER = 'root'
DB_PASSWORD = 'your_password'
DB_NAME = 'smart_library'

# MinIO 配置
MINIO_ENDPOINT = 'localhost:9000'
MINIO_ACCESS_KEY = 'minioadmin'
MINIO_SECRET_KEY = 'minioadmin'
MINIO_BUCKET_COVERS = 'library-covers'
MINIO_BUCKET_ATTACHMENTS = 'library-attachments'

# 爬虫配置
REQUEST_DELAY = 1  # 请求延迟（秒）
USER_AGENT = 'Mozilla/5.0 ...'
```

## 监控和调试

### 查看任务进度

```sql
-- 查看豆瓣任务进度
SELECT 
    category_name,
    progress,
    target,
    CONCAT(ROUND(progress * 100.0 / target, 2), '%') as completion,
    CASE status
        WHEN 0 THEN '待处理'
        WHEN 1 THEN '处理中'
        WHEN 2 THEN '已完成'
        WHEN 3 THEN '失败'
    END as status_text
FROM douban_crawl_task
ORDER BY id;

-- 查看 ZLibrary 任务统计
SELECT 
    CASE status
        WHEN 0 THEN '待处理'
        WHEN 1 THEN '处理中'
        WHEN 2 THEN '已完成'
        WHEN 3 THEN '失败'
        WHEN 4 THEN '无资源'
    END as status_text,
    COUNT(*) as count
FROM zlibrary_download_task
GROUP BY status;

-- 查看 ZLibrary 文件下载统计
SELECT 
    SUM(pdf_downloaded) as pdf_count,
    SUM(epub_downloaded) as epub_count,
    SUM(mobi_downloaded) as mobi_count,
    COUNT(*) as total_tasks
FROM zlibrary_download_task
WHERE status = 2;
```

### 查看失败任务

```sql
-- 查看豆瓣失败任务
SELECT 
    category_name,
    error_msg,
    retry_count,
    mtime
FROM douban_crawl_task
WHERE status = 3
ORDER BY mtime DESC;

-- 查看 ZLibrary 失败任务
SELECT 
    title,
    isbn,
    error_msg,
    retry_count,
    mtime
FROM zlibrary_download_task
WHERE status = 3
ORDER BY mtime DESC;
```

### 查看日志

```bash
# 实时查看日志
tail -f crawler.log

# 查看错误日志
grep ERROR crawler.log
```

## 注意事项

1. **请求频率**：豆瓣和 Z-Library 都有反爬机制，建议设置合理的延迟（1-2秒）
2. **网络稳定性**：建议在网络稳定的环境下运行，避免频繁中断
3. **存储空间**：确保 MinIO 有足够的存储空间
4. **数据库连接**：确保数据库连接池配置合理
5. **任务数量**：首次运行建议使用 `--limit` 参数测试

## 故障排查

### 问题 1：任务一直处于"处理中"状态

可能是上次运行异常退出，手动重置：

```sql
-- 重置豆瓣任务
UPDATE douban_crawl_task SET status = 0 WHERE status = 1;

-- 重置 ZLibrary 任务
UPDATE zlibrary_download_task SET status = 0 WHERE status = 1;
```

### 问题 2：ISBN 重复导致爬取失败

系统会自动跳过已存在的 ISBN，这是正常行为。

### 问题 3：MinIO 上传失败

检查 MinIO 配置和网络连接：

```bash
# 测试 MinIO 连接
curl http://localhost:9000/minio/health/live
```

### 问题 4：豆瓣返回 403

可能是请求过快被封禁，建议：
- 增加 `REQUEST_DELAY`
- 更换 User-Agent
- 使用代理

## 项目结构

```
crawler/
├── config.py              # 配置文件
├── crawler.py             # 主爬虫逻辑
├── README.md              # 使用说明
├── schema/
│   └── crawler_task.sql   # 任务表结构
├── crawlers/
│   ├── __init__.py
│   ├── douban_book_crawler.py    # 豆瓣爬虫
│   └── zlibrary_crawler.py       # ZLibrary 爬虫
└── utils/
    ├── __init__.py
    ├── db_helper.py       # 数据库工具
    └── minio_helper.py    # MinIO 工具
```
