package com.niu.core.service.admin.site.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 结算账户VO
 */
@Data
public class SiteSettAccountVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 账户类型 */
    private Integer type;

    /** 账户类型名称 */
    private String typeName;

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