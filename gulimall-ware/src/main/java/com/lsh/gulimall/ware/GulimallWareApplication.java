package com.lsh.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * //TODO
 * @Author codestar
 * @Date 23:16 2021/6/1
 * @Description
 **/
@MapperScan("com.lsh.gulimall.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallWareApplication.class, args);
	}

}
