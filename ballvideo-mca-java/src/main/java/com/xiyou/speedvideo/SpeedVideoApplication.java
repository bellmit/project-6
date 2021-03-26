package com.xiyou.speedvideo;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.xiyou.speedvideo.mapper")
public class SpeedVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeedVideoApplication.class, args);
		log.warn("============================= 程序启动成功 =================================");
	}

}
