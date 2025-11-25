package com.niu.mall.service.site.order.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单参数
 */
@Data
public class MallOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchType;

    private String searchName;

    private Integer status;

    private String payType;

    private String orderFrom;

    private String[] createTime;

    private String[] payTime;

    private String activityType;

    private String keyword;
}
