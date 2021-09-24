package com.lsh.gulimall.order.web;


import com.lsh.gulimall.order.service.OmsOrderService;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import com.lsh.gulimall.order.vo.OrderSubmitVo;
import com.lsh.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.rmi.ServerException;

@Controller
@Slf4j
public class OrderWebController {

	@Autowired
	OmsOrderService orderService;

	/**
	 * //TODO
	 *
	 * @param model
	 * @return: String
	 * @Description: 订单确认页数据
	 */
	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ServerException {
		OrderConfirmVo confirmVo = orderService.confirmOrder();
		model.addAttribute("orderConfirmData", confirmVo);
		return "confirm";
	}


	@PostMapping("/submitOrder")
	/**
	 * //TODO
	 *
	 * @param orderSubmitVo
	 * @return: String
	 * @Description: 下单
	 */
	public String submitOrder(@RequestBody OrderSubmitVo orderSubmitVo) {
		/*创建订单*/
		SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);

		/*下单成功 来到支付选择页*/ /*下单失败 回到确认页面 重新确认订单*/
		if (responseVo.getCode()==0) {
			/*成功*/
			return "pay";
		}
		/*失败*/
		return "redirect:https://order:springboot.ml/toTrade";
	}
}
