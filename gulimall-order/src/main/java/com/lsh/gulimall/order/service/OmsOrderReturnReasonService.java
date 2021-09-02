package com.lsh.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.order.entity.OmsOrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
public interface OmsOrderReturnReasonService extends IService<OmsOrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

