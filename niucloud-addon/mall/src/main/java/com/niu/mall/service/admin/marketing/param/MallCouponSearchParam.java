package com.niu.mall.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 优惠券参数
 */
@Data
public class MallCouponSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Integer status;


}
