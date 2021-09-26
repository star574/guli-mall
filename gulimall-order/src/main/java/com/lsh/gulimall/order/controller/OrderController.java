package com.lsh.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.service.OrderService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;



/**
 * 订单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
@RestController
@RequestMapping("generator/order")
public class OrderController {
    @Autowired
    private OrderService OrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = OrderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity Order = OrderService.getById(id);

        return R.ok().put("Order", Order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:order:save")
    public R save(@RequestBody OrderEntity Order){
		OrderService.save(Order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:order:update")
    public R update(@RequestBody OrderEntity Order){
		OrderService.updateById(Order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:order:delete")
    public R delete(@RequestBody Long[] ids){
		OrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
