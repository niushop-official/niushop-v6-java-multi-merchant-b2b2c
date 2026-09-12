package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺会员表实体
 */
@Data
public class SiteShopMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所属插件（mall/o2o 等） */
    private String addon;

    /** 累计消费金额 */
    private BigDecimal comsumMoney;

    /** 消费次数 */
    private Integer comsumNum;

    /** 首次消费时间 */
    private Long firstConsumTime;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
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

    /** 创建时间 */
    private Long createTime;

    /** 是否老用户，1是 0否 */
    private Integer isOld;

}