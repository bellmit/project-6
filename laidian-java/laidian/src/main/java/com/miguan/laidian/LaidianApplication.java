package com.miguan.laidian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.miguan.laidian.mapper")
@EnableJpaRepositories(basePackages = "com.miguan.laidian.repositories")
@SpringBootApplication
@EnableScheduling
public class LaidianApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaidianApplication.class, args);
	}

}
