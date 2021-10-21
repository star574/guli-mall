package com.lsh.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.seckill.feign.CouponFeignClient;
import com.lsh.gulimall.seckill.feign.ProductFeignClient;
import com.lsh.gulimall.seckill.service.SeckillService;
import com.lsh.gulimall.seckill.to.SeckillSkuRedisTo;
import com.lsh.gulimall.seckill.vo.SeckillSessionWithSkus;
import com.lsh.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

	@Autowired
	CouponFeignClient couponFeignClient;

	/*
	 * RedisTemplate使用的是 JdkSerializationRedisSerializer
	 * StringRedisTemplate使用的是 StringRedisSerializer
	 * */
	@Autowired
	StringRedisTemplate redisTemplatel;

	@Autowired
	ProductFeignClient productFeignClient;

	@Autowired
	RedissonClient redissonClient;

	private final String SESSION_CACHE_PREFIX = "seckill:session:";
	private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
	private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // 加上商品随机码


	@Override
	public void uploadSeckillSkuLatest3Days() {
		// 1、扫描需要秒杀的服务 远程调用coupon服务
		R r = couponFeignClient.getLatest3DaySession();
		if (r.getCode() == 0) {
			List<SeckillSessionWithSkus> skuHasStockList = r.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
			});
			log.warn("需要上架的活动及其商品{}", skuHasStockList);
			// 上架商品 缓存到redis
			if (skuHasStockList != null && !skuHasStockList.isEmpty()) {
				// 1、活动信息
				saveSessionInfos(skuHasStockList);
				// 2、活动商品信息
				saveSessionSkuInfos(skuHasStockList);
			}

		}
	}

	/**
	 * //TODO
	 *
	 * @param skuHasStockList
	 * @return: void
	 * @Description: redis存放每个活动以及对应的商品skuId
	 * KEY 活动id活动开始时间以及结束时间
	 * <p>
	 * redisTemplatel.opsForList().leftPushAll(key, skuIds) 从左至右存放一个list
	 */
	private void saveSessionInfos(List<SeckillSessionWithSkus> skuHasStockList) {
		skuHasStockList.forEach(session -> {
			// 获取时间戳
			Long startTime = session.getStartTime().getTime();
			Long endTime = session.getEndTime().getTime();
			String key = SESSION_CACHE_PREFIX + session.getId().toString() + startTime + "_" + endTime;

			//收集商品id
			List<String> skuIds = session.getRelationEntityList().stream().map(s -> s.getId().toString()).collect(Collectors.toList());
			// 放入redis
			redisTemplatel.opsForList().leftPushAll(key, skuIds);
		});
	}

	private void saveSessionSkuInfos(List<SeckillSessionWithSkus> skuHasStockList) {
		skuHasStockList.forEach(session -> {
			BoundHashOperations<String, Object, Object> boundHashOps = redisTemplatel.boundHashOps(SKUKILL_CACHE_PREFIX);
			session.getRelationEntityList().forEach(seckillSkuVo -> {
//				stringRedisTemplatel JSON.toJSONString(redisTo)
				SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
				// 1、sku基本信息
				R info = productFeignClient.getSkuInfo(seckillSkuVo.getSkuId());
				if (info.getCode() == 0) {
					SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
					});
					if (skuInfo != null) {
						redisTo.setSkuInfoVo(skuInfo);
					}
				}
				// 2、sku秒杀信息
				BeanUtils.copyProperties(seckillSkuVo, redisTo);

				// 3、随机码 开始时间 结束时间
				Long startTime = session.getStartTime().getTime();
				Long endTime = session.getEndTime().getTime();
				redisTo.setStartTime(startTime);
				redisTo.setEndTime(endTime);
				// 随机码 防止暴力发请求抢购
				String token = UUID.randomUUID().toString().replace("-", "");
				redisTo.setRandomCode(token);

				// 设置分布式信号量 锁  应对高并发 秒杀库存
				RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
				// 设置(可以秒杀的数量)库存为信号量 限流
				semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
				// 4、保存
				boundHashOps.put(redisTo.getId().toString(), JSON.toJSONString(redisTo));
			});
		});
	}
}