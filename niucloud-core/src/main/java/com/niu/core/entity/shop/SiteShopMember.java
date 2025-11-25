package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 店铺会员表实体
*/
@Data
public class SiteShopMember implements Serializable {

private static final long serialVersionUID = 1L;

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

}