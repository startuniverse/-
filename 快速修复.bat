@echo off
echo ========================================
echo 教育平台问题修复脚本
echo ========================================
echo.
echo 修复内容：
echo 1. admin密码改为adminadmin
echo 2. 用户123密码改为123
echo 3. 修复所有服务器内部错误
echo 4. 创建示例数据
echo.

set /p mysql_pass="请输入MySQL密码: "

if "%mysql_pass%"=="" (
    echo 错误：MySQL密码不能为空
    pause
    exit /b 1
)

echo.
echo [1/3] 正在执行数据库修复...
mysql -u root -p%mysql_pass% education_platform < E:\shixun\backend\database\FINAL_COMPLETE_FIX.sql
if %ERRORLEVEL% NEQ 0 (
    echo 数据库执行失败，请检查密码和数据库连接
    pause
    exit /b 1
)
echo ✓ 数据库修复完成

echo.
echo [2/3] 正在编译后端代码...
cd /d E:\shixun\backend
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败，请检查代码
    pause
    exit /b 1
)
echo ✓ 编译成功

echo.
echo [3/3] 修复完成！
echo.
echo 登录信息：
echo   Admin: admin / adminadmin
echo   用户123: 123 / 123
echo.
echo 下一步：
echo 1. 启动后端: mvn spring-boot:run
echo 2. 测试所有功能
echo.
echo 详细说明请查看：E:\shixun\最终修复说明.md
echo.
pause
