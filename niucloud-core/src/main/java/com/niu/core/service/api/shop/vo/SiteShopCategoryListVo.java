package com.niu.core.service.api.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 站点分类列表
*/
@Data
public class SiteShopCategoryListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 分类id */
        private Integer categoryId;

        /** 分类名称 */
        private String categoryName;



}
