package com.niu.mall.service.site.marketing.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 店铺营销活动（整体活动）参数
 */
@Data
public class MallDiscountSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activeName;

    private String activeStatus;


}
