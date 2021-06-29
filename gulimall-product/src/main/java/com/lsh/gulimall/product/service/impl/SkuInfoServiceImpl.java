package com.lsh.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.product.dao.SkuInfoDao;
import com.lsh.gulimall.product.entity.SkuInfoEntity;
import com.lsh.gulimall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author codestar
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {


		/*检索关键字*/
		String key = (String) params.get("key");
		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");
		String minStr = (String) params.get("min");
		String maxStr = (String) params.get("max");
		double max = 0;
		double min = 0;


		try {
			if (!StringUtils.isEmpty(maxStr)) {
				max = Double.parseDouble(maxStr);
			}
			if (!StringUtils.isEmpty(minStr)) {
				min = Double.parseDouble(minStr);
			}
		} catch (Exception ignored) {

		}

		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(queryWrapper -> {
				queryWrapper.like("sku_name", key).or().like("sku_title", key).or().like("sku_subtitle", key);
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
		if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			wrapper.eq("catalog_id", catelogId);
		}
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("status", status);
		}

		if (min != 0) {
			wrapper.ge("price", min);
		}
		if (max != 0) {
			wrapper.le("price", max);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}

		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
		return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
	}

}