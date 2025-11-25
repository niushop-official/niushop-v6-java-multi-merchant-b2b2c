package com.niu.core.service.admin.account.vo;

import com.niu.core.enums.account.SeparMainTypeEnum;
import com.niu.core.enums.account.SeparValueSourceEnum;
import com.niu.core.enums.account.SeparValueTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeparDetailVo {

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

    private String valueSourceName;

    private String valueTypeName;

    private String mainTypeName;

    public String getValueSourceName() {
        return SeparValueSourceEnum.getName(valueSource);
    }

    public String getValueTypeName() {
        return SeparValueTypeEnum.getName(valueType);
    }

    public String getMainTypeName() {
        return SeparMainTypeEnum.getName(mainType);
    }
}
