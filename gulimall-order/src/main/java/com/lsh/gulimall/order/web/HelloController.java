package com.lsh.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/5/21 3:58 AM
 * @Description:
 **/
@Controller
public class HelloController {

	@GetMapping("/{page}.html")
	public String listPage(@PathVariable("page") String page) {

		return page;
	}
}
