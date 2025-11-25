package com.niu.core.service.admin.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 店铺扩展表参数
 */
@Data
public class SiteShopAccountInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer bankType;
    private String bankAccountName;
    private String bankAccountNo;
    private String bankName;
    private String bankAddress;
    private String alipayName;//支付宝姓名
    private String alipayAccountNo;//支付宝账号
    private String alipayPaymentCode;//支付宝支付码
    private String wechatName;//微信名称
    private String wechatAccountNo;//微信账号
    private String wechatPaymentCode;//微信收款码图片



}
