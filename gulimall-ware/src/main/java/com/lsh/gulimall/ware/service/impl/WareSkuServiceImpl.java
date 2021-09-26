package com.lsh.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.dao.WareSkuDao;
import com.lsh.gulimall.ware.entity.WareSkuEntity;
import com.lsh.gulimall.ware.entity.vo.LockStockResult;
import com.lsh.gulimall.ware.entity.vo.WareSkuLockVo;
import com.lsh.gulimall.ware.feign.ProductFeignClient;
import com.lsh.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


	@Autowired
	private ProductFeignClient productFeignClient;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");
		String wareId = (String) params.get("wareId");
		String skuId = (String) params.get("skuId");

		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();


		if (!StringUtils.isEmpty(skuId)) {
			wrapper.eq("sku_id", skuId);
		}

		if (!StringUtils.isEmpty(wareId)) {
			wrapper.eq("ware_id", wareId);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}

		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public boolean addStock(Long skuId, Long wareId, Integer skuNum) {

		if (this.count(new QueryWrapper<WareSkuEntity>().eq("ware_id", wareId).eq("sku_id", skuId)) == 0) {
			WareSkuEntity wareSkuEntity = new WareSkuEntity();
			wareSkuEntity.setSkuId(skuId);
			wareSkuEntity.setWareId(wareId);
			wareSkuEntity.setStock(skuNum);
			wareSkuEntity.setStockLocked(0);

			try {

				R r = productFeignClient.info(skuId);
				Map<String, Object> info = (Map<String, Object>) r.get("skuInfo");
				if (r.getCode() == 0) {
					wareSkuEntity.setSkuName((String) info.get("skuName"));
				}
			} catch (Exception ignored) {
				/*手动处理异常*/

				// TODO  让事务不回滚
			}
			this.save(wareSkuEntity);
		} else {
			baseMapper.addStock(skuId, wareId, skuNum);
		}
		return true;
	}

	@Override
	public List<SkuHasStockTo> hasStock(List<Long> skuIds) {
		List<WareSkuEntity> skuEntityList = this.list(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));

		List<SkuHasStockTo> skuHasStockVoList = skuEntityList.stream().map(wareSkuEntity -> {
			SkuHasStockTo skuHasStockVo = new SkuHasStockTo();
			skuHasStockVo.setSkuId(wareSkuEntity.getSkuId());
			/*有库存: 库存减去锁定库存大于0*/
			if (wareSkuEntity.getSkuId() != null && wareSkuEntity.getStock() != null && wareSkuEntity.getStockLocked() != null) {
				skuHasStockVo.setHasStock(wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0);
			} else {
				skuHasStockVo.setHasStock(false);
			}
			return skuHasStockVo;
		}).collect(Collectors.toList());

		return skuHasStockVoList;
	}

	/**
	 * //TODO
	 *
	 * @param wareSkuLockVo
	 * @return: List<LockStockResult>
	 * @Description: 为某个订单锁定库存
	 */
	@Override
	public List<LockStockResult> orderLockStock(WareSkuLockVo wareSkuLockVo) {



		return null;
	}

}