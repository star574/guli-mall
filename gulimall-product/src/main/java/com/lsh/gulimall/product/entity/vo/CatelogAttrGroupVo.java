package com.lsh.gulimall.product.entity.vo;

import com.lsh.gulimall.product.entity.AttrEntity;
import com.lsh.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author codestar
 */
@Data
public class CatelogAttrGroupVo extends AttrGroupEntity {
	List<AttrVo> attrs;
}
