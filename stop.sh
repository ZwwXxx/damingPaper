#!/bin/bash

# 大明题库系统停止脚本
APP_NAME="ruoyi-admin.jar"

echo "正在停止大明题库系统..."

# 查找进程ID
PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 1
fi

# 停止进程
echo "正在停止进程 (PID: $PID)..."
kill -TERM $PID

# 等待进程结束
sleep 2

# 检查是否还在运行
if ps -p $PID > /dev/null 2>&1; then
    echo "强制停止进程..."
    kill -KILL $PID
fi

echo "✓ 大明题库系统已停止"
