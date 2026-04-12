#!/bin/bash

# 协同过滤推荐系统运行脚本

echo "=========================================="
echo "Smart Library 推荐系统"
echo "=========================================="
echo ""
echo "选择操作："
echo "1. 查看统计信息"
echo "2. 测试单个用户推荐"
echo "3. 增量更新推荐（推荐）"
echo "4. 全量生成推荐"
echo ""
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo "正在查看统计信息..."
        python3 recommend.py stats
        ;;
    2)
        echo ""
        read -p "请输入用户ID: " user_id
        echo "正在为用户 $user_id 生成测试推荐..."
        python3 recommend.py test --user-id "$user_id"
        ;;
    3)
        echo ""
        read -p "查询最近几小时的活跃用户？(默认1): " hours
        hours=${hours:-1}
        echo "正在增量更新推荐（最近 $hours 小时）..."
        python3 recommend.py incremental --hours "$hours"
        ;;
    4)
        echo ""
        read -p "确认全量生成推荐？这可能需要几分钟 (y/n): " confirm
        if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
            echo "正在全量生成推荐..."
            python3 recommend.py generate
        else
            echo "已取消"
        fi
        ;;
    *)
        echo "无效选项"
        exit 1
        ;;
esac

echo ""
echo "=========================================="
echo "操作完成！"
echo "=========================================="
