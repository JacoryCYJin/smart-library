#!/bin/bash

# 协同过滤推荐系统 - Cron 任务设置脚本

echo "=========================================="
echo "设置推荐系统定时任务"
echo "=========================================="
echo ""

# 获取当前脚本所在目录的绝对路径
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "推荐系统路径: $SCRIPT_DIR"
echo ""

# 生成 cron 任务命令
CRON_CMD="0 2 * * * cd $SCRIPT_DIR && source venv/bin/activate && python recommend.py incremental --hours 24 >> /tmp/recommend.log 2>&1"

echo "将添加以下 cron 任务："
echo "$CRON_CMD"
echo ""
echo "说明："
echo "  - 每天凌晨 2 点运行"
echo "  - 增量更新最近 24 小时活跃用户的推荐"
echo "  - 日志输出到 /tmp/recommend.log"
echo ""

read -p "是否继续？(y/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]
then
    # 检查是否已存在相同的 cron 任务
    if crontab -l 2>/dev/null | grep -q "recommend.py incremental"; then
        echo "警告: 已存在推荐系统的 cron 任务"
        echo ""
        crontab -l | grep "recommend.py"
        echo ""
        read -p "是否替换现有任务？(y/n) " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]
        then
            # 删除旧任务
            crontab -l | grep -v "recommend.py" | crontab -
            echo "已删除旧任务"
        else
            echo "取消操作"
            exit 0
        fi
    fi
    
    # 添加新任务
    (crontab -l 2>/dev/null; echo "$CRON_CMD") | crontab -
    
    echo ""
    echo "✓ Cron 任务已添加成功！"
    echo ""
    echo "查看当前 cron 任务："
    echo "  crontab -l"
    echo ""
    echo "查看日志："
    echo "  tail -f /tmp/recommend.log"
    echo ""
    echo "手动测试运行："
    echo "  cd $SCRIPT_DIR"
    echo "  source venv/bin/activate"
    echo "  python recommend.py incremental --hours 24"
    echo ""
else
    echo "取消操作"
fi
