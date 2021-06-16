package com.lsh.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.service.AttrGroupService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;


/**
 * 属性分组
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
	@Autowired
	private AttrGroupService attrGroupService;

	/**
	 * 列表
	 */
	@RequestMapping("/list/{catelogId}")
	// @RequiresPermissions("product:attrgroup:list")
	public R list(@PathVariable Long catelogId, @RequestParam Map<String, Object> params) {
		PageUtils page = attrGroupService.queryPage(catelogId,params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	// @RequiresPermissions("product:attrgroup:info")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		return R.ok().put("attrGroup", attrGroup);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:attrgroup:save")
	public R save(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.save(attrGroup);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:attrgroup:update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:attrgroup:delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

		return R.ok();
	}

}
