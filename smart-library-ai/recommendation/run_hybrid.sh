#!/bin/bash

# 混合推荐系统运行脚本
# @author JacoryCyJin
# @date 2025/05/07

echo "========================================="
echo "  阅墨智能图书馆 - 混合推荐系统"
echo "========================================="
echo ""

# 检查 Python 环境
if ! command -v python3 &> /dev/null; then
    echo "❌ 错误: 未找到 Python 3"
    echo "请先安装 Python 3.11 或更高版本"
    exit 1
fi

# 检查虚拟环境
if [ ! -d "venv" ]; then
    echo "⚠️  未找到虚拟环境，正在创建..."
    python3 -m venv venv
    echo "✅ 虚拟环境创建完成"
fi

# 激活虚拟环境
echo "🔄 激活虚拟环境..."
source venv/bin/activate

# 安装依赖
echo "📦 检查依赖..."
pip install -q -r requirements.txt

# 检查 .env 文件
if [ ! -f ".env" ]; then
    echo "❌ 错误: 未找到 .env 文件"
    echo "请复制 .env.example 并配置数据库连接"
    exit 1
fi

echo ""
echo "========================================="
echo "  选择推荐模式"
echo "========================================="
echo "1. 混合推荐（协同过滤 + 内容推荐）[推荐]"
echo "2. 仅协同过滤推荐"
echo "3. 仅内容推荐（实验性）"
echo "4. 生成内容相似度矩阵"
echo ""
read -p "请选择模式 (1-4): " mode

case $mode in
    1)
        echo ""
        echo "🚀 开始生成混合推荐..."
        python3 hybrid_recommend.py --mode hybrid
        ;;
    2)
        echo ""
        echo "🚀 开始生成协同过滤推荐..."
        python3 hybrid_recommend.py --mode cf-only
        ;;
    3)
        echo ""
        echo "🚀 开始生成内容推荐..."
        python3 hybrid_recommend.py --mode content-only
        ;;
    4)
        echo ""
        echo "🚀 开始生成内容相似度矩阵..."
        python3 hybrid_recommend.py --mode content-matrix
        ;;
    *)
        echo "❌ 无效的选择"
        exit 1
        ;;
esac

echo ""
echo "========================================="
echo "  推荐生成完成！"
echo "========================================="
