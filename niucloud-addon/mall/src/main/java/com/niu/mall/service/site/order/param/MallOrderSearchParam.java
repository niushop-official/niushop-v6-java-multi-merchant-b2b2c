package com.niu.mall.service.site.order.param;
import com.alibaba.fastjson2.annotation.JSONField;
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

    private String deliveryType;

    private String goodsSource;

    private String keyword;

    private String orderKeyword;

    private String orderNo;

    @JSONField(name = "extend_field_1")
    private Integer extendField1;

    @JSONField(name = "extend_field_2")
    private Integer extendField2;
}
