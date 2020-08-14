package com.dkm.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    @NotNull
    public Docket createRestApi() {
        return (new Docket(DocumentationType.SWAGGER_2)).apiInfo(this.apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.dkm")).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return (new ApiInfoBuilder()).title("后台管理 API")
                .version("1.0").build();
    }
}
