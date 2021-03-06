package com.lsh.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.search.config.ESConfig;
import com.lsh.gulimall.search.constant.EsConstant;
import com.lsh.gulimall.search.feign.ProductFeignClient;
import com.lsh.gulimall.search.service.MallSearchService;
import com.lsh.gulimall.search.vo.AttrResponseVo;
import com.lsh.gulimall.search.vo.SearchParam;
import com.lsh.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceIml implements MallSearchService {

	@Autowired
	RestHighLevelClient restHighLevelClient;


	@Autowired
	ProductFeignClient productFeignService;

	@Override
	public SearchResult search(SearchParam searchParam) {

		SearchResult searchResult = null;


		/*??????????????????*/
		SearchRequest searchRequest = buildSearchRequest(searchParam);
		try {
			/*????????????*/
			SearchResponse response = restHighLevelClient.search(searchRequest, ESConfig.COMMON_OPTIONS);
			/*????????????*/
			searchResult = buildSearchResult(response, searchParam);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return searchResult;
	}

	/*??????????????????*/
	private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
		SearchResult searchResult = new SearchResult();
		List<SkuEsModel> skuEsModels = new ArrayList<>();
		/*1 ??????????????????????????????*/
		SearchHit[] productHits = response.getHits().getHits();

		if (productHits != null && productHits.length > 0) {
			for (SearchHit productHit : productHits) {
				String sourceAsString = productHit.getSourceAsString();
				/*??????????????????*/
				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
				if (!StringUtils.isEmpty(searchParam.getKeyword())) {
					/*??????*/
					esModel.setSkuTitle(productHit.getHighlightFields().get("skuTitle").getFragments()[0].string());
				}
				skuEsModels.add(esModel);
			}
			searchResult.setProducts(skuEsModels);
		}

		/*2. ???????????????????????????????????????*/
		/*??????????????????*/
		Aggregations aggregations = response.getAggregations();
		ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
		ParsedNested attr_agg = aggregations.get("attr_agg");
		Aggregations aggregations2 = attr_agg.getAggregations();
		/*id*/
		ParsedLongTerms attr_id_agg = aggregations2.get("attr_id_agg");
		List<? extends Terms.Bucket> buckets1 = attr_id_agg.getBuckets();
		if (buckets1 != null && buckets1.size() > 0) {
			for (Terms.Bucket bucket : buckets1) {
				SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
				long attrId = bucket.getKeyAsNumber().longValue();
				/*??????id*/
				attrVo.setAttrId(attrId);
				Aggregations aggregations1 = bucket.getAggregations();
				ParsedStringTerms attr_name_agg = aggregations1.get("attr_name_agg");
				String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
				/*?????????*/
				attrVo.setAttrName(attrName);

				ParsedStringTerms attr_value_agg = aggregations1.get("attr_value_agg");
				List<String> attrValues = attr_value_agg.getBuckets().stream().map(bucket1 -> {
					String asString = bucket1.getKeyAsString();
					return asString;
				}).collect(Collectors.toList());
				/*?????????*/
				attrVo.setAttrValue(attrValues);
				attrVos.add(attrVo);
			}
			searchResult.setAttrs(attrVos);
		}



		/*3. ??????????????????????????????????????????*/
		ParsedLongTerms brand_agg = aggregations.get("brand_agg");
		ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
		List<? extends Terms.Bucket> buckets = brand_agg.getBuckets();
		if (buckets != null && buckets.size() > 0) {
			for (Terms.Bucket bucket : buckets) {
				SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
				/*??????id*/
				brandVo.setBrandId(Long.valueOf(bucket.getKeyAsString()));
				/*?????????*/
				ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
				String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
				brandVo.setBrandName(brandName);

				/*????????????*/
				ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
				String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
				brandVo.setBrandImg(brandImg);

				brandVos.add(brandVo);
			}
			searchResult.setBrands(brandVos);
		}

		/*4. ????????????*/
		ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
		List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
		if (catalog_agg.getBuckets() != null && catalog_agg.getBuckets().size() > 0) {
			for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
				SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
				/*id*/
				catalogVo.setCatalogId(Long.valueOf(bucket.getKeyAsString()));
				Aggregations aggregations1 = bucket.getAggregations();
				ParsedStringTerms catalogNameAgg = aggregations1.get("catalog_name_agg");
				String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
				/*catalog name*/
				catalogVo.setCatalogName(catalogName);
				catalogVoList.add(catalogVo);
			}
			searchResult.setCatalogs(catalogVoList);
		}

		/*5. ????????????*/
		long total = response.getHits().getTotalHits().value;
		/*????????????*/
		searchResult.setPageNum(searchParam.getPageNum());
		searchResult.setTotal(total);
		/*?????????*/
		searchResult.setTotalPages((int) (total % EsConstant.PRODUCT_PAGESIZE == 0 ? (total / EsConstant.PRODUCT_PAGESIZE) : (total / EsConstant.PRODUCT_PAGESIZE + 1)));
		System.out.println("searchResult = " + JSON.toJSONString(searchResult));

		List<Integer> pageNavs = new ArrayList<>();
		for (int i = 0; i < searchResult.getTotalPages(); i++) {
			pageNavs.add(i + 1);
		}

		searchResult.setPageNavs(pageNavs);

		/*?????????????????????*/
		List<String> attrs = searchParam.getAttrs();
		if (attrs != null && attrs.size() > 0) {
			List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
				String[] split = attr.split("_");
				SearchResult.NavVo navVo = new SearchResult.NavVo();
				//6.1 ???????????????
				navVo.setNavValue(split[1]);
				//6.2 ????????????????????????
				try {
					R r = productFeignService.info(Long.parseLong(split[0]));
					System.out.println(r);
					if (r.getCode() == 0) {
						AttrResponseVo attrResponseVo = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
						});
						navVo.setNavName(attrResponseVo.getAttrName());
					}
				} catch (Exception e) {
					log.error("??????????????????????????????????????????", e);
				}
				//6.3 ???????????????????????????(???????????????????????????????????????)
				String queryString = searchParam.get_queryString();
				String replace = queryString.replace("&attrs=" + attr, "").replace("attrs=" + attr + "&", "").replace("attrs=" + attr, "");
				navVo.setLink("http://search.gulimall.com/search.html" + (replace.isEmpty() ? "" : "?" + replace));
				return navVo;
			}).collect(Collectors.toList());
			searchResult.setNavs(navVos);
		}

		// TODO brand ???????????????


		return searchResult;
	}

	/*??????????????????*/
	private SearchRequest buildSearchRequest(SearchParam searchParam) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		/*????????????,??????(??????,??????,??????,????????????,??????)*/
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		1.must
		if (!StringUtils.isEmpty(searchParam.getKeyword())) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
		}
//		1.2.1 filter catalogId
		if (searchParam.getCatalog3Id() != null) {
			boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
		}
//		1.2.2 filter brandId
		if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
		}
//		1.2.3 filter attrs
		if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
			List<String> attrs = searchParam.getAttrs();
			for (String attr : attrs) {
				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
				String[] s = attr.split("_");
				/*attrId ??????id*/
//				s[0]
				/*attrValue ????????????value*/
				String[] split = s[1].split(":");
				/*??????id*/
				queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", s[0]));
				/*?????????*/
				queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", split));

				/*?????????????????????????????? nestedQuery ???????????? boolQueryBuilder.filter*/
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
				/*?????????*/
				boolQueryBuilder.filter(nestedQuery);
			}
		}
//		1.2.4 filter hasStock
		if (searchParam.getHasStock() != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("hasStock", searchParam.getHasStock() == 1));
		}
//		1.2.5 filter skuPrice
		if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {

			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
			String skuPrice = searchParam.getSkuPrice();
			String[] split = skuPrice.split("_");
			if (split.length == 2) {
				rangeQueryBuilder.gte(split[0]);
				rangeQueryBuilder.lte(split[1]);
			} else if (skuPrice.startsWith("_")) {
				rangeQueryBuilder.gte(0);
				rangeQueryBuilder.lte(split[0]);
			} else {
				rangeQueryBuilder.gte(split[0]);
			}
			boolQueryBuilder.filter(rangeQueryBuilder);
		}
		searchSourceBuilder.query(boolQueryBuilder);

		/*
		 * ????????????
		 * */

		/*??????*/
		if (!StringUtils.isEmpty(searchParam.getSort())) {
			String sort = searchParam.getSort();
			String[] s = sort.split("_");
			/*????????????*/
//			s[0]
			/*????????????*/
//			s[1]
			searchSourceBuilder.sort(s[0], s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
		}
		/*??????*/
//		PRODUCT_PAGESIZE
		/*-1*????????????*/
		searchSourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

		/*??????*/
		if (!StringUtils.isEmpty(searchParam.getKeyword())) {
			HighlightBuilder highlightBuilder = new HighlightBuilder();
			highlightBuilder.field("skuTitle");
			highlightBuilder.preTags("<b style='color:red;'>");
			highlightBuilder.postTags("</b>");
			searchSourceBuilder.highlighter(highlightBuilder);
		}
		/*????????????*/
		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
		/*??????id*/
		brand_agg.field("brandId").size(50);

		/*?????????*/
		/*????????????*/
		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword").size(1));
		/*????????????*/
		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg.keyword").size(1));
//		1
		searchSourceBuilder.aggregation(brand_agg);

		/*????????????*/
		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
		catalog_agg.field("catalogId").size(20);
		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("cataName.keyword").size(1));

//		2
		searchSourceBuilder.aggregation(catalog_agg);

		/*???????????? ?????????*/
		NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
		/*?????????*/
		TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
		/*??????*/
		TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName.keyword").size(1);
		TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);

		attr_id_agg.subAggregation(attr_name_agg);
		attr_id_agg.subAggregation(attr_value_agg);

		nested.subAggregation(attr_id_agg);

//		3
		searchSourceBuilder.aggregation(nested);

		System.out.println("?????????DSL searchSourceBuilder = " + searchSourceBuilder);
		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
		return searchRequest;
	}

}
