package com.niu.core.service.site.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 店铺会员标签参数
 */
@Data
public class SiteShopMemberLabelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelName;

}
