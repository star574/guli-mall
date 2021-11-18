/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * @author demo
 */
@SpringBootApplication
@Slf4j
public class RenrenApplication {
	@Autowired
	DataSource dataSource;

	@PostConstruct
	public void checkJDBC() throws SQLException {
		log.warn("当前数据源信息" + dataSource.getConnection().getMetaData().getURL());
	}
	public static void main(String[] args) {
		SpringApplication.run(RenrenApplication.class, args);
	}
}