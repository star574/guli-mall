package com.lsh.gulimall.order.feign;

import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gulimall-ware")
public interface WareService {

	/**
	 * //TODO
	 *
	 * @param skuId
	 * @return: R
	 * @Description: 批量检查是否有库存
	 */
	@PostMapping("ware/waresku/haStock")
	R haStock(@RequestBody List<Long> skuId);

	/**
	 * //TODO
	 *
	 * @param addrId
	 * @return: R
	 * @Description: 根据用户收获地址计算运费
	 */
	@GetMapping("/fare")
	R getFare(@RequestParam("addrId") long addrId);
}
