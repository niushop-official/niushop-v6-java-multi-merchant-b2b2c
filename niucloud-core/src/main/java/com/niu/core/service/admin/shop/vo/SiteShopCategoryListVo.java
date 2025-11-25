package com.niu.core.service.admin.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
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

        /** 手续费比率 */
        private BigDecimal commissionRate;


        /** 分类图 */
        private String image;

        /** 排序号 */
        private Integer sort;


        /** 添加时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;



}
