package com.niu.core.service.admin.marketing.param;
import cn.hutool.json.JSONArray;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.*;

/**
 * 优惠券参数
*/
@Data
public class MallCouponParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotNull(message = "title参数缺失")
    private String title;


    private Integer isAllSiteJoin;
    private Integer[] joinSiteIds;
    @NotNull(message = "remainCount参数缺失")
    private Integer remainCount;

    @NotNull(message = "limitCount参数缺失")
    private Integer limitCount;

    @NotNull(message = "price参数缺失")
    private BigDecimal price;

    @NotNull(message = "minConditionMoney参数缺失")
    private BigDecimal minConditionMoney;

    @NotNull(message = "type参数缺失")
    private Integer type;

    /** 业务类型 mall/waimai/o2o */
    private String addon;

    @NotNull(message = "receiveType参数缺失")
    private Integer receiveType;

    private String[] receiveTime;

    private Integer receiveTypeTime;

    @NotNull(message = "validType参数缺失")
    private Integer validType;

    private String validTime;

    @NotNull(message = "length参数缺失")
    private Integer length;

    private Integer threshold;

    private Integer limit;

    private Integer[] goodsIds;

    private Integer[] brandIds;

    private JSONArray goodsCategoryIds;

}
