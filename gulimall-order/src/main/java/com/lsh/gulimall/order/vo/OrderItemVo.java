package com.lsh.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
	/*id*/
	private Long skuId;
	/*标题*/
	private String title;
	/*图片*/
	private String image;
	/*属性*/
	private List<String> skuAttr;
	/*价格*/
	private BigDecimal price;
	/*数量*/
	private Integer count;
	/*小计*/
	private BigDecimal totalPrice;

}
