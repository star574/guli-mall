package com.lsh.gulimall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.lsh.gulimall.seckill.feign")
public class GulimallSeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);

    }
}
