package com.lsh.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.lsh.gulimall.search.config.ESConfig;
import lombok.extern.java.Log;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallSearchApplication.class)
public class GulimallSearchApplicationTests {

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Test
	public void contextLoads() {
		System.out.println(restHighLevelClient);
	}

	class User {
		private String userNname;
		private Integer age;

		public User(String userNname, Integer age) {
			this.userNname = userNname;
			this.age = age;
		}

		public User() {

		}

	}

	@Test
	public void testIndex() {
		/*索引 users type 已经过时 id */
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
		/*1*/
//		indexRequest.source("userName","张三","age",18);

		/*2*/
//		User user = new User("张三", 18);
//		String string = JSON.toJSONString(user);
//		indexRequest.source(string);

		HashMap<String, Object> map = new HashMap<>();
		map.put("userName", "张三");
		map.put("age", "18");
		indexRequest.source(map);
		indexRequest.timeout("5s");

		/*同步*/
		try {
			IndexResponse index = restHighLevelClient.index(indexRequest, ESConfig.COMMON_OPTIONS);
			System.out.println(index);
			System.out.println(index.getIndex());
			System.out.println(index.status());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("保存/更新失败");
		}
		/*异步*/

	}

	@Test
	public void testGet() {

		/*查询 index:users id:1*/
		GetRequest request = new GetRequest("users", "1");
		try {
			GetResponse documentFields = restHighLevelClient.get(request, ESConfig.COMMON_OPTIONS);
			System.out.println(documentFields.getSource());
			System.out.println("version" + documentFields.getVersion());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*同步 复杂查询年龄段平均薪资*/
	@Test
	public void testSearch() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("bank");
		/*查询条件*/
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		/*构造查询*/
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address", "mill");
		searchSourceBuilder.query(matchQueryBuilder);

		/*聚合*/
		/*结果 : {"key":38,"doc_count":2},{"key":28,"doc_count":1},{"key":32,"doc_count":1}*/
		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(20);
		/*结果 : "{value":25208.0}*/
		AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balanceAgg").field("balance");
		searchSourceBuilder.aggregation(ageAgg);
		searchSourceBuilder.aggregation(balanceAgg);
		/*从第O条开始,查询5条*/
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(5);

		/*排序 倒序*/
		searchSourceBuilder.sort("balance", SortOrder.DESC);

		/*超时时间 10s */
		searchSourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));
		searchRequest.source(searchSourceBuilder);
		System.out.println("searchSourceBuilder = " + searchSourceBuilder);

		List<Accout> Reslist = new ArrayList<>();
		try {
			SearchResponse search = restHighLevelClient.search(searchRequest, ESConfig.COMMON_OPTIONS);
			System.out.println("search = " + search);
			SearchHits searchHits = search.getHits();
			SearchHit[] hits = searchHits.getHits();
			for (SearchHit hit : hits) {
				/*获取数据*/
				Accout accout = JSON.parseObject(hit.getSourceAsString(), Accout.class);
				Reslist.add(accout);
			}
			Aggregations aggregations = search.getAggregations();

			Terms agg1 = aggregations.get("ageAgg");
			List<? extends Terms.Bucket> buckets = agg1.getBuckets();
			for (Terms.Bucket bucket : buckets) {
				System.out.println("年龄分布 : " + bucket.getKey() +"人数" + bucket.getDocCount());
			}
			Avg agg2 = aggregations.get("balanceAgg");
			System.out.println("平均薪资 = " + agg2.getValue());
//			List<Aggregation> aggregationList = aggregations.asList();
//			for (Aggregation aggregation : aggregationList) {
//				/*聚合名*/
//				aggregation.getName();
//				/*获取数据*/
//				Map<String, Object> metaData = aggregation.getMetaData();
//				/**/
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n\n");
		/*遍历结果*/
		for (Accout accout : Reslist) {
			System.out.println(accout);
		}

	}
}
