package com.lsh.gulimall.search.web;

import com.lsh.gulimall.search.service.MallSearchService;
import com.lsh.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class SearchController {

	@Autowired
	MallSearchService mallSearchService;


	@GetMapping(value = {"/", "/list.html"})
	public String listPage(SearchParam searchParam) {

		Object result = mallSearchService.search(searchParam);

		return "list";
	}
}
