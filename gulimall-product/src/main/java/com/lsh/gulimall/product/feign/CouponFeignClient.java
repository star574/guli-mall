package com.lsh.gulimall.product.feign;


import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.to.SpuBoundTo;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.impl.CouponFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;

// @Primary  解决 idea中 fallback 实现类 FeignClient 重复问题 : Could not autowire. There is more than one bean
@Primary
@FeignClient(name = "gulimall-coupon", fallback = CouponFeignClientImpl.class)
public interface CouponFeignClient {

	@PostMapping("coupon/spubounds/saveSpuBounds")
	R saveSpuBounds(SpuBoundTo spuBoundTo);

	@PostMapping("coupon/skufullreduction/save/reduction")
	R saveSkuRedution(SkuReductionTo skuReductionTo);
}
