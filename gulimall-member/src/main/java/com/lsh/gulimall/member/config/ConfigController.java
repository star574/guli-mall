package com.lsh.gulimall.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:
 *
 * @ClassName ConfigController
 * @Author codestar
 * @DATE 2021/6/3 上午12:52
 * @Version 1.0
 * @Description
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

	@Value("${useLocalCache:false}")
	private boolean useLocalCache;
	@Value("${user.id}")
	private String userId;

	@RequestMapping("/get")
	public boolean get() {
		System.out.println(userId);
		return useLocalCache;
	}

}
