package com.lsh.gulimall.coupon.controller;
import com.lsh.gulimall.common.utils.R;

import java.util.Arrays;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.coupon.entity.HomeSubjectSpuEntity;
import com.lsh.gulimall.coupon.service.HomeSubjectSpuService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.Query;



/**
 * 专题商品
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@RestController
@RequestMapping("coupon/homesubjectspu")
public class HomeSubjectSpuController {
	@Autowired
	private HomeSubjectSpuService homeSubjectSpuService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:homesubjectspu:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = homeSubjectSpuService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:homesubjectspu:info")
	public R info(@PathVariable("id") Long id) {
            HomeSubjectSpuEntity homeSubjectSpu = homeSubjectSpuService.getById(id);

		return R.ok().put("homeSubjectSpu", homeSubjectSpu);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:homesubjectspu:save")
	public R save(@RequestBody HomeSubjectSpuEntity homeSubjectSpu) {
            homeSubjectSpuService.save(homeSubjectSpu);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:homesubjectspu:update")
	public R update(@RequestBody HomeSubjectSpuEntity homeSubjectSpu) {
            homeSubjectSpuService.updateById(homeSubjectSpu);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:homesubjectspu:delete")
	public R delete(@RequestBody Long[] ids) {
            homeSubjectSpuService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
