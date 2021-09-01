package com.lsh.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * //TODO
 * <p>
 * RabbitMQ 的使用
 * 1. 引入amqp场景
 * 2.
 *
 * @Author: codestar
 * @Date 9/1/21 9:14 PM
 * @Description: 订单模块
 **/
@MapperScan("com.lsh.gulimall.order.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession
public class GulimallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }
}
