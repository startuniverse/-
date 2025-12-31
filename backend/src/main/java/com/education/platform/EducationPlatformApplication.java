package com.education.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 城市教育局综合信息服务平台 - 主应用类
 *
 * @author Education Platform Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@SpringBootApplication
@MapperScan("com.education.platform.mapper")
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

    public static void main(String[] args) {
        SpringApplication.run(EducationPlatformApplication.class, args);
        System.out.println("=================================");
        System.out.println("教育局综合信息服务平台启动成功！");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================");
    }
}
