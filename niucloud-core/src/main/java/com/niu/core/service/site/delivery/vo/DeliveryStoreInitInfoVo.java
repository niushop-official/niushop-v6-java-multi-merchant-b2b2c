package com.niu.core.service.site.delivery.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryStoreVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryVo;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送服务商列表视图
 */
@Data
public class DeliveryStoreInitInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String weekList;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String timeIntervalList;
    private LocalDeliveryStoreVo localDeliveryConfig;
}
