@echo off
echo ========================================
echo 教育平台登录授权修复部署脚本
echo ========================================
echo.

echo [1/4] 正在编译项目...
cd /d E:\shixun\backend
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败，请检查代码错误
    pause
    exit /b 1
)
echo ✓ 编译成功

echo.
echo [2/4] 请手动执行数据库脚本
echo.
echo 请在MySQL中执行以下命令：
echo   USE education_platform;
echo   SOURCE E:/shixun/backend/database/FULL_FIX.sql;
echo.
echo 按任意键继续...
pause >nul

echo.
echo [3/4] 启动应用
echo.
echo 选择启动方式：
echo 1. 直接运行 (mvn spring-boot:run)
echo 2. 打包后运行 (mvn package + java -jar)
echo 3. 退出
echo.
set /p choice="请输入选择 (1/2/3): "

if "%choice%"=="1" (
    echo 正在启动应用...
    call mvn spring-boot:run
) else if "%choice%"=="2" (
    echo 正在打包...
    call mvn package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo 打包失败
        pause
        exit /b 1
    )
    echo 正在启动应用...
    java -jar target/education-platform-1.0.0.jar
) else (
    echo 已退出
)

echo.
echo 部署完成！
pause
