package com.niu.core.service.site.delivery.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.delivery.ShippingTemplateEnum;
import com.niu.core.enums.order.TradeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ShopDeliveryShippingTemplate列表视图
 */
@Data
public class DeliveryShippingTemplateListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer templateId;  // 
    private Integer siteId;  // 站点id
    private String templateName;  // 模板名称

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String supportTrade;  // 交易类型
    private String feeType;  // 运费计算方式1.重量2体积3按件
    private String feeTypeName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 修改时间
    private Integer isFreeShipping;  // 该区域是否包邮
    private Integer noDelivery;  // 是否指定该区域不配送

    public String getFeeTypeName() {
        return ShippingTemplateEnum.getNameByType(this.feeType);
    }

    public List<String> getSupportTradeName() {

        List<String> tradeTypeNameList = new ArrayList<>();
        if(ObjectUtil.isEmpty(supportTrade)) {
            return tradeTypeNameList;
        }
        JSONObject tradeTypeJson = TradeTypeEnum.getType();
        JSONArray supportTradeJson = JSONUtil.parseArray(supportTrade);
        for (int i = 0; i < supportTradeJson.size(); i++)
        {
            if(tradeTypeJson.containsKey(supportTradeJson.getStr(i)))
            {
                tradeTypeNameList.add(tradeTypeJson.getStr(supportTradeJson.getStr(i)));
            }
        }
        return tradeTypeNameList;
    }
}
