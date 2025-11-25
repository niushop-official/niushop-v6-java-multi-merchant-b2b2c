package com.niu.core.service.site.delivery.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送方式设置参数
*/
@Data
public class DeliveryConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private JSONArray value;

}
