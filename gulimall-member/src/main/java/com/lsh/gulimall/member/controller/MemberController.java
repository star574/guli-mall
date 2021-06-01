package com.lsh.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;


import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.member.Feign.MemberFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lsh.gulimall.member.entity.MemberEntity;
import com.lsh.gulimall.member.service.MemberService;
import com.lsh.gulimall.common.utils.PageUtils;


/**
 * 会员
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:34:03
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberFeignClient memberFeignClient;

	/*服务调用*/
	@GetMapping("/")
	R getList() {
		return memberFeignClient.list();
	}
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("member:member:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = memberService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("member:member:info")
	public R info(@PathVariable("id") Long id) {
		MemberEntity member = memberService.getById(id);

		return R.ok().put("member", member);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("member:member:save")
	public R save(@RequestBody MemberEntity member) {
		memberService.save(member);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("member:member:update")
	public R update(@RequestBody MemberEntity member) {
		memberService.updateById(member);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("member:member:delete")
	public R delete(@RequestBody Long[] ids) {
		memberService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
