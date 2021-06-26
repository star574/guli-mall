package com.lsh.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*导入依赖
 * 编写配置 注入 RestHighLevelClient*/
@Configuration
public class ESConfig {


	public static final RequestOptions COMMON_OPTIONS;

	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

		COMMON_OPTIONS = builder.build();
	}

	/*配置并注入elasticsearch client */
	@Bean
	public RestHighLevelClient esRestClient() {

		RestClientBuilder builder = null;
		// 可以指定多个es
		builder = RestClient.builder(new HttpHost("192.168.2.99", 9200, "http"));
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;
	}
}
