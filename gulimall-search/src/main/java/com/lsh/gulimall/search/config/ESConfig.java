package com.lsh.gulimall.search.config;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*导入依赖
 * 编写配置 注入 RestHighLevelClient*/
@Configuration
public class ESConfig {


	@Value("${elasticsearch.host}")
	private String host;

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
		System.out.println("ES连接地址:-------------" + this.host);
		HttpHost host = new HttpHost(this.host, 9200, HttpHost.DEFAULT_SCHEME_NAME);
		builder = RestClient.builder(host);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "luoshiheng574"));
		builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
		RestHighLevelClient restClient = new RestHighLevelClient(builder);

		return restClient;
	}
}
