package com.lsh.gulimall.seckill.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.seckill.feign.CouponFeignClient;
import com.lsh.gulimall.seckill.service.SeckillService;
import com.lsh.gulimall.seckill.vo.SeckillSessionWithSkus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignClient couponFeignClient;

    @Override
    public void uploadSeckillSkuLatet3Days() {
        // 1、扫描需要秒杀的服务 远程调用coupon服务
        R r = couponFeignClient.getLatest3DaySession();
        if (r.getCode() == 0) {
            SeckillSessionWithSkus seckillSessionWithSkus = r.getData(new TypeReference<SeckillSessionWithSkus>() {
            });
            log.warn("需要上架的活动及其商品{}", seckillSessionWithSkus);
        }
    }
}