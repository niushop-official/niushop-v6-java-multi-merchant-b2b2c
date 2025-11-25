package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopCashOutEnum {
    WAIT_AUDIT(1, "待审核"),
    WAIT_TRANSFER(2, "待转账"),
    TRANSFER_ING(4, "转帐中"),
    TRANSFERED(3, "已转账"),
    REFUSE(-1, "已拒绝"),
    CANCEL(-2, "已取消");

    private final Integer state;

    private final String name;


    /**
     * 获取店铺申请状态
     * @return
     */
    public static Map<Integer, String> getStatus() {
        Map<Integer, String> map = new HashMap<>();
        for(ShopCashOutEnum item : ShopCashOutEnum.values())
        {
            map.put(item.getState(), item.getName());
        }
        return map;
    }
}
