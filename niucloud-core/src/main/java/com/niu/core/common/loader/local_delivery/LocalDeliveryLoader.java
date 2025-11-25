package com.niu.core.common.loader.local_delivery;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.SystemLoader;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeLoader;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class LocalDeliveryLoader extends SystemLoader {

    /**
     * 获取同城配送驱动
     *
     * @return
     */
    public static JSONObject getType() {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("delivery/local_delivery_type.json");
        return jsonObject;
    }

    /**
     * @param key
     * @return
     */
    public static BaseLocalDelivery getDriver(String key) {
        JSONObject jsonObject = LocalDeliveryLoader.getType();
        JSONObject keyJson = jsonObject.getJSONObject(key);
        BaseLocalDelivery base = BaseLocalDelivery.forName(keyJson.getStr("driver"));
        if (base == null) throw new CommonException("同城配送驱动不存在");
        return base;
    }

}
