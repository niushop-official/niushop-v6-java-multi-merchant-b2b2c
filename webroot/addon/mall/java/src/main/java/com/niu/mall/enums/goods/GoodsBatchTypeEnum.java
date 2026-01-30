package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum GoodsBatchTypeEnum {
    LABEL("label", "商品标签"),
    SERVICE("service", "商品服务"),
    PLAT_CATEGORY("plat_category", "商品类目"),
    CATEGORY("category", "商品分类"),
    BRAND("brand", "商品品牌"),
    DIY_FORM("diy_form", "商品表单"),
    DELIVERY("delivery", "配送方式"),
    STOCK("stock", "商品库存"),
    MEMBER_DISCOUNT("member_discount", "会员折扣");

    private final String type;
    private final String name;

    /**
     * 获取所有批量设置类型的映射
     * @return Map<String, Map<String, Object>>
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (GoodsBatchTypeEnum item : GoodsBatchTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取枚举名称
     * @param type 批量设置类型
     * @return String
     */
    public static String getNameByType(String type) {
        for (GoodsBatchTypeEnum item : GoodsBatchTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 通过type获取枚举对象
     * @param type 批量设置类型
     * @return Optional<GoodsBatchTypeEnum>
     */
    public static GoodsBatchTypeEnum getEnumByType(String type) {
        for (GoodsBatchTypeEnum enumValue : GoodsBatchTypeEnum.values()) {
            if (enumValue.getType().equals(type)) {
                return enumValue;
            }
        }
        return null;
    }

}