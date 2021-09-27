package com.lsh.gulimall.ware.exception;


public class NoStockException extends RuntimeException {
	private Long skuId;

	public NoStockException(Long skuId) {
		super("商品:  " + skuId + "没有足够库存");
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
}
