package com.niu.core.service.site.goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.BrandStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsBrand视图
 */
@Data
public class GoodsBrandInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;  // 品牌ID
    private Integer siteId;  // 站点id
    private String brandName;  // 品牌名称
    private String logo;  // 品牌logo
    private String desc;  // 品牌介绍
    private Integer sort;  // 排序
    private Integer status;  // 排序
    private String statusName;
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String colorJson;
    public String getStatusName() {
        return BrandStatusEnum.getNameByType(this.status);
    }
}
