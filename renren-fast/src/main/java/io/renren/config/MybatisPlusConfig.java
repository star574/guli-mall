/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * mybatis-plus配置
 *
 * @author Mark shihengluo574@gmail.com
 */
@Configuration
@Slf4j
public class MybatisPlusConfig {

	@Autowired
	DataSource dataSource;

	public void checkJDBC() throws SQLException {
		log.warn("当前数据源信息" + dataSource.getConnection().getMetaData().getURL());
	}

	/**
	 * 分页插件
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

}
