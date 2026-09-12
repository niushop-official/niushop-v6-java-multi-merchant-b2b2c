package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点结算账户实体
 */
@Data
//@TableName("niu_site_sett_account")
public class SiteSettAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 所属主体ID */
    private Integer mainId;

    /** 主体类型 */
    private String mainType;

    /** 账户类型（1银行卡 2支付宝 3微信 4微信零钱） */
    private Integer type;

    /** 持卡人/账户姓名 */
    private String name;

    /** 账号 */
    private String accountNo;

    /** 收款码图片（支付宝/微信） */
    private String accountImg;

    /** 开户行名称 */
    private String bankName;

    /** 开户行地址 */
    private String bankAddress;

    /** 绑定的会员ID */
    private Integer bindMemberId;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;
}