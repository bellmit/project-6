package com.miguan.idmapping;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@MapperScan("com.miguan.idmapping.mapper")
@Slf4j
public class IdMappingApp {

    public static void main(String[] args) {
        SpringApplication.run(IdMappingApp.class, args);
        log.warn("启动完成");
    }

    /**
     * @Description 处理跨域请求
     **/
    @Configuration
    public class CorsConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowCredentials(true)
                    .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                    .maxAge(3600);
        }
    }

}
