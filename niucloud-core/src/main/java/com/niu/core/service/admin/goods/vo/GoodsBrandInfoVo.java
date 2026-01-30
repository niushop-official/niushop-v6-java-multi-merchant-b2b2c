package com.niu.core.service.admin.goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.BrandPopularEnum;
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
    private Integer status; // 显示状态 1显示 2隐藏
    private Integer isHot; // 是否热门 1是 2否
    private String statusName;
    private String isHotName;
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String colorJson;
    public String getStatusName() {
        return BrandStatusEnum.getNameByType(this.status);
    }
    public String getIsHotName() {
        return BrandPopularEnum.getNameByType(this.isHot);
    }
}
