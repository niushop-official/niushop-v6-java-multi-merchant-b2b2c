package com.niu.mall.service.api.goods.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsBrowseListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    /** 浏览人 */
    private Integer memberId;

    /** sku_id */
    private Integer skuId;

    /** 商品id */
    private Integer goodsId;

    /** 浏览时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long browseTime;

    /** 商品图片 */
    private String goodsCover;

    /** 商品名称 */
    private String goodsName;

    private BigDecimal price;
    private BigDecimal salePrice;

    private BigDecimal marketPrice;

    private String memberPrice;

    private String memberDiscount;
    private String vipCardDiscount;
    private String svipDiscount;
    private String svipPrice;

    private BigDecimal showPrice;

    private String showType;
    private Integer status;
    private Long deleteTime;
    private GoodsInfo goods;
    
    public String getGoodsCoverThumbMid() {
        return SpringContext.bean(ICoreUploadService.class).thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
    }

    @Data
    public static class GoodsInfo {
        private Integer status;
        private Long deleteTime;
    }

}
