package com.lsh.gulimall.seckill.scheduled;

import com.lsh.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * //TODO
 *
 * @Description: 秒杀商品定时上架 每天晚上3点 上架最近3天需要秒杀的商品
 * @Author: shihe
 * @Date: 2021-10-20 00:00
 */
@Service
//@EnableScheduling
//@Async
@Slf4j
public class SeckillSkuScheduled {

	@Autowired
	SeckillService seckillService;

	/**
	 * //TODO
	 *
	 * @param
	 * @return: void
	 * @Description: 每天晚上3点上架
	 */
	@Scheduled(cron = "10 * * * * ?")
	public void uploadSeckillSkuLatest3Days() {
		//1、重复上架无需处理
		seckillService.uploadSeckillSkuLatest3Days();
	}

}
