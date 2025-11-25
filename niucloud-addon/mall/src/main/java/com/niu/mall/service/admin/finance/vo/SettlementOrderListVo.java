package com.niu.mall.service.admin.finance.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.enums.order.*;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.service.admin.order.vo.MallOrderLogListVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder视图
 */
@Data
public class SettlementOrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  // 
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private BigDecimal orderMoney;  // 订单金额
    private BigDecimal shopMoney;
    private BigDecimal mallMoney;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;  // 订单支付时间

    private SiteInfo site;
    private String orderFrom;
    private String orderFromName;
    private String settlementStatus;
    private String settlementStatusName;
    private String frontEndLogo;
    private String phone;

    @Data
    public static class SiteInfo{
        private Integer siteId;
        private String siteName;
        private String logo;
        private String frontEndLogo;
        private String phone;
    }
    private String siteName;
    private String logo;

    public String getOrderFromName() {
        if (ObjectUtil.isNotEmpty(this.orderFrom) && ObjectUtil.isNotEmpty(ChannelEnum.getNameByCode(this.orderFrom))) {
            return ChannelEnum.getNameByCode(this.orderFrom);
        }
        return OrderFromTypeEnum.getType().getStr(this.orderFrom, "");
    }

    public String getSettlementStatusName() {
        if (ObjectUtil.isNotEmpty(this.settlementStatus)) {
            return SettlementEnum.getNameByStatus(this.settlementStatus);
        }
        return "";
    }


}
