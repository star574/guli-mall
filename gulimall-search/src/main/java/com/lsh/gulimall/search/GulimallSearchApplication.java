package com.lsh.gulimall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSearchApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(GulimallSearchApplication.class, args);
		RestHighLevelClient esRestClient = run.getBean("esRestClient", RestHighLevelClient.class);
		System.out.println(esRestClient);
	}

}
