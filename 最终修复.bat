@echo off
chcp 65001 >nul
echo ========================================
echo 教育平台最终修复脚本
echo ========================================
echo.
echo 解决报错.md中的所有问题：
echo 1. 数据库表缺少deleted字段
echo 2. 密码长度限制
echo 3. 用户密码修复
echo.

set /p mysql_pass="请输入MySQL密码: "

if "%mysql_pass%"=="" (
    echo 错误：MySQL密码不能为空
    pause
    exit /b 1
)

echo.
echo [1/2] 执行数据库修复...
mysql -u root -p%mysql_pass% education_platform < E:\shixun\backend\database\FINAL_FIX.sql
if %ERRORLEVEL% NEQ 0 (
    echo 数据库执行失败，请检查：
    echo 1. MySQL密码是否正确
    echo 2. 数据库名是否为 education_platform
    echo 3. 数据库服务是否启动
    pause
    exit /b 1
)

echo.
echo [2/2] 编译后端...
cd /d E:\shixun\backend
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败，请检查代码
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ 修复完成！
echo ========================================
echo.
echo 登录信息：
echo   Admin: admin / adminadmin
echo   用户123: 123 / 123
echo.
echo 下一步操作：
echo 1. 启动后端: mvn spring-boot:run
echo 2. 修改前端密码限制（3个文件）
echo 3. 测试所有功能
echo.
echo 前端修改文件：
echo   - frontend/src/views/Login.vue
echo   - frontend/src/views/Register.vue
echo   - frontend/src/views/campus/Profile.vue
echo.
echo 详细说明：E:\shixun\最终修复说明.md
echo.
pause
