@echo off
echo ========================================
echo 教育平台完整修复脚本
echo ========================================
echo.
echo 修复内容：
echo 1. 添加deleted字段（解决MyBatis Plus报错）
echo 2. 修复用户密码
echo 3. 创建示例数据
echo 4. 移除密码长度限制（需手动修改前端）
echo.

set /p mysql_pass="请输入MySQL密码: "

if "%mysql_pass%"=="" (
    echo 错误：MySQL密码不能为空
    pause
    exit /b 1
)

echo.
echo [步骤1/3] 正在添加deleted字段...
mysql -u root -p%mysql_pass% education_platform < E:\shixun\backend\database\FIX_DELETED.sql
if %ERRORLEVEL% NEQ 0 (
    echo 注意：部分字段可能已存在，继续执行...
)

echo.
echo [步骤2/3] 正在修复用户密码和创建数据...
mysql -u root -p%mysql_pass% education_platform < E:\shixun\backend\database\FIX_ALL.sql
if %ERRORLEVEL% NEQ 0 (
    echo 数据库执行失败
    pause
    exit /b 1
)

echo.
echo [步骤3/3] 正在编译后端...
cd /d E:\shixun\backend
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo 编译失败
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
echo 下一步：
echo 1. 启动后端: mvn spring-boot:run
echo 2. 修改前端密码长度限制（3个文件）
echo 3. 测试所有功能
echo.
echo 详细说明：E:\shixun\最终修复说明.md
echo.
pause
