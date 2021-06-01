package com.lsh.gulimall.member.Feign.Impl;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.member.Feign.MemberFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author codestar
 */
public class FeignClientImpl implements MemberFeignClient {
	@Override
	public R list() {
		return R.error("服务调用失败");
	}
}
