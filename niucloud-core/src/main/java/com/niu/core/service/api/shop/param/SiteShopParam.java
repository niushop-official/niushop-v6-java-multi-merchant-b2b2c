package com.niu.core.service.api.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺扩展表参数
*/
@Data
public class SiteShopParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    @NotNull(message = "uid 参数缺失")
    private Integer uid;

    @NotNull(message = "username参数缺失")
    private String username;

    @NotNull(message = "real_name参数缺失")
    private String realName;

    @NotNull(message = "password参数缺失")
    private String password;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;

    private String expireTime = "0";

    private String siteDomain = "";

    /** 营业执照 */
    private String businessLicense = "";

    /** 商家分类id */
    @NotNull(message = "categoryId参数缺失")
    private Integer categoryId;

    /** 是否自营 */
    private Integer isSelf = 0;

}
