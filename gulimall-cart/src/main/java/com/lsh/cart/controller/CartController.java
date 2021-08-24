package com.lsh.cart.controller;

import com.lsh.cart.service.CartService;
import com.lsh.cart.vo.Cart;
import com.lsh.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class CartController {

	@Autowired
	CartService cartService;

	@GetMapping("/cart.html")
	String cartListPage(Model model) {
		System.out.println("======================");
		Cart cart=cartService.getCartList();
		model.addAttribute("cart",cart);
		System.out.println(cart);
		return "cartList";
	}

	/*添加商品到购物车*/
	@GetMapping("/addToCart")
	String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) {
		CartItem cartItem = cartService.addToCart(skuId, num);
		/*重定向携带数据*/
		redirectAttributes.addAttribute("skuId", skuId);
		/*addFlashAttribute 将数据放入session 只能取一次*/
		/*addAttribute 将数据放入url */
		return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
	}

	@GetMapping("/addToCartSuccessPage.html")
	public String addToCartSuccessPage(@RequestParam Long skuId, Model model) {

		/*重定向到成功页面*/
		CartItem cartItem = cartService.getCartItem(skuId);
		log.info("正在查询购物项 skuId="+skuId);
		model.addAttribute("item", cartItem);
		log.info("购物项查询成功!" + cartItem);
		return "success";
	}
}
