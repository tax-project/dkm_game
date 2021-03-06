package com.dkm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author qf
 */
@SpringBootApplication(scanBasePackages = {"com.dkm","springfox.documentation.swagger.web"})
@EnableEurekaClient
@EnableSwagger2
@EnableTransactionManagement
@EnableFeignClients
@MapperScan("com.dkm.*.mappers")
public class GameAdminApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GameAdminApplication.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(GameAdminApplication.class);
    }

}
