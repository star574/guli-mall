package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.service.CategoryBrandRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.lsh.gulimall.product.dao.CategoryDao;
import com.lsh.gulimall.product.entity.CategoryEntity;
import com.lsh.gulimall.product.service.CategoryService;

import com.lsh.gulimall.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author codestar
 */
@Service("categoryService")
@Slf4j
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

	@Autowired
	CategoryBrandRelationService categoryBrandRelationService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> getCategoryServiceList(String info) {
		if (info != null) {
			log.info("查询 " + info);
		}
		List<CategoryEntity> list = StringUtils.isEmpty(info) ? this.list() : this.list(new QueryWrapper<CategoryEntity>().like("name", info));
		List<CategoryEntity> collect = list.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(0L)).map(categoryEntity -> {
			categoryEntity.setChildren(getChildren(categoryEntity, list));
			return categoryEntity;
		}).sorted((categoryEntity1, categoryEntity2) -> categoryEntity1.getSort() - categoryEntity2.getSort()).collect(Collectors.toList());
		return collect;
	}

	@Override
	public boolean deleteById(Long[] catIds) {
		for (Long catId : catIds) {
			CategoryEntity byId = this.getById(catId);
			if (byId == null) {
				return false;
			}
			/*存在子分类*/
			List<CategoryEntity> parentCid = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", catId));
			if (parentCid.size() != 0) {
				return false;
			}
		}

		return this.removeByIds(Arrays.asList(catIds));
	}

	/**
	 * //TODO
	 *
	 * @param catelogId
	 * @return catelogPath完整路径
	 * @throws
	 * @date 2021/6/17 下午10:59
	 * @Description
	 */
	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> longs = new ArrayList<>();
		List<Long> paths = findParentPath(catelogId, longs);
		Collections.reverse(paths);
		return paths.toArray(new Long[0]);
	}

	/*关联更新*/

	@Override
	public boolean updateCascade(CategoryEntity category) {
		/*关联更新*/
		return this.updateById(category) && categoryBrandRelationService.updateCategory(category);
	}

	private List<Long> findParentPath(Long catelogId, List<Long> longs) {
		longs.add(catelogId);
		CategoryEntity categoryEntity = this.getById(catelogId);
		if (categoryEntity.getParentCid() != 0) {
			findParentPath(categoryEntity.getParentCid(), longs);
		}
		return longs;
	}

	public List<CategoryEntity> getChildren(CategoryEntity current, List<CategoryEntity> list) {

		List<CategoryEntity> collect = list.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(current.getCatId())).map(categoryEntity -> {
			categoryEntity.setChildren(getChildren(categoryEntity, list));
			return categoryEntity;
		}).sorted((categoryEntity1, categoryEntity2) -> categoryEntity1.getSort() - categoryEntity2.getSort()).collect(Collectors.toList());

		return collect;
	}
}