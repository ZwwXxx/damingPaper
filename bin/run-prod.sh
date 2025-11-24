#!/bin/bash

echo "========================================"
echo "启动生产环境服务..."
echo "========================================"

# 应用名称
APP_NAME=ruoyi-admin

# 应用jar包路径
APP_JAR=/opt/app/${APP_NAME}.jar

# JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"

# Spring Boot配置
SPRING_OPTS="--spring.profiles.active=druid,prod"

# 日志文件
LOG_FILE=/opt/app/logs/${APP_NAME}.log

# 检查应用是否已启动
PID=$(ps -ef | grep ${APP_JAR} | grep -v grep | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "应用正在运行，PID: $PID"
    echo "正在停止..."
    kill -15 $PID
    sleep 3
    
    # 检查是否已停止
    PID=$(ps -ef | grep ${APP_JAR} | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "强制停止..."
        kill -9 $PID
    fi
    echo "应用已停止"
fi

# 创建日志目录
mkdir -p /opt/app/logs

# 启动应用
echo "启动应用..."
nohup java ${JAVA_OPTS} -jar ${APP_JAR} ${SPRING_OPTS} > ${LOG_FILE} 2>&1 &

# 等待启动
sleep 3

# 检查启动状态
PID=$(ps -ef | grep ${APP_JAR} | grep -v grep | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "========================================"
    echo "✅ 应用启动成功！"
    echo "PID: $PID"
    echo "日志文件: ${LOG_FILE}"
    echo "查看日志: tail -f ${LOG_FILE}"
    echo "========================================"
else
    echo "========================================"
    echo "❌ 应用启动失败！"
    echo "请查看日志: ${LOG_FILE}"
    echo "========================================"
    exit 1
fi
