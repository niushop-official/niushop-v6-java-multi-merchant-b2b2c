package com.niu.core.enums.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupportTradeEnum {
    MALL("mall","商城"),
    SUPPLY("supply","供应商");
    private final String type;

    private final String name;

    public static String getNameByType(String type){
        for(SupportTradeEnum item : SupportTradeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
