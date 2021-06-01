package com.lsh.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.ware.entity.PurchaseEntity;
import com.lsh.gulimall.ware.service.PurchaseService;
//import com.lsh.gulimall.common.utils.PageUtils;
//


/**
 * 采购信息
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
	@Autowired
	private PurchaseService purchaseService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	// @RequiresPermissions("ware:purchase:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("ware:purchase:info")
	public R info(@PathVariable("id") Long id) {
            PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	// @RequiresPermissions("ware:purchase:save")
	public R save(@RequestBody PurchaseEntity purchase) {
            purchaseService.save(purchase);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	// @RequiresPermissions("ware:purchase:update")
	public R update(@RequestBody PurchaseEntity purchase) {
            purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	// @RequiresPermissions("ware:purchase:delete")
	public R delete(@RequestBody Long[] ids) {
            purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
