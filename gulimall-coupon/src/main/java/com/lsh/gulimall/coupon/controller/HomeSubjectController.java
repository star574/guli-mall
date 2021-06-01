package com.lsh.gulimall.coupon.controller;
import com.lsh.gulimall.common.utils.R;

import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.coupon.entity.HomeSubjectEntity;
import com.lsh.gulimall.coupon.service.HomeSubjectService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.Query;



/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:30:32
 */
@RestController
@RequestMapping("coupon/homesubject")
public class HomeSubjectController {
	@Autowired
	private HomeSubjectService homeSubjectService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	// // @RequiresPermissions("coupon:homesubject:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = homeSubjectService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	// // @RequiresPermissions("coupon:homesubject:info")
	public R info(@PathVariable("id") Long id) {
            HomeSubjectEntity homeSubject = homeSubjectService.getById(id);

		return R.ok().put("homeSubject", homeSubject);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	// // @RequiresPermissions("coupon:homesubject:save")
	public R save(@RequestBody HomeSubjectEntity homeSubject) {
            homeSubjectService.save(homeSubject);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	// // @RequiresPermissions("coupon:homesubject:update")
	public R update(@RequestBody HomeSubjectEntity homeSubject) {
            homeSubjectService.updateById(homeSubject);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	// // @RequiresPermissions("coupon:homesubject:delete")
	public R delete(@RequestBody Long[] ids) {
            homeSubjectService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
