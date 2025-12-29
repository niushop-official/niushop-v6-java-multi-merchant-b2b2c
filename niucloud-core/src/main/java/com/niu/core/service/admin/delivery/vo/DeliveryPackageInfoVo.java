package com.niu.core.service.admin.delivery.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ShopDeliveryCompany列表视图
 */
@Data
public class DeliveryPackageInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer siteId;
    private Integer localDeliverId;
    private String status;
    /**
     * 业务ID或编号
     */
    private String tradeNo;

    /**
     * 包裹名称
     */
    private String name;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 详细配送方式
     */
    private String subDeliveryType;

    /**
     * 快递公司ID
     */
    private Integer expressCompanyId;

    /**
     * 配送单号
     */
    private String expressNumber;

    /**
     * 创建时间（时间戳，序列化为字符串）
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 快递公司ID（用于内部处理）
     */
    private Integer companyId;

    /**
     * 快递公司名称（用于内部处理）
     */
    private String companyName;

    /**
     * 快递公司编码（用于内部处理）
     */
    private String expressNo;

    /**
     * 快递公司信息（嵌套对象，与PHP返回结构一致）
     */
    private CompanyInfo company;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 物流跟踪信息
     */
    private DeliveryTraceVo traces;

    /**
     * 订单商品列表
     */
    private List<Map<String, Object>> tradeItemList;

    /**
     * 快递公司信息内部类
     */
    @Data
    public static class CompanyInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Integer companyId;
        private String companyName;
        private String expressNo;
    }
}