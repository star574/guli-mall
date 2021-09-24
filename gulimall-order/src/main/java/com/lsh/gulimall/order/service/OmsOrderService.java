package com.lsh.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.order.entity.OmsOrderEntity;
import com.lsh.gulimall.order.vo.OrderConfirmVo;
import com.lsh.gulimall.order.vo.OrderSubmitVo;
import com.lsh.gulimall.order.vo.SubmitOrderResponseVo;

import java.rmi.ServerException;
import java.util.Map;

/**
 * 订单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

	OrderConfirmVo confirmOrder() throws ServerException;


	SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);
}

