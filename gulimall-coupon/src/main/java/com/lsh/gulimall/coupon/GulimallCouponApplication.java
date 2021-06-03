package com.lsh.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author codestar
 */
//@MapperScan("com.lsh.gulimall.coupon.dao")
@EnableDiscoveryClient
@SpringBootApplication()
//@ComponentScan("com.lsh")
public class GulimallCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallCouponApplication.class, args);
	}

}
