#!/bin/bash

# 大明题库系统日志查看脚本
LOG_FILE="logs/application.log"

echo "正在查看大明题库系统日志..."
echo "日志文件：$LOG_FILE"
echo "按 Ctrl+C 退出日志查看"
echo "----------------------------------------"

if [ -f "$LOG_FILE" ]; then
    tail -f $LOG_FILE
else
    echo "日志文件不存在：$LOG_FILE"
    echo "请确保应用已启动"
fi
