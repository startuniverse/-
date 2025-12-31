@echo off
chcp 65001 >nul
echo ========================================
echo 密码问题一键修复
echo ========================================
echo.
echo 问题：admin/admin 和 123/123 登录失败
echo 原因：数据库密码哈希不正确
echo.

set /p mysql_pass="请输入MySQL密码: "

if "%mysql_pass%"=="" (
    echo 错误：MySQL密码不能为空
    pause
    exit /b 1
)

echo.
echo 正在修复密码...
mysql -u root -p%mysql_pass% education_platform < E:\shixun\backend\database\QUICK_PASSWORD_FIX.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ 密码修复成功！
    echo.
    echo 登录信息：
    echo   Admin: admin / adminadmin
    echo   用户123: 123 / 123
    echo   用户12345: 12345 / 123123
    echo.
    echo 请测试登录！
) else (
    echo.
    echo ✗ 修复失败，请检查：
    echo 1. MySQL密码是否正确
    echo 2. 数据库名是否为 education_platform
    echo 3. 数据库服务是否启动
)

echo.
pause
