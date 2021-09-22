package com.lsh.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * //TODO
 *
 * @Author: codestar
 * @Date: 2021-09-09 23:57:51
 * @Description:
 */
@EnableFeignClients(basePackages = {"com.lsh.gulimall.auth.feign"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession // 开启spring session
public class GulimallAuthServerApplication {

	public static void main(String[] args) {

		System.out.println("啦啦啦啦啦啦");
		SpringApplication.run(GulimallAuthServerApplication.class, args);
	}

}
