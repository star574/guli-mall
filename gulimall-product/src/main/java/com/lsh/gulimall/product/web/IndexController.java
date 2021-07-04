package com.lsh.gulimall.product.web;

import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.entity.vo.frontvo.Catelog2Vo;
import com.lsh.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

	@Autowired
	CategoryService categoryService;


	@RequestMapping({"/", "/index", "/index.html"})
	String indexPage(Model model) {
		/*1.查询所有的一级分类*/
		List<CategoryEntity> categoryEntityList = categoryService.getOneLevelCategorys();
//		log.info("访问首页!");
		model.addAttribute("categorys", categoryEntityList);
		return "index";
	}

	/*index/catalog.json*/

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:50
	 * @Description 获取前端所有分类
	 */
	@RequestMapping("index/catalog.json")
	@ResponseBody
	Map<String, List<Catelog2Vo>> getCatalogJson() {
		Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
		return map;
	}
	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:50
	 * @Description 测试服务
	 */
	@RequestMapping("/hello")
	@ResponseBody
	String hello() {
		return "hello";
	}
}
