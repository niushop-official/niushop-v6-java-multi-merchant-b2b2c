package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ShopMemberIsFollowEnum {
    NOT_FOLLOW(0, "未关注"),
    FOLLOWED(1, "已关注");

    private final Integer status;

    private final String name;


    /**
     * 获取店铺关注状态
     * @return
     */
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(ShopMemberIsFollowEnum item : ShopMemberIsFollowEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getNameByStatus(Integer status) {
        for(ShopMemberIsFollowEnum item : ShopMemberIsFollowEnum.values())
        {
            if(Objects.equals(item.getStatus(), status))
            {
                return item.getName();
            }
        }
        return "";
    }

}
