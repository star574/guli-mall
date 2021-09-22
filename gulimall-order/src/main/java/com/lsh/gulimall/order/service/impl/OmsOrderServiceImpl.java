package com.lsh.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import com.lsh.gulimall.order.dao.OmsOrderDao;
import com.lsh.gulimall.order.entity.OmsOrderEntity;
import com.lsh.gulimall.order.feign.CartService;
import com.lsh.gulimall.order.feign.MemberService;
import com.lsh.gulimall.order.feign.WareService;
import com.lsh.gulimall.order.interceptor.LoginUserInterceptor;
import com.lsh.gulimall.order.service.OmsOrderService;
import com.lsh.gulimall.order.vo.MemberAddressVo;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import com.lsh.gulimall.order.vo.OrderItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


/**
 * //TODO
 *
 * @Author: codestar
 * @Date: 2021-09-09 01:44:19
 * @Description:
 */
@Slf4j
@Service("omsOrderService")
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderDao, OmsOrderEntity> implements OmsOrderService {

	private MemberService memberService;
	private CartService cartService;

	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Autowired
	private WareService wareService;


	/**
	 * //TODO
	 *
	 * @param memberService
	 * @return: null
	 * @Description: set方法注入 去除警告 Field injection is not recommended
	 */
	@Autowired
	private void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@Autowired
	private void setCartService(CartService cartService) {
		this.cartService = cartService;
	}

	/**
	 * //TODO
	 *
	 * @param params
	 * @return: PageUtils
	 * @Description:
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OmsOrderEntity> page = this.page(
				new Query<OmsOrderEntity>().getPage(params),
				new QueryWrapper<>()
		);


		return new PageUtils(page);
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return: OrderConfirmVo
	 * @Description: 获取购物车里全部购物项以及收货地址 优惠信息等数据 异步编排
	 * !!(异步编排下  新的线程无法获得 ThreadLocal的数据 空指针异常)
	 * 解决方案 : 在每一个新的线程开始之前 在新线程的 RequestContextHolder 放入header的数据(在RequestInterceptor之前) 这样RequestInterceptor中的RequestContextHolder就可以拿到数据
	 * (ServletRequestAttributes)RequestAttributes 转换 后可以拿到request
	 * RequestContextHolder.setRequestAttributes(requestAttributes); 放入主线程数据
	 * <p>
	 * 必须调用get方法或者join方法 等待异步执行完成
	 * join()方法抛出的是uncheck异常（即未经检查的异常),不会强制开发者抛出 会将异常包装成CompletionException异常 /CancellationException异常，但是本质原因还是代码内存在的真正的异常 没有返回值
	 * get()方法抛出的是经过检查的异常，ExecutionException, InterruptedException 需要用户手动处理（抛出或者 try catch） 可以自定义返回值
	 * <p>
	 * get和join只是阻塞直到所有阶段都完成，它不会触发计算；计算会立即安排
	 * <p>
	 * <p>
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public OrderConfirmVo confirmOrder() {
		OrderConfirmVo confirmVo = new OrderConfirmVo();
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
		Long memberId = memberRespVo.getId();
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


		CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				/*调用之前放入请求头数据*/
				RequestContextHolder.setRequestAttributes(requestAttributes);
				R r = memberService.orderAddressInfo(memberId);
				if (r.getCode() != 0) {
					System.out.println(r.getCode());
				}
				List<MemberAddressVo> address = (List<MemberAddressVo>) r.get("address");
				/*收货地址*/
				confirmVo.setAddress(address);
			}
		}, threadPoolExecutor);


		/*查询购物项后查询库存信息*/
		CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				/*调用之前放入请求头数据*/
				RequestContextHolder.setRequestAttributes(requestAttributes);
				List<OrderItemVo> orderItemVos = cartService.orderCartItemList();
				if (orderItemVos != null && orderItemVos.size() > 0) {
					confirmVo.setItems(orderItemVos);
				}
			}
		}, threadPoolExecutor).thenRunAsync(new Runnable() {
			@Override
			public void run() {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				List<OrderItemVo> items = confirmVo.getItems();
				/*收集所有skuId*/
				List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
				R r = wareService.haStock(collect);
				List<SkuHasStockTo> skuHasStockList = r.getData(new TypeReference<List<SkuHasStockTo>>() {
				});
				System.out.println("r=========>"+r);
				if (skuHasStockList != null && skuHasStockList.size() > 0) {
					/*获取对应的库存map*/
					Map<Long, Boolean> map = skuHasStockList.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::isHasStock));

					/*存入库存信息*/
					confirmVo.setStocks(map);
					log.info("库存查询成功{}", map);
				}else {
					confirmVo.setStocks(new HashMap<>());
				}

			}
		});


		/*等待异步任务执行完成*/
		CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFuture1, completableFuture2);
//		completableFuture.join();
		try {
			/*必须调用get方法或者join方法 等待异步执行完成*/
			completableFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}


		Integer integration = memberRespVo.getIntegration();
		confirmVo.setIntegration(integration);

//		TODO  为了防止订单重复提交 防重令牌



		log.info(String.valueOf(confirmVo));
		return confirmVo;
	}

}