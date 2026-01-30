package com.niu.core.entity.recharge;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员充值套餐实体
 */
@Data
public class RechargePackage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "package_id", type = IdType.AUTO)
    private Integer packageId;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 充值金额
     */
    private BigDecimal price;

    /**
     * 赠送金额
     */
    private BigDecimal giveMoney;

    /**
     * 赠送积分
     */
    private Integer point;

    /**
     * 赠送成长值
     */
    private Integer growth;

    /**
     * 赠品json
     */
    private String giftJson;

    /**
     * 发放数量
     */
    private Integer saleNum;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态（0关闭 1开启）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

}