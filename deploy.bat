@echo off
chcp 65001 > nul

REM 自动化部署脚本 - Windows版本
REM 用法: deploy.bat [环境] [服务器IP]

set PROFILE=%1
set SERVER_IP=%2
if "%PROFILE%"=="" set PROFILE=prod
if "%SERVER_IP%"=="" set SERVER_IP=47.115.216.25

set SERVER_USER=root
set SERVER_PATH=/root/paper
set LOCAL_JAR_PATH=ruoyi-admin\target\ruoyi-admin.jar
set APP_NAME=ruoyi-admin.jar

echo 🚀 开始自动化部署流程...
echo 📋 配置信息:
echo    - 环境: %PROFILE%
echo    - 服务器: %SERVER_IP%
echo    - 目标路径: %SERVER_PATH%
echo.

REM 步骤1: 本地打包
echo 📦 步骤1: 开始本地打包...
call mvn clean package -DskipTests -Pprod
if errorlevel 1 (
    echo ❌ 打包失败!
    exit /b 1
)
echo ✅ 本地打包完成

REM 检查jar文件是否存在
if not exist "%LOCAL_JAR_PATH%" (
    echo ❌ 找不到打包文件: %LOCAL_JAR_PATH%
    exit /b 1
)

REM 步骤2: 停止远程服务
echo 🛑 步骤2: 停止远程应用...
ssh %SERVER_USER%@%SERVER_IP% "cd %SERVER_PATH% && ./stop.sh"

REM 步骤3: 备份旧版本
echo 💾 步骤3: 备份旧版本...
for /f "tokens=1-4 delims=/ " %%a in ('date /t') do (
    for /f "tokens=1-2 delims=: " %%e in ('time /t') do (
        set BACKUP_NAME=backup_%%c%%a%%b_%%e%%f.jar
    )
)
ssh %SERVER_USER%@%SERVER_IP% "cd %SERVER_PATH% && if [ -f %APP_NAME% ]; then mv %APP_NAME% %BACKUP_NAME%; echo 备份文件: %BACKUP_NAME%; fi"

REM 步骤4: 上传新版本
echo 📤 步骤4: 上传新版本...
scp %LOCAL_JAR_PATH% %SERVER_USER%@%SERVER_IP%:%SERVER_PATH%/%APP_NAME%
if errorlevel 1 (
    echo ❌ 文件上传失败!
    exit /b 1
)
echo ✅ 新版本上传完成

REM 步骤5: 启动应用
echo 🚀 步骤5: 启动应用...
ssh %SERVER_USER%@%SERVER_IP% "cd %SERVER_PATH% && ./start.sh %PROFILE%"

REM 步骤6: 健康检查
echo 🔍 步骤6: 等待应用启动并进行健康检查...
timeout /t 10 /nobreak > nul

ssh %SERVER_USER%@%SERVER_IP% "cd %SERVER_PATH% && if pgrep -f %APP_NAME% > /dev/null; then echo ✅ 应用启动成功; else echo ❌ 应用启动失败; exit 1; fi"

echo.
echo 🎉 部署完成! 应用已成功启动
echo 🌐 访问地址: http://%SERVER_IP%:8080
echo 📝 查看日志: ssh %SERVER_USER%@%SERVER_IP% 'cd %SERVER_PATH% && tail -f logs/application.log'

pause
