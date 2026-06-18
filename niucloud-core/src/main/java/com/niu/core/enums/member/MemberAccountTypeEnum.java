package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum MemberAccountTypeEnum {

    POINT("point", "积分"),
    BALANCE("balance", "余额"),
    MONEY("money", "可提现余额"),
    COMMISSION("commission", "佣金"),
    GROWTH("growth", "成长值"),
    ;

    private final String type;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(MemberAccountTypeEnum.values())
                .collect(Collectors.toMap(MemberAccountTypeEnum::getType, MemberAccountTypeEnum::getName));
    }

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        return Arrays.stream(MemberAccountTypeEnum.values())
                .filter(e -> e.getType().equals(type))
                .map(MemberAccountTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Boolean containsByType(String type) {
        return Arrays.stream(MemberAccountTypeEnum.values())
                .map(MemberAccountTypeEnum::getType)
                .toList()
                .contains(type);
    }

}
