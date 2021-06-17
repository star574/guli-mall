package com.lsh.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author codestar
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.lsh.gulimall.product.dao")
public class MybatisConfig {


	@Bean
	PaginationInterceptor getPaginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		/*最大页回到第一页*/
		paginationInterceptor.setOverflow(true);

		/*最大条数限制*/
		paginationInterceptor.setLimit(1000);

		return paginationInterceptor;
	}

}
