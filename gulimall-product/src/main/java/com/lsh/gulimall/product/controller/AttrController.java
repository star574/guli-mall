package com.lsh.gulimall.product.controller;

import java.util.*;

import com.lsh.gulimall.common.utils.PageUtils;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.entity.ProductAttrValueEntity;
import com.lsh.gulimall.product.entity.vo.AttrVo;
import com.lsh.gulimall.product.service.AttrAttrgroupRelationService;
import com.lsh.gulimall.product.service.AttrGroupService;
import com.lsh.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.product.service.AttrService;


/**
 * 商品属性
 *
 * @author codestar
 * @email shihengluo574@gmail.com
 * @date 2021-05-31 22:31:07
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
	@Autowired
	private AttrService attrService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private AttrAttrgroupRelationService attrAttrgroupRelationService;

	@Autowired
	private AttrGroupService attrGroupService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// // @RequiresPermissions("
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = attrService.queryPage(params);
		return R.ok().put("page", page);
	}

	/**
	 * 属性列表
	 */
	@RequestMapping("/{attrType}/list/{catelogId}")
	// // @RequiresPermissions("
	public R attrBaseList(@PathVariable String attrType, @PathVariable Long catelogId, @RequestParam Map<String, Object> params) {
		PageUtils page = attrService.queryattrPage(catelogId, params, attrType);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrId}")
	// @RequiresPermissions("product:attr:info")
	public R info(@PathVariable("attrId") Long attrId) {

		AttrVo attrVo = attrService.getAttrInfo(attrId);
		return R.ok().put("attr", attrVo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("product:attr:save")
	public R save(@RequestBody AttrVo attrVo) {
		boolean save = attrService.saveAttr(attrVo);
		return save ? R.ok() : R.error();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("product:attr:update")
	public R update(@RequestBody AttrVo attrVo) {
		boolean b = attrService.updateAttr(attrVo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("product:attr:delete")
	public R delete(@RequestBody Long[] attrIds) {
//		attrService.removeByIds(Arrays.asList(attrIds));


		return attrService.removeAttr(Arrays.asList(attrIds))?R.ok():R.error();
	}



	/**
	 * 信息
	 */
	@RequestMapping("base/listforspu/{spuId}")
	// @RequiresPermissions("product:attr:info")
	public R spuBaseInfo(@PathVariable("spuId") Long spuId) {

		List<ProductAttrValueEntity> spuBaseInfoVos=attrService.getSpuInfo(spuId);

		return R.ok().put("data", spuBaseInfoVos);
	}

	/**
	 * 信息
	 */
	@PostMapping("update/{spuId}")
	// @RequiresPermissions("product:attr:info")
	public R spuInfoUpdate(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> productAttrValueEntityList) {

		return attrService.updateSpuInfo(spuId,productAttrValueEntityList)?R.ok():R.error();
	}


}
