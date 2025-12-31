@echo off
chcp 65001 >nul
echo ===================================
echo 教育局平台 - 修复验证脚本 (Windows)
echo ===================================
echo.

echo 1. 检查 MybatisPlusConfig.java...
findstr /C:"public SqlSessionFactory sqlSessionFactory" src\main\java\com\education\platform\config\MybatisPlusConfig.java >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ sqlSessionFactory Bean 配置存在
) else (
    echo    ✗ sqlSessionFactory Bean 配置缺失
    echo    请参考 FIX_SPRINGBOOT_320_ISSUES.md 添加配置
)

echo.
echo 2. 检查主应用类 @MapperScan 注解...
findstr /C:"@MapperScan" src\main\java\com\education\platform\EducationPlatformApplication.java >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ @MapperScan 注解存在
) else (
    echo    ✗ @MapperScan 注解缺失
    echo    请添加: @MapperScan("com.education.platform.mapper")
)

echo.
echo 3. 检查 application.yml 配置...
findstr /C:"type-aliases-super-type" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ type-aliases-super-type 配置存在
) else (
    echo    ✗ type-aliases-super-type 配置缺失
    echo    请添加: type-aliases-super-type: java.lang.Object
)

echo.
echo 4. 检查 pom.xml 版本...
findstr /C:"<version>3.2.0</version>" pom.xml >nul 2>&1
if %errorlevel% equ 0 (
    echo    Spring Boot: 3.2.0
) else (
    echo    Spring Boot: 其他版本
)

findstr /C:"<mybatis-plus.version>3.5.5</mybatis-plus.version>" pom.xml >nul 2>&1
if %errorlevel% equ 0 (
    echo    MyBatis-Plus: 3.5.5
    echo    ✓ 版本组合需要修复（已提供解决方案）
) else (
    echo    MyBatis-Plus: 其他版本
    echo    ℹ 当前版本组合
)

echo.
echo 5. 检查 Mapper 接口 @Mapper 注解...
dir /b src\main\java\com\education\platform\mapper\*.java 2>nul | find /c /v "" >nul
set MAPPER_COUNT=0
for /f %%a in ('dir /b src\main\java\com\education\platform\mapper\*.java 2^>nul ^| find /c /v ""') do set MAPPER_COUNT=%%a

echo    Mapper 接口总数: %MAPPER_COUNT%
echo    ℹ 请手动检查所有 Mapper 接口都有 @Mapper 注解

echo.
echo 6. 检查数据库配置...
findstr /C:"jdbc:mysql://localhost:3306/education_platform" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ 数据库 URL 配置正确
) else (
    echo    ⚠ 请检查数据库 URL 配置
)

echo.
echo 7. 检查 Redis 配置...
findstr /C:"redis:" src\main\resources\application.yml >nul 2>&1
if %errorlevel% equ 0 (
    echo    ✓ Redis 配置存在
    echo    注意: 如果 Redis 未运行，可临时禁用: spring.redis.enabled: false
) else (
    echo    ℹ Redis 配置未找到（可选）
)

echo.
echo ===================================
echo 验证完成！
echo ===================================
echo.
echo 下一步操作:
echo 1. 确保 MySQL 服务正在运行
echo 2. 确保数据库 education_platform 已创建
echo 3. 执行: mysql -u root -p education_platform ^< database/schema.sql
echo 4. 运行: mvn spring-boot:run
echo.
echo 如果仍有问题，请查看 FIX_SPRINGBOOT_320_ISSUES.md
echo.
pause
