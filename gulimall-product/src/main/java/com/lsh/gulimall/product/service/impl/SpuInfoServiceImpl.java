package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.to.SpuBoundTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.dao.SpuInfoDao;
import com.lsh.gulimall.product.entity.*;
import com.lsh.gulimall.product.entity.vo.*;
import com.lsh.gulimall.product.feign.CouponFeignClient;
import com.lsh.gulimall.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author codestar
 * http://localhost:8000/api
 */
@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity>
		implements SpuInfoService {

	@Autowired
	CategoryService categoryService;

	@Autowired
	BrandService brandService;

	@Autowired
	SpuInfoDescService spuInfoDescService;

	@Autowired
	SpuImagesService spuImagesService;

	@Autowired
	SkuImagesService skuImagesService;

	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Autowired
	ProductAttrValueService productAttrValueService;

	@Autowired
	SkuInfoService skuInfoService;

	@Autowired
	CouponFeignClient couponFeignClient;

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
			wrapper.and(queryWrapper -> {
				queryWrapper.like("spu_name", key).or().like("spu_description", key);
				try {
					queryWrapper.or().like("id", Long.parseLong(key));
				} catch (Exception ignored) {
					System.out.println(key);
				}
			});
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
			wrapper.eq("catalog_id", Long.parseLong(catelogId));
		}
		if (!StringUtils.isEmpty(brandId) && Integer.parseInt(brandId) != 0) {
			wrapper.eq("brand_id", Long.parseLong(brandId));
		}
		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("publish_status", Long.parseLong(status));
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


		spuImagesService.saveImages(infoEntityId, images);

		// 4.保存规格参数 保存商品属性
		List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
		productAttrValueService.saveBaseAttrs(infoEntityId, baseAttrs);

		// 5.保存对应所有sku信息
		// 5.1 sku基本信息
		List<Skus> skus = spuSaveVo.getSkus();
		// TODO
		if (skus != null && skus.size() > 0) {
			skus.forEach(sku -> {
				String deefaultImg = null;
				for (Images image : sku.getImages()) {
					if (image.getDefaultImg() == 1) {
						deefaultImg = image.getImgUrl();
					}
				}
				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				BeanUtils.copyProperties(sku, skuInfoEntity);
				skuInfoEntity.setSpuId(infoEntityId);
				skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
				skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoEntity.setSkuDefaultImg(deefaultImg);
				skuInfoService.save(skuInfoEntity);

				Long skuId = skuInfoEntity.getSkuId();

				// 5.2 sku图片信息
				List<SkuImagesEntity> collect = sku.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
					// 没有图片不保存
				}).filter(skuImagesEntity -> !StringUtils.isEmpty(skuImagesEntity.getImgUrl()))
						.collect(Collectors.toList());
				skuImagesService.saveBatch(collect);
				List<Attr> attrs = sku.getAttr();

				// 5.3 销售属性信息
				List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrs.stream().map(attr -> {
					SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
					skuSaleAttrValueEntity.setAttrSort(0);
					skuSaleAttrValueEntity.setSkuId(skuId);
					return skuSaleAttrValueEntity;
				}).collect(Collectors.toList());
				skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

				// 5.4 sku优惠满减信息
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(sku, skuReductionTo);
				skuReductionTo.setSkuId(skuId);
				if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice()
						.compareTo(new BigDecimal(0)) > 0) {
					R ok = couponFeignClient.saveSkuRedution(skuReductionTo);

					if (ok.getCode() != 0) {
						log.error("远程保存saveSkuRedution失败");
					}
				}
			});

		}


		// 6.积分信息
		Bounds bounds = spuSaveVo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(infoEntityId);
		couponFeignClient.saveSpuBounds(spuBoundTo);

		return true;
	}

	@Override
	public boolean saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
		return this.save(spuInfoEntity);

	}


}