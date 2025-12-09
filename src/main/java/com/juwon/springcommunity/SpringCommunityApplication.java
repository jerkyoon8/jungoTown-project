package com.juwon.springcommunity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.juwon.springcommunity")
public class SpringCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCommunityApplication.class, args);
	}

}
