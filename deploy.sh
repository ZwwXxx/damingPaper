#!/bin/bash

# 自动化部署脚本
# 用法: ./deploy.sh [环境] [服务器IP]
# 示例: ./deploy.sh prod 47.115.216.25

set -e

# 配置参数
PROFILE=${1:-prod}
SERVER_IP=${2:-47.115.216.25}
SERVER_USER="root"
SERVER_PATH="/root/paper"
LOCAL_JAR_PATH="ruoyi-admin/target/ruoyi-admin.jar"
APP_NAME="ruoyi-admin.jar"

echo "🚀 开始自动化部署流程..."
echo "📋 配置信息:"
echo "   - 环境: $PROFILE"
echo "   - 服务器: $SERVER_IP"
echo "   - 目标路径: $SERVER_PATH"
echo ""

# 步骤1: 本地打包
echo "📦 步骤1: 开始本地打包..."
mvn clean package -DskipTests -Pprod
if [ $? -ne 0 ]; then
    echo "❌ 打包失败!"
    exit 1
fi
echo "✅ 本地打包完成"

# 检查jar文件是否存在
if [ ! -f "$LOCAL_JAR_PATH" ]; then
    echo "❌ 找不到打包文件: $LOCAL_JAR_PATH"
    exit 1
fi

# 步骤2: 停止远程服务
echo "🛑 步骤2: 停止远程应用..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && ./stop.sh"
echo "✅ 远程应用已停止"

# 步骤3: 备份旧版本
echo "💾 步骤3: 备份旧版本..."
BACKUP_NAME="backup_$(date +%Y%m%d_%H%M%S).jar"
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && if [ -f $APP_NAME ]; then mv $APP_NAME $BACKUP_NAME; echo '备份文件: $BACKUP_NAME'; fi"

# 步骤4: 上传新版本
echo "📤 步骤4: 上传新版本..."
scp $LOCAL_JAR_PATH $SERVER_USER@$SERVER_IP:$SERVER_PATH/$APP_NAME
if [ $? -ne 0 ]; then
    echo "❌ 文件上传失败!"
    exit 1
fi
echo "✅ 新版本上传完成"

# 步骤5: 启动应用
echo "🚀 步骤5: 启动应用..."
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && ./start.sh $PROFILE"
if [ $? -ne 0 ]; then
    echo "❌ 应用启动失败!"
    exit 1
fi

# 步骤6: 健康检查
echo "🔍 步骤6: 等待应用启动并进行健康检查..."
sleep 10

# 检查应用是否正常启动
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && if pgrep -f $APP_NAME > /dev/null; then echo '✅ 应用启动成功'; else echo '❌ 应用启动失败'; exit 1; fi"

# 检查日志
echo "📋 最新日志:"
ssh $SERVER_USER@$SERVER_IP "cd $SERVER_PATH && tail -n 20 logs/application.log"

echo ""
echo "🎉 部署完成! 应用已成功启动"
echo "🌐 访问地址: http://$SERVER_IP:8080"
echo "📝 查看日志: ssh $SERVER_USER@$SERVER_IP 'cd $SERVER_PATH && tail -f logs/application.log'"
