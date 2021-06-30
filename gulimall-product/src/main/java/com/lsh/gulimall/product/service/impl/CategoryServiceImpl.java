package com.lsh.gulimall.product.service.impl;

import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.product.entity.vo.frontvo.Catelog2Vo;
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

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 21:58
	 * @Description 获取前端所有一级分类
	 */
	@Override
	public List<CategoryEntity> getOneLevelCategorys() {
		return this.list(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/30 22:48
	 * @Description 获取前端全部分类数据
	 */
	@Transactional
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogJson() {
		/*获取一级分类*/
		List<CategoryEntity> oneLevelCategorys = getOneLevelCategorys();
		if (oneLevelCategorys != null) {
			/*key:k.getCatId() value: catelog2VoList*/
			Map<String, List<Catelog2Vo>> map = oneLevelCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
					categoryEntity1 -> {
						/*获取一级分类下的二级分类列表*/
						List<CategoryEntity> categoryEntities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", categoryEntity1.getCatId()));
						/*获取二级下三级分类列表*/
						/*处理二级分类*/
						if (categoryEntities != null) {
							List<Catelog2Vo> catelog2VoList = categoryEntities.stream().map(categoryEntity2 -> {
								List<CategoryEntity> categoryEntityList = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", categoryEntity2.getCatId()));
								/*处理三级分类*/
								if (categoryEntityList != null) {
									List<Catelog2Vo.Catelog3Vo> catelog3VoList = categoryEntityList.stream().map(categoryEntity3 -> {
										if (categoryEntity3.getName().equals("米粉/菜粉")) {
											log.warn(categoryEntity3.toString());
										}
										Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(categoryEntity2.getCatId().toString(), categoryEntity3.getCatId().toString(), categoryEntity3.getName());
										return catelog3Vo;
									}).collect(Collectors.toList());
									Catelog2Vo catelog2Vo = new Catelog2Vo(categoryEntity1.getCatId().toString(), catelog3VoList, categoryEntity2.getCatId().toString(), categoryEntity2.getName());
									return catelog2Vo;
								}
								return null;
							}).collect(Collectors.toList());
							return catelog2VoList;
						}
						return null;
					}));
			return map;
		}
		return null;
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