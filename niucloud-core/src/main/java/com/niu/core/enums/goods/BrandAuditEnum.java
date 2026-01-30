package com.niu.core.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum BrandAuditEnum {
    AUDIT_WAIT(1, "待审核"),
    AUDIT_PASS(2, "审核通过"),
    AUDIT_REJECT(-1, "审核拒绝");

    private final Integer status;
    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(BrandAuditEnum item : BrandAuditEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByType(Integer status){
        for(BrandAuditEnum item : BrandAuditEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
