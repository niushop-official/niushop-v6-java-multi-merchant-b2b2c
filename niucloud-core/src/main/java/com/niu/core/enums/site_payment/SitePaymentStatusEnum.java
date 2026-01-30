package com.niu.core.enums.site_payment;

import com.niu.core.enums.site.SiteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 站点支付申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum SitePaymentStatusEnum {
    
    ON("on", "正常"),
    OFF("off", "已到期");

    private final String status;
    private final String name;


    /**
     * status
     * @param status
     * @return
     */
    public static String getNameByStatus(String status){
        for(SitePaymentStatusEnum item : SitePaymentStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}

