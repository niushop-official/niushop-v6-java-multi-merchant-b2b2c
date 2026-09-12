package com.niu.core.service.admin.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.BrandPopularEnum;
import com.niu.core.enums.goods.BrandStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌详情视图
 */
@Data
public class GoodsBrandInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;
    private Integer siteId;
    private String brandName;
    private String logo;
    private String desc;
    private Integer sort;
    private Integer status;
    private Integer isHot;
    private String statusName;
    private String isHotName;
    private String addon;

    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String colorJson;

    public String getStatusName() {
        return BrandStatusEnum.getNameByType(this.status);
    }

    public String getIsHotName() {
        return BrandPopularEnum.getNameByType(this.isHot);
    }
}
