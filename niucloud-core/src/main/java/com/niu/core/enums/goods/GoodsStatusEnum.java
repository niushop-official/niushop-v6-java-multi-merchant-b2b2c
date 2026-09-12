package com.niu.core.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品状态（售卖状态）
 * <p>
 * v104 升级后，商品状态拆分为两个独立字段：
 * <ul>
 *   <li>{@code status} — 售卖状态（本枚举），取值：1=销售中 0=仓库中 -3=强制下架</li>
 *   <li>{@code audit_status} — 审核状态，使用 {@link GoodsAuditStatusEnum}，取值：1=待审核 2=审核通过 -1=审核未通过</li>
 * </ul>
 * <p>
 * 旧版 WAIT_AUDIT(-1)/AUDIT_REFUSE(-2) 已迁移到 audit_status 字段，
 * 仅在 v104 升级脚本中用于识别历史数据，**正常业务逻辑禁止使用**。
 */
@Getter
@AllArgsConstructor
public enum GoodsStatusEnum {
    ONLINE("销售中", 1),
    OFFLINE("仓库中", 0),

    /**
     * @deprecated 已迁移到 {@link GoodsAuditStatusEnum#WAIT_AUDIT}，仅在 v104 升级脚本中用于识别历史数据
     */
    @Deprecated
    WAIT_AUDIT("待审核", -1),

    /**
     * @deprecated 已迁移到 {@link GoodsAuditStatusEnum#AUDIT_REFUSE}，仅在 v104 升级脚本中用于识别历史数据
     */
    @Deprecated
    AUDIT_REFUSE("审核拒绝", -2),

    AUDI_TO_FINE("强制下架", -3);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (GoodsStatusEnum item: GoodsStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (GoodsStatusEnum item: GoodsStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (GoodsStatusEnum item : GoodsStatusEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
