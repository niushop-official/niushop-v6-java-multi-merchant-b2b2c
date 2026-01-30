package com.niu.core.enums.recharge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RechargePackageEnum {
    ON(1,"开启"),
    OFF(0,"关闭");

    private final Integer status;
    private final String name;

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByType(Integer status){
        for(RechargePackageEnum item : RechargePackageEnum.values()){
            if(item.getStatus().equals(status)){
                return item.getName();
            }
        }
        return "";
    }
}
