package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatusEnum {
    STATUS_WAIT("待支付", 0),
    STATUS_ING("支付中", 1),
    STATUS_FINISH("已支付", 2),
    STATUS_AUDIT("待审核", 3),
    STATUS_CANCLE("已取消", -1)
    ;

    private final String name;

    private final Integer code;


    /**
     * 通过status获取当前名称
     * @param key
     * @return
     */
    public static String getNameByStatus(Integer key){
        for(PayStatusEnum item : PayStatusEnum.values())
        {
            if(item.getCode().equals(key))
            {
                return item.getName();
            }
        }
        return "";
    }
}
