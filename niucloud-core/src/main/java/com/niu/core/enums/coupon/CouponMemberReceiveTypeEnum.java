package com.niu.core.enums.coupon;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

import java.lang.reflect.Method;

/**
 * 会员优惠券领取方式（合并各插件枚举 / JSON）
 */
public class CouponMemberReceiveTypeEnum {

    private static final String FRONT_WAREHOUSE_ENUM =
            "com.niu.front_warehouse.enums.coupon.FrontWarehouseCouponReceiveTypeEnum";

    public static JSONArray getReceiveType() {
        JSONArray merged = new JSONArray();
        // 前置仓：枚举优先
        JSONArray frontWarehouse = loadEnumArray(FRONT_WAREHOUSE_ENUM);
        if (frontWarehouse != null && !frontWarehouse.isEmpty()) {
            mergeByName(merged, frontWarehouse);
        }
        // 其余插件仍可贡献 JSON（如 mall）
        JSONArray fromJson = new JsonModuleLoader().mergeResultSet("shop_coupon/coupon_receive_type.json");
        if (fromJson != null && !fromJson.isEmpty()) {
            mergeByName(merged, fromJson);
        }
        return merged;
    }

    public static String getReceiveTypeName(String type) {
        if (StrUtil.isBlank(type)) {
            return "";
        }
        JSONArray types = getReceiveType();
        for (int i = 0; i < types.size(); i++) {
            JSONObject item = types.getJSONObject(i);
            if (type.equals(item.getStr("name", ""))) {
                return item.getStr("title", "");
            }
        }
        return "";
    }

    private static void mergeByName(JSONArray target, JSONArray source) {
        for (int i = 0; i < source.size(); i++) {
            JSONObject item = source.getJSONObject(i);
            String name = item.getStr("name", "");
            if (StrUtil.isBlank(name)) {
                continue;
            }
            boolean exists = false;
            for (int j = 0; j < target.size(); j++) {
                if (name.equals(target.getJSONObject(j).getStr("name", ""))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                target.add(item);
            }
        }
    }

    private static JSONArray loadEnumArray(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("toJSONArray");
            Object result = method.invoke(null);
            if (result instanceof JSONArray array) {
                return array;
            }
        } catch (ClassNotFoundException ignored) {
            // 插件未安装
        } catch (Exception ignored) {
            // 忽略枚举加载失败
        }
        return null;
    }
}
