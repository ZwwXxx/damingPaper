#!/bin/bash

# 大明题库系统启动脚本
APP_NAME="ruoyi-admin.jar"

# 环境配置 (可选择: dev, prod, test)


# JVM配置 (先加载druid基础配置，再加载环境配置)
JVM_OPTS="-Xms512m -Xmx1024m -Duser.timezone=Asia/Shanghai -Dspring.profiles.active=druid,prod"

echo "使用环境配置: ${PROFILE}"

echo "正在启动大明题库系统..."

# 检查进程是否已运行
PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用已在运行中 (PID: $PID)"
    exit 1
fi

# 检查jar文件是否存在
if [ ! -f "$APP_NAME" ]; then
    echo "错误：找不到 $APP_NAME 文件！"
    exit 1
fi

# 创建日志目录
mkdir -p logs

# 启动应用
nohup java $JVM_OPTS -jar $APP_NAME > logs/application.log 2>&1 &
echo "✓ 大明题库系统启动成功！"
echo "访问地址：http://localhost:8080"
echo "日志文件：logs/application.log"