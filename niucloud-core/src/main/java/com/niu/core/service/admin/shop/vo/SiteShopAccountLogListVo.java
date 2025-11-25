package com.niu.core.service.admin.shop.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;

import com.niu.core.enums.shop.ShopAccountEnum;
import lombok.Data;
import java.math.BigDecimal;
/**
* 商家结算账单记录表列表
*/
@Data
public class SiteShopAccountLogListVo implements Serializable {

private static final long serialVersionUID = 1L;


        /** 来源类型（订单结算，提现） */
        private String fromType;

        /**  */
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

        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;
        public String getFromTypeName(){
                return ObjectUtil.isNotEmpty(ShopAccountEnum.getNameByType(this.fromType)) ? ShopAccountEnum.getNameByType(this.fromType).getName() : "";
        }


}
