#!/bin/bash

# Spring Boot 3.2.0 + MyBatis-Plus 3.5.5 兼容性修复验证脚本

echo "==================================="
echo "教育局平台 - 修复验证脚本"
echo "==================================="
echo ""

# 检查 MybatisPlusConfig.java
echo "1. 检查 MybatisPlusConfig.java..."
if grep -q "public SqlSessionFactory sqlSessionFactory" src/main/java/com/education/platform/config/MybatisPlusConfig.java; then
    echo "   ✓ sqlSessionFactory Bean 配置存在"
else
    echo "   ✗ sqlSessionFactory Bean 配置缺失"
    echo "   请参考 FIX_SPRINGBOOT_320_ISSUES.md 添加配置"
fi

# 检查 @MapperScan 注解
echo ""
echo "2. 检查主应用类 @MapperScan 注解..."
if grep -q "@MapperScan" src/main/java/com/education/platform/EducationPlatformApplication.java; then
    echo "   ✓ @MapperScan 注解存在"
else
    echo "   ✗ @MapperScan 注解缺失"
    echo "   请添加: @MapperScan(\"com.education.platform.mapper\")"
fi

# 检查 application.yml
echo ""
echo "3. 检查 application.yml 配置..."
if grep -q "type-aliases-super-type" src/main/resources/application.yml; then
    echo "   ✓ type-aliases-super-type 配置存在"
else
    echo "   ✗ type-aliases-super-type 配置缺失"
    echo "   请添加: type-aliases-super-type: java.lang.Object"
fi

# 检查 pom.xml 版本
echo ""
echo "4. 检查 pom.xml 依赖版本..."
SPRING_BOOT_VERSION=$(grep -oP '<version>\K[0-9]+\.[0-9]+\.[0-9]+' pom.xml | head -1)
MYBATIS_PLUS_VERSION=$(grep -oP '<mybatis-plus.version>\K[0-9]+\.[0-9]+\.[0-9]+' pom.xml)

echo "   Spring Boot: $SPRING_BOOT_VERSION"
echo "   MyBatis-Plus: $MYBATIS_PLUS_VERSION"

if [ "$SPRING_BOOT_VERSION" = "3.2.0" ] && [ "$MYBATIS_PLUS_VERSION" = "3.5.5" ]; then
    echo "   ✓ 版本组合需要修复（已提供解决方案）"
else
    echo "   ℹ 当前版本组合"
fi

# 检查 Mapper 接口
echo ""
echo "5. 检查 Mapper 接口 @Mapper 注解..."
MAPPER_COUNT=$(find src/main/java/com/education/platform/mapper -name "*.java" | wc -l)
MAPPER_WITH_ANNOTATION=$(grep -l "@Mapper" src/main/java/com/education/platform/mapper/*.java 2>/dev/null | wc -l)

echo "   Mapper 接口总数: $MAPPER_COUNT"
echo "   带 @Mapper 注解: $MAPPER_WITH_ANNOTATION"

if [ "$MAPPER_COUNT" -eq "$MAPPER_WITH_ANNOTATION" ]; then
    echo "   ✓ 所有 Mapper 接口都有 @Mapper 注解"
else
    echo "   ⚠ 部分 Mapper 接口缺少 @Mapper 注解"
fi

# 检查数据库配置
echo ""
echo "6. 检查数据库配置..."
if grep -q "jdbc:mysql://localhost:3306/education_platform" src/main/resources/application.yml; then
    echo "   ✓ 数据库 URL 配置正确"
else
    echo "   ⚠ 请检查数据库 URL 配置"
fi

# 检查 Redis 配置
echo ""
echo "7. 检查 Redis 配置..."
if grep -q "spring:" src/main/resources/application.yml && grep -q "redis:" src/main/resources/application.yml; then
    echo "   ✓ Redis 配置存在"
    echo "   注意: 如果 Redis 未运行，可临时禁用: spring.redis.enabled: false"
else
    echo "   ℹ Redis 配置未找到（可选）"
fi

echo ""
echo "==================================="
echo "验证完成！"
echo "==================================="
echo ""
echo "下一步操作:"
echo "1. 确保 MySQL 服务正在运行"
echo "2. 确保数据库 education_platform 已创建"
echo "3. 执行: mysql -u root -p education_platform < database/schema.sql"
echo "4. 运行: mvn spring-boot:run"
echo ""
echo "如果仍有问题，请查看 FIX_SPRINGBOOT_320_ISSUES.md"
