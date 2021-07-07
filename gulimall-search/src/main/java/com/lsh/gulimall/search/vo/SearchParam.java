package com.lsh.gulimall.search.vo;

import lombok.Data;
/**
 * //TODO
 * @Author shihe
 * @Date 2:05 2021/7/8
 * @Description 前端参数vo
 **/
@Data
public class SearchParam  {
	private String keyword;  //全文匹配
	private Long catalog3Id; //三级分类id

}
