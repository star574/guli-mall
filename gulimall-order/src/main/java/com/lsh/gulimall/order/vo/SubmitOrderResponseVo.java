package com.lsh.gulimall.order.vo;

import com.lsh.gulimall.order.entity.OmsOrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
	private OmsOrderEntity order;
	private Integer code; //0 成功
}
