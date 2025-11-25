package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 商家结算账单记录表实体
*/
@Data
public class SiteShopAccountLog implements Serializable {

private static final long serialVersionUID = 1L;

    /** 创建时间 */
    private Long createTime;

    /** 来源类型（订单结算，提现） */
    private String fromType;

    /**  */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 用户id */
    private Integer memberId;

    /** 备注信息 */
    private String memo;

    /** 变动账单金额 */
    private BigDecimal money;

    /** 变动后的账户余额 */
    private BigDecimal moneySum;

    /** 关联Id（结算id，提现id） */
    private String relatedId;

    /** 站点id */
    private Integer siteId;

}