package com.zntk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 接口文档配置。
 *
 * 这个配置类负责定义接口文档首页展示的项目标题、版本、说明等信息。
 * 启动项目后访问 http://localhost:8080/doc.html 即可查看接口文档。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI zntkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ZNTK 智能题库与 AI 组卷系统接口文档")
                        .description("基于 Spring Boot、MyBatis-Plus、Redis、JWT 的智能题库后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ZNTK")
                                .email("zntk@example.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
