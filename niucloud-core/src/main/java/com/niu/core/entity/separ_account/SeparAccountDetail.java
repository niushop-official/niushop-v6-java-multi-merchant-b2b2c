package com.niu.core.entity.separ_account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 实体
*/
@Data
public class SeparAccountDetail implements Serializable {

private static final long serialVersionUID = 1L;

    /**  */
    private Long createTime;

    /**  */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 主体id */
    private Integer mainId;

    /** 主体类型 名称 */
    private String mainName;

    /** 主体类型  plat  site */
    private String mainType;

    /** 备注信息 */
    private String memo;

    /** 分账操作id */
    private Integer separId;

    /** 金额 */
    private BigDecimal value;

    /** 资金来源  商户分账   平台手续费..... */
    private String valueSource;

    /** 资金类型  现金  余额 */
    private String valueType;

}