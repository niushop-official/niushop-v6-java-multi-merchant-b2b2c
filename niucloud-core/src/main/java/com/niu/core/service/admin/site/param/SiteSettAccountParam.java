package com.niu.core.service.admin.site.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 结算账户参数
 */
@Data
public class SiteSettAccountParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（编辑时需要） */
    private Integer id;

    /** 账户类型（1银行卡 2支付宝 3微信 4微信零钱） */
    @NotNull(message = "账户类型参数缺失")
    private Integer type;

    /** 持卡人/账户姓名 */
    private String name;

    /** 账号 */
    private String accountNo;

    /** 收款码图片 */
    private String accountImg;

    /** 开户行名称 */
    private String bankName;

    /** 开户行地址 */
    private String bankAddress;

    /** 绑定的会员ID */
    private Integer bindMemberId;
}