package com.xxl.core.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class swaggerConfig {
    @Configuration
    @EnableSwagger2
    public class SwaggerConfig {
        /**
         * 创建API应用
         * apiInfo() 增加API相关信息
         * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
         * 本例采用指定扫描的包路径来定义指定要建立API的目录。
         *
         * @return
         */
        @Bean
        public Docket webApiConfig() {

            return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("webApi")
                    .apiInfo(adminApiInfo())
                    .select()
                    //.paths(Predicates.none(PathSelectors.regex("/admin/.*")))
                    .paths(Predicates.and(PathSelectors.regex("/api/.*")))

                    //.paths(Predicate.not(PathSelectors.regex("/error.*")))
                    .build();

        }

        private ApiInfo webApiInfo() {

            return new ApiInfoBuilder()
                    .title("网站-课程中心API文档")
                    .description("本文档描述了课程中心微服务接口定义")
                    .version("1.0")
                    .contact(new Contact("Helen", "http://atguigu.com", "1576777438@qq.com"))
                    .build();
        }

        @Bean
        public Docket adminApiConfig() {

            return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("adminApi")
                    .apiInfo(webApiInfo())
                    .select()
//                .paths(Predicates.none(PathSelectors.regex("/admin/.*")))
                    .paths(Predicates.and(PathSelectors.regex("/admin/.*")))

                    //.paths(Predicate.not(PathSelectors.regex("/error.*")))
                    .build();

        }

        private ApiInfo adminApiInfo(){

            return new ApiInfoBuilder()
                    .title("尚融宝后台管理系统-API文档")
                    .description("本文档描述了尚融宝后台管理系统接口")
                    .version("1.0")
                    .contact(new Contact("Helen", "http://atguigu.com", "55317332@qq.com"))
                    .build();
        }

    }
}