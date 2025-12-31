# Spring Boot 3.2.0 与 MyBatis-Plus 3.5.5 兼容性问题修复指南

## 问题描述

在运行 `mvn spring-boot:run` 时出现以下错误：

```
java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
    at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.getTypeForFactoryBeanFromAttributes(FactoryBeanRegistrySupport.java:86)
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getTypeForFactoryBean(AbstractAutowireCapableBeanFactory.java:838)
    at org.springframework.beans.factory.support.AbstractBeanFactory.isTypeMatch(AbstractBeanFactory.java:620)
    ...
```

## 问题原因

这是 Spring Boot 3.2.0 与 MyBatis-Plus 3.5.5 之间的已知兼容性问题。Spring Boot 3.2.0 改变了内部处理 FactoryBean 的方式，导致 MyBatis-Plus 的工厂 bean 类型检查失败。

## 解决方案

### 1. 更新 MybatisPlusConfig.java

在 `src/main/java/com/education/platform/config/MybatisPlusConfig.java` 中添加显式的 SqlSessionFactory 配置：

```java
package com.education.platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 修复Spring Boot 3.2.0与MyBatis-Plus 3.5.5的兼容性问题
     * 解决：Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfiguration(new MybatisConfiguration());
        factoryBean.setGlobalConfig(new com.baomidou.mybatisplus.core.config.GlobalConfig());
        factoryBean.setPlugins(mybatisPlusInterceptor);
        return factoryBean.getObject();
    }
}
```

### 2. 更新 application.yml

在 `src/main/resources/application.yml` 中添加：

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    call-setters-on-nulls: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.education.platform.entity
  # 修复Spring Boot 3.2.0兼容性问题
  type-aliases-super-type: java.lang.Object
```

### 3. 确保主应用类有 @MapperScan 注解

在 `EducationPlatformApplication.java` 中：

```java
@SpringBootApplication
@MapperScan("com.education.platform.mapper")  // 必须添加此注解
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "教育局综合信息平台API",
        version = "1.0.0",
        description = "城市教育局综合信息服务平台RESTful API文档"
    )
)
public class EducationPlatformApplication {
    // ...
}
```

## 验证修复

修复完成后，重新运行应用：

```bash
mvn spring-boot:run
```

或者：

```bash
mvn clean compile spring-boot:run
```

## 其他可能的解决方案

如果上述方案仍然无法解决问题，可以尝试以下替代方案：

### 方案A：降级 Spring Boot 版本

将 `pom.xml` 中的 Spring Boot 版本降级到 3.1.x：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.1.12</version>
    <relativePath/>
</parent>
```

### 方案B：升级 MyBatis-Plus 版本

使用更新版本的 MyBatis-Plus：

```xml
<properties>
    <mybatis-plus.version>3.5.6</mybatis-plus.version>
</properties>
```

### 方案C：禁用 Redis（如果不需要）

如果 Redis 服务未运行，可以暂时禁用相关配置：

```yaml
spring:
  redis:
    enabled: false
```

## 检查清单

- [ ] MybatisPlusConfig.java 已更新
- [ ] application.yml 已添加 type-aliases-super-type
- [ ] 主应用类有 @MapperScan 注解
- [ ] 所有 Mapper 接口有 @Mapper 注解
- [ ] MySQL 数据库服务正在运行
- [ ] Redis 服务（可选）正在运行

## 技术背景

- **Spring Boot**: 3.2.0
- **MyBatis-Plus**: 3.5.5
- **Java**: 17
- **MySQL**: 8.0.33

## 参考资料

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Spring Boot 3.2.0 Release Notes](https://github.com/spring-projects/spring-boot/releases/tag/v3.2.0)
