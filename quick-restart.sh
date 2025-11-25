#!/bin/bash

# 快速重启脚本 - 只重启，不重新打包
# 用法: ./quick-restart.sh [环境] [服务器IP]

PROFILE=${1:-prod}
SERVER_IP=${2:-47.115.216.25}
SERVER_USER="root"
SERVER_PATH="/root/paper"

echo "🔄 快速重启应用..."
echo "📋 服务器: $SERVER_IP, 环境: $PROFILE"

# 停止应用
echo "🛑 停止应用..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && ./stop.sh"

# 等待2秒
sleep 2

# 启动应用
echo "🚀 启动应用..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && ./start.sh $PROFILE"

# 等待启动
sleep 8

# 检查状态
echo "📋 检查应用状态..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && if pgrep -f ruoyi-admin.jar > /dev/null; then echo '✅ 应用启动成功'; else echo '❌ 应用启动失败'; fi"

echo "🌐 访问地址: http://$SERVER_IP:8080"
