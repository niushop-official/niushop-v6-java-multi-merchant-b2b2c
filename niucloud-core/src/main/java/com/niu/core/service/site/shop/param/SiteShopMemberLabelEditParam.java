package com.niu.core.service.site.shop.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺会员标签参数
*/
@Data
public class SiteShopMemberLabelEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;

    private Integer siteId;

    @NotNull(message = "labelName参数缺失")
    @NotBlank(message = "标签名称不能为空")
    private String labelName;

    private String memo = "";

    private Integer sort = 0;

}
