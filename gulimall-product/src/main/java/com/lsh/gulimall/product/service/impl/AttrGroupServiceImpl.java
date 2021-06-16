package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.AttrGroupDao;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import com.lsh.gulimall.product.service.AttrGroupService;
import com.lsh.gulimall.common.utils.Query;
import org.springframework.util.StringUtils;


/**
 * @author codestar
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Long catelogId, Map<String, Object> params) {
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();


		if (catelogId != 0) {
			wrapper = wrapper.eq("catelog_id", catelogId);
		}
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper = wrapper.and(wra -> {
				wra.like("attr_group_id", key)
						.or().like("attr_group_name", key)
						.or().like("sort", key)
						.or().like("icon", key);
			});
		}
		IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);

		return new PageUtils(page);
	}

}