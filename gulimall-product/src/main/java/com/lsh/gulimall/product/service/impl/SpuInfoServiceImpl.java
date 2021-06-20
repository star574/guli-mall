package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.entity.SpuInfoDescEntity;
import com.lsh.gulimall.product.entity.vo.BaseAttrs;
import com.lsh.gulimall.product.entity.vo.Skus;
import com.lsh.gulimall.product.entity.vo.SpuInfoVo;
import com.lsh.gulimall.product.entity.vo.SpuSaveVo;
import com.lsh.gulimall.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.SpuInfoDao;
import com.lsh.gulimall.product.entity.SpuInfoEntity;

import com.lsh.gulimall.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author codestar
 * http://localhost:8000/api
 */
@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Autowired
	CategoryService categoryService;

	@Autowired
	BrandService brandService;

	@Autowired
	SpuInfoDescService spuInfoDescService;

	@Autowired
	SpuImagesService spuImagesService;

	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Autowired
	ProductAttrValueService productAttrValueService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");

		QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.like("spu_name", key).or().like("spu_descruption", key);
		}
        /*
          catelogId: 6,//三级分类id
          brandId: 1,//品牌id
          status: 0,//商品状态
        */
		String catelogId = (String) params.get("catelogId");
		String brandId = (String) params.get("brandId");
		String status = (String) params.get("status");
		if (!StringUtils.isEmpty(catelogId) && Integer.parseInt(catelogId) != 0) {
			wrapper = wrapper.eq("catalog_id", Long.parseLong(catelogId));
		}
		if (!StringUtils.isEmpty(brandId)) {
			wrapper = wrapper.eq("brand_id", Long.parseLong(brandId));
		}
		if (!StringUtils.isEmpty(status)) {
			wrapper = wrapper.eq("publish_status", Long.parseLong(status));
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper = wrapper.orderByAsc(sidx);
		}

		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);
		log.warn(page.getRecords().toString());
		List<SpuInfoVo> spuInfoVoList = new ArrayList<>();


		for (SpuInfoEntity record : page.getRecords()) {
			String catelogName = categoryService.getById(record.getCatalogId()).getName();
			String brandName = brandService.getById(record.getBrandId()).getName();
			SpuInfoVo spuInfoVo = new SpuInfoVo();
			BeanUtils.copyProperties(record, spuInfoVo);
			spuInfoVo.setCatelogName(catelogName);
			spuInfoVo.setBrandName(brandName);

			spuInfoVoList.add(spuInfoVo);

		}

		PageUtils pageUtils = new PageUtils(page);
		pageUtils.setList(spuInfoVoList);

		return pageUtils;
	}


	/*保存商品全部信息*/
	@Transactional
	@Override
	public boolean saveSpuVo(SpuSaveVo spuSaveVo) {
		// 1.保存基本信息
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
		this.saveBaseSpuInfo(spuInfoEntity);
		Long infoEntityId = spuInfoEntity.getId();

		// 2.保存描述图片
		List<String> decript = spuSaveVo.getDecript();
		SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
		spuInfoDescEntity.setSpuId(infoEntityId);
		spuInfoDescEntity.setDecript(String.join(",", decript));
		spuInfoDescService.save(spuInfoDescEntity);
		// 3.保存图片集
		List<String> images = spuSaveVo.getImages();


		spuImagesService.saveImages(infoEntityId,images);

		// 4.保存规格参数 保存商品属性
		List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
		boolean b=productAttrValueService.saveBaseAttrs(infoEntityId,baseAttrs);


		// 5.保存对应所有sku信息
		// 5.1 sku基本信息
		List<Skus> skus = spuSaveVo.getSkus();
		// TODO


		// 5.2 sku图片信息
		// 5.3 销售属性信息
		// 5.4 sku优惠满减信息


		// 6.积分信息

		return false;
	}

	@Override
	public boolean saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
		return this.save(spuInfoEntity);

	}


}