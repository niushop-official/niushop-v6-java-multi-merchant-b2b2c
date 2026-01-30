package com.niu.core.service.site.shop.vo;

import lombok.Data;

import java.io.Serializable;

/**
* 店铺扩展表详情
*/
@Data
public class SiteShopCashOutCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页面 */
    private String page;

    /** 二维码 */
    private String qrcode;

    /** 二维码 */
    private String urlLink;

}