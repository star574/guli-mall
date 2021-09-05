package com.lsh.gulimall.order.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/5/21 7:22 AM
 * @Description: 订单确认页面数据
 **/
//@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmVo {
	/*收货地址*/
	@Setter
	@Getter
	private List<MemberAddressVo> address;

	/*购物项*/
	@Setter
	@Getter
	private List<OrderItemVo> items;

	/*发票记录*/

	/*优惠卷信息*/
	/**
	 * 积分
	 */
	@Setter
	@Getter
	private Integer integration;

	/*总价格*/
//	private BigDecimal total;

	/*应付总额度*/
	private BigDecimal payPrice;


	/*获取订单总价格*/
	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal(0);
		if (items != null) {
			for (OrderItemVo item : items) {
				BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
				total = total.add(multiply);
			}
		}
		return total;
	}

	public BigDecimal getPayPrice() {
		return getTotal();
	}


}
