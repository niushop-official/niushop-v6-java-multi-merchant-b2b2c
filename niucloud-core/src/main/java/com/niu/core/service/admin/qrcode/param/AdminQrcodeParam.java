package com.niu.core.service.admin.qrcode.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AdminQrcodeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 场景：wap_index、shop_index、goods_detail 等 */
    private String scene;

    /** link / image */
    private String type;

    /** h5 / weapp / wechat */
    private String channel;

    /** 业务参数 */
    private Map<String, Object> param;
}
