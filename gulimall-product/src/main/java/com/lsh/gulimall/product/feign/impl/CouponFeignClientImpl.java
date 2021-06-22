package com.lsh.gulimall.product.feign.impl;
import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.to.SpuBoundTo;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.CouponFeignClient;
import org.springframework.stereotype.Component;

@Component
public class CouponFeignClientImpl implements CouponFeignClient {

	@Override
	public R saveSpuBounds(SpuBoundTo spuBoundTo) {
		return R.error("服务调用失败");
	}

	@Override
	public R saveSkuRedution(SkuReductionTo skuReductionTo) {
		return R.error("服务调用失败");
	}
}