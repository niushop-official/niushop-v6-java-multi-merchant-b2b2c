package com.niu.core.service.site.delivery.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 配送服务商列表视图
 */
@Data
public class DeliveryStoreCheckVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer localDeliveryStoreCount;
    private Integer currentLocalDeliveryStoreCount;
    private DeliveryConfigVo localDeliveryConfig;
    private Integer pickUpStoreCount;
    private Integer currentPickUpStoreCount;
    private DeliveryConfigVo pickUpConfig;
}
