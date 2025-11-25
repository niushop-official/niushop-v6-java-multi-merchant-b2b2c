package com.niu.core.service.admin.shop.param;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 站点分类参数
 */
@Data
public class SiteShopCategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    private String categoryName;


}
