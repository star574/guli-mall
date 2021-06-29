package com.lsh.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.ware.entity.WareSkuEntity;
import com.lsh.gulimall.ware.service.WareSkuService;


/**
 * 商品库存
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
@RestController
@RequestMapping("ware/waresku")
@Slf4j
public class WareSkuController {
	@Autowired
	private WareSkuService wareSkuService;

	/**
	 * 列表
	 */
	@PostMapping("/haStock")
	public R haStock(@RequestBody List<Long> skuId) {
		List<SkuHasStockTo> skuHasStockVoList = wareSkuService.hasStock(skuId);
		log.warn("库存服务调用成功! {} ",skuHasStockVoList);
		return R.ok().data(skuHasStockVoList);
	}


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("ware:waresku:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wareSkuService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("ware:waresku:info")
	public R info(@PathVariable("id") Long id) {
		WareSkuEntity wareSku = wareSkuService.getById(id);

		return R.ok().put("wareSku", wareSku);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("ware:waresku:save")
	public R save(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.save(wareSku);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("ware:waresku:update")
	public R update(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.updateById(wareSku);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("ware:waresku:delete")
	public R delete(@RequestBody Long[] ids) {
		wareSkuService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
