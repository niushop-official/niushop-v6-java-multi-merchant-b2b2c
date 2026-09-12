package com.niu.core.service.site.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺会员表参数
 */
@Data
public class SiteShopMemberSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 累计消费金额 */
    private BigDecimal comsumMoney;

    /** 消费次数 */
    private Integer comsumNum;

    /** 首次消费时间 */
    private Long firstConsumTime;

    /** 主键 */
    private Integer id;

    /** 是否关注 */
    private Integer isFollow;

    /** 最近一次消费时间 */
    private Long lastConsumTime;

    /** 会员id */
    private Integer memberId;

    /** 店铺会员标签 */
    private String shopMemberLabel;

    /** 站点id */
    private Integer siteId;

    /** 关键字 */
    private String keyword;

    /** 注册途径 */
    private String registerChannel;

    /** 所属插件 */
    private String addon;

    private String[] createTime;
}
