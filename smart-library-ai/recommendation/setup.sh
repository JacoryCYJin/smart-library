#!/bin/bash

# 协同过滤推荐系统安装脚本

echo "=========================================="
echo "Smart Library 推荐系统安装"
echo "=========================================="

# 1. 检查 Python 版本
echo ""
echo "1. 检查 Python 版本..."
python3 --version

if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到 Python 3"
    echo "请先安装 Python 3.8 或更高版本"
    exit 1
fi

# 2. 安装依赖
echo ""
echo "2. 安装 Python 依赖..."
pip3 install -r requirements.txt

if [ $? -ne 0 ]; then
    echo "❌ 错误: 依赖安装失败"
    exit 1
fi

# 3. 配置环境变量
echo ""
echo "3. 配置环境变量..."
if [ ! -f .env ]; then
    cp .env.example .env
    echo "✅ 已创建 .env 文件"
    echo "⚠️  请编辑 .env 文件，填入数据库密码"
    echo ""
    echo "编辑命令: nano .env"
    echo "或者: vim .env"
else
    echo "✅ .env 文件已存在"
fi

echo ""
echo "=========================================="
echo "安装完成！"
echo "=========================================="
echo ""
echo "下一步："
echo "1. 编辑 .env 文件，填入数据库密码"
echo "2. 运行测试: python3 recommend.py stats"
echo "3. 生成推荐: python3 recommend.py generate"
echo ""
