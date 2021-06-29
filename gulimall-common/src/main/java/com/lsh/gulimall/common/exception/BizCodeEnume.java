package com.lsh.gulimall.common.exception;

/**
 * @author codestar
 */

public enum BizCodeEnume {
	VALID_EXCEPTION(10001, "参数格式校验失败"),
	PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
	UNKNOW_EXCEPTION(10000, "未知异常");

	private Integer code;
	private String msg;


	BizCodeEnume(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
