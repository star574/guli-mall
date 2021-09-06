package com.lsh.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import com.lsh.gulimall.order.dao.OmsOrderDao;
import com.lsh.gulimall.order.entity.OmsOrderEntity;
import com.lsh.gulimall.order.feign.CartService;
import com.lsh.gulimall.order.feign.MemberService;
import com.lsh.gulimall.order.interceptor.LoginUserInterceptor;
import com.lsh.gulimall.order.service.OmsOrderService;
import com.lsh.gulimall.order.vo.MemberAddressVo;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import com.lsh.gulimall.order.vo.OrderItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("omsOrderService")
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderDao, OmsOrderEntity> implements OmsOrderService {

	@Autowired
	MemberService memberService;

	@Autowired
	StringRedisTemplate redisTemplate;

	private final String CART_PREFIX = "gulimall:cart:";

	@Autowired
	private CartService cartService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OmsOrderEntity> page = this.page(
				new Query<OmsOrderEntity>().getPage(params),
				new QueryWrapper<OmsOrderEntity>()
		);

		return new PageUtils(page);
	}

	/**
	 * @param null
	 * @return: null
	 * @Description: 返回订单确认页需要的数据
	 */
	@Override
	public OrderConfirmVo confirmOrder() throws ServerException {
		OrderConfirmVo confirmVo = new OrderConfirmVo();
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
		Long memberId = memberRespVo.getId();

		R r = memberService.orderAddressInfo(memberId);
		if (r.getCode() != 0) {
			System.out.println(r.getCode());
			return null;
		}
		List<MemberAddressVo> address = (List<MemberAddressVo>) r.get("address");
		/*收货地址*/
		confirmVo.setAddress(address);

		/*购物车里的购物项*/
//		memberRespVo
		List<OrderItemVo> orderItemVos = cartService.orderCartItemList();
		if (orderItemVos != null && orderItemVos.size() > 0) {
			confirmVo.setItems(orderItemVos);
		}
		Integer integration = memberRespVo.getIntegration();
		confirmVo.setIntegration(integration);


//		TODO  为了防止订单重复提交 防重令牌

		log.info(String.valueOf(confirmVo));
		return confirmVo;
	}

}