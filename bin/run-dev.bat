@echo off
echo.
echo [INFO] 启动开发环境服务...
echo.

cd %~dp0
cd ../ruoyi-admin/target

set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

java -jar %JAVA_OPTS% ^
  --spring.profiles.active=druid,dev ^
  ruoyi-admin.jar

pause
