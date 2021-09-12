package com.lsh.gulimall.order.web;


import com.lsh.gulimall.order.service.OmsOrderService;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.rmi.ServerException;

@Controller
@Slf4j
public class OrderWebController {

	@Autowired
	OmsOrderService orderService;

	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ServerException {
		OrderConfirmVo confirmVo = orderService.confirmOrder();
		model.addAttribute("orderConfirmData", confirmVo);
		System.out.println();
		return "confirm";
	}
}
