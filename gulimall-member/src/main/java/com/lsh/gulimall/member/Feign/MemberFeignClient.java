package com.lsh.gulimall.member.Feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.member.Feign.Impl.FeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author codestar
 */
@FeignClient(name = "gulimall-coupon", fallback = FeignClientImpl.class)
@Component
public interface MemberFeignClient {

	/**
	 * //TODO
	 *
	 * @param
	 * @return R
	 * @throws
	 * @date 2021/6/1 23:41
	 * @Description
	 */
	@RequestMapping("/coupon/coupon/test")
	R list();

}
