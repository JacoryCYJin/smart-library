# 协同过滤推荐系统使用指南

## 快速启动

```bash
# 1. 进入目录
cd smart-library-ai/recommendation

# 2. 激活虚拟环境
source venv/bin/activate

# 3. 运行推荐生成（首次或全量更新）
python recommend.py generate

# 4. 查看推荐结果
python recommend.py stats

# 5. 设置定时任务（可选）
./setup-cron.sh
```

## 概述

基于 Item-CF（物品协同过滤）算法的推荐系统，融合用户浏览、收藏、评分三种行为，为用户生成个性化图书推荐。

## 系统架构

- **Python 端**：离线计算推荐结果，保存到数据库
- **Spring Boot 端**：在线查询推荐结果，实时响应用户请求

## 环境配置

### 1. 安装依赖

```bash
# 进入推荐系统目录
cd smart-library-ai/recommendation

# 创建虚拟环境（首次）
python3 -m venv venv

# 激活虚拟环境
source venv/bin/activate

# 安装依赖
pip install -r requirements.txt
```

### 2. 配置数据库

编辑 `.env` 文件：

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=你的数据库密码
DB_NAME=smart_library

# 推荐算法参数
SIMILARITY_THRESHOLD=0.1
TOP_N_SIMILAR=20
TOP_N_RECOMMEND=10

# 行为权重
WEIGHT_BROWSE=1.0
WEIGHT_FAVORITE=3.0
WEIGHT_COMMENT=5.0
```

## 使用方式

### 方式 1: 交互式脚本（推荐）

```bash
cd smart-library-ai/recommendation
./run.sh
```

然后选择操作：
1. 查看统计信息
2. 测试单个用户推荐
3. 增量更新推荐（推荐）
4. 全量生成推荐

### 方式 2: 命令行

```bash
cd smart-library-ai/recommendation
source venv/bin/activate

# 查看统计信息
python recommend.py stats

# 测试单个用户推荐
python recommend.py test --user-id u0000000000000000000000000000001

# 增量更新（最近 24 小时活跃用户）
python recommend.py incremental --hours 24

# 全量生成推荐（所有用户）
python recommend.py generate
```

## 定时任务设置

### macOS/Linux (cron)

```bash
# 编辑 crontab
crontab -e

# 添加定时任务（每天凌晨 2 点运行增量更新）
0 2 * * * cd /Users/euygnehcnij/Code/smart-library/smart-library-ai/recommendation && source venv/bin/activate && python recommend.py incremental --hours 24 >> /tmp/recommend.log 2>&1
```

### 查看 cron 任务

```bash
# 查看当前 cron 任务
crontab -l

# 查看日志
tail -f /tmp/recommend.log
```

## 推荐算法说明

### 评分聚合

融合三种用户行为：
- **浏览**：权重 1.0，最多计 10 次
- **收藏**：权重 3.0
- **评分**：权重 5.0，归一化到 0-10 分

**公式**：
```
综合评分 = min(浏览次数, 10) × 1.0 + 收藏数 × 3.0 + 评分 × 5.0
```

### Item-CF 算法

1. **构建评分矩阵**：用户 × 资源
2. **计算物品相似度**：余弦相似度
3. **生成推荐**：基于用户历史行为和物品相似度

### 推荐策略

- **相似度阈值**：0.1（过滤低相似度物品）
- **Top-N 相似物品**：20 个
- **推荐数量**：每用户 10 条

## 数据库表

### recommend_result

推荐结果表：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | VARCHAR(32) | 用户 ID |
| resource_id | VARCHAR(32) | 资源 ID |
| score | DECIMAL(10,4) | 推荐分数 |
| rank | INT | 推荐排名 |
| ctime | DATETIME | 创建时间 |
| mtime | DATETIME | 更新时间 |

## Spring Boot API

### 获取推荐列表

```bash
GET /recommend/list?limit=10
Authorization: Bearer YOUR_JWT_TOKEN
```

**响应**：
```json
{
  "code": 0,
  "message": "成功",
  "data": [
    {
      "resourceId": "r001",
      "title": "图书标题",
      "coverUrl": "封面URL",
      ...
    }
  ]
}
```

### 获取统计信息

```bash
GET /recommend/stats
```

**响应**：
```json
{
  "code": 0,
  "message": "成功",
  "data": 150
}
```

## 常见问题

### 1. 数据库连接失败

检查 `.env` 文件中的数据库配置是否正确。

### 2. 没有推荐结果

- 确保数据库中有用户行为数据（浏览、收藏、评分）
- 运行 `python recommend.py generate` 生成初始推荐

### 3. 推荐结果不更新

- 检查 cron 任务是否正常运行：`crontab -l`
- 查看日志：`tail -f /tmp/recommend.log`

### 4. 虚拟环境问题

```bash
# 删除旧的虚拟环境
rm -rf venv

# 重新创建
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

## 性能优化建议

1. **增量更新优先**：日常使用增量更新，减少计算量
2. **定时任务**：在低峰期（凌晨）运行
3. **数据库索引**：确保 `recommend_result` 表有 `user_id` 索引
4. **缓存策略**：Spring Boot 端可以使用 Redis 缓存推荐结果

## 监控指标

- **推荐覆盖率**：有推荐结果的用户占比
- **推荐成功率**：成功生成推荐的用户占比
- **平均推荐数**：每个用户的平均推荐数量
- **计算耗时**：推荐生成的总耗时

## 文件说明

- `item_cf.py` - Item-CF 算法实现
- `rating_aggregator.py` - 评分聚合器
- `recommend.py` - 命令行入口
- `config.py` - 配置管理
- `requirements.txt` - Python 依赖
- `.env` - 环境变量配置
- `run.sh` - 交互式运行脚本
- `setup.sh` - 环境安装脚本

## 更新日志

- **2025-04-11**: 初始版本，实现 Item-CF 算法和混合推荐策略
