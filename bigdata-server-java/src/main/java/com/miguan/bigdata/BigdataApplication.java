package com.miguan.bigdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.miguan.**.mapper")
@SpringBootApplication
@EnableScheduling
public class BigdataApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataApplication.class, args);
	}

}
