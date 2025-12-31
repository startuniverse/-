# 快速修复指南

## 当前状态
已将 Spring Boot 降级到 3.1.12，但仍然遇到 MyBatis 解析错误。

## 已完成的修复

### 1. ✅ Spring Boot 版本降级
- `pom.xml`: 3.2.0 → 3.1.12

### 2. ✅ 禁用 MyBatis-Plus 自动配置
- `application.yml`: 添加 `spring.autoconfigure.exclude: com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration`

### 3. ✅ 创建自定义 MyBatis 配置
- 新建 `MyBatisConfig.java` 完全控制配置

## 下一步尝试

### 方案 A：检查数据库连接
确保 MySQL 服务正在运行，并且数据库已创建：

```sql
CREATE DATABASE IF NOT EXISTS education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 方案 B：临时禁用所有 Mapper XML
修改 `MyBatisConfig.java`，暂时注释掉 XML 文件加载：

```java
// sessionFactory.setMapperLocations(
//     new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml")
// );
```

### 方案 C：使用纯 Java 配置（无 XML）
为每个 Mapper 创建纯 Java 实现，避免 XML 解析问题。

### 方案 D：检查具体错误
运行时添加调试参数获取详细错误：

```bash
mvn spring-boot:run -X
```

## 可能的问题根源

1. **数据库连接失败** - 检查 MySQL 是否运行
2. **数据库表不存在** - 需要先执行 schema.sql
3. **XML 文件路径错误** - 检查 mapper 目录结构
4. **Mapper 接口与 XML 不匹配** - 检查 namespace 和 id

## 快速测试

1. **验证数据库**：
   ```bash
   mysql -u root -p -e "SHOW DATABASES LIKE 'education_platform';"
   ```

2. **验证表结构**：
   ```bash
   mysql -u root -p education_platform -e "SHOW TABLES;"
   ```

3. **运行应用**：
   ```bash
   cd E:\shixun\backend
   mvn clean compile
   mvn spring-boot:run
   ```

如果仍然失败，请提供完整的错误日志，特别是：
- 数据库连接错误
- XML 解析错误
- Mapper 绑定错误
