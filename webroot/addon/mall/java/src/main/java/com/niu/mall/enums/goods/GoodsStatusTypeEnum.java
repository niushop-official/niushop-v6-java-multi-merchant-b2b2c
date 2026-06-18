package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum GoodsStatusTypeEnum {
    ON_SALE("销售中", 1),
    IN_WAREHOUSE("仓库中", 2),
    PENDING_REVIEW("待审核", 3),
    REVIEW_FAILS("审核未通过", 4),
    FORCE_REMOVAL("强制下架", 5),
    SUPPLY_UPDATE("货源更新", 6);

    private final String name;

    private final Integer status;

    public static Optional<GoodsStatusTypeEnum> getEnumByStatus(Integer status) {
        return Arrays.stream(GoodsStatusTypeEnum.values())
                .filter(e -> e.getStatus().equals(status))
                .findFirst();
    }

    public static String getNameByStatus(Integer status) {
        for (GoodsStatusTypeEnum item : GoodsStatusTypeEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status) {
        Map<String, Object> map = new HashMap<>();
        for (GoodsStatusTypeEnum item : GoodsStatusTypeEnum.values()) {
            if (item.getStatus().equals(status)) {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        for (GoodsStatusTypeEnum item : GoodsStatusTypeEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getStatus());
            map.put("goods_status_" + item.getStatus(), mapItem);
        }
        return map;
    }
}
