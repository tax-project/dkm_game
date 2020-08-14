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
@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@EnableTransactionManagement
@EnableFeignClients
@MapperScan("com.dkm.*.dao")
public class GameBaseApplication extends SpringBootServletInitializer {

   public static void main(String[] args) {
      SpringApplication.run(GameBaseApplication.class, args);
   }


   /**
    * 打包
    * @param builder
    * @return
    */
   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      return builder.sources(GameBaseApplication.class);
   }

}
