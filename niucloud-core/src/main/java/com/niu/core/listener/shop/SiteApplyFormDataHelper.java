package com.niu.core.listener.shop;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 入驻申请万能表单数据转换（对齐 PHP CoreDiyFormRecordsService 入参）
 */
public final class SiteApplyFormDataHelper {

    private SiteApplyFormDataHelper() {
    }

    public static boolean isEmpty(Object formData) {
        if (formData == null) {
            return true;
        }
        if (formData instanceof Map<?, ?> map && map.isEmpty()) {
            return true;
        }
        if (formData instanceof JSONArray array && array.isEmpty()) {
            return true;
        }
        return false;
    }

    public static DiyFormRecordsParam toRecordsParam(Object formData, Integer applyId, Integer memberId,
                                                     Integer formRecordId, Integer defaultFormId, Integer siteId) {
        if (isEmpty(formData)) {
            return null;
        }

        DiyFormRecordsParam param = new DiyFormRecordsParam();
        param.setSiteId(siteId);
        param.setMemberId(memberId);
        param.setRelateId(applyId);

        Integer formId = defaultFormId;
        Object valueSource = formData;

        if (formData instanceof Map<?, ?> map) {
            Object mapFormId = map.containsKey("form_id") ? map.get("form_id") : map.get("formId");
            if (mapFormId != null) {
                formId = Integer.valueOf(String.valueOf(mapFormId));
            }
            Object mapRecordId = map.containsKey("record_id") ? map.get("record_id") : map.get("recordId");
            if (mapRecordId != null) {
                param.setRecordId(Integer.valueOf(String.valueOf(mapRecordId)));
            }
            if (map.containsKey("value")) {
                valueSource = map.get("value");
            }
        }

        if (formRecordId != null && formRecordId > 0) {
            param.setRecordId(formRecordId);
        }
        if (formId == null || formId <= 0) {
            return null;
        }

        param.setFormId(formId);
        JSONArray value = toJSONArray(valueSource);
        if (value == null || value.isEmpty()) {
            return null;
        }
        param.setValue(value);
        return param;
    }

    private static JSONArray toJSONArray(Object valueSource) {
        if (valueSource == null) {
            return null;
        }
        if (valueSource instanceof JSONArray jsonArray) {
            return jsonArray;
        }
        if (valueSource instanceof String str) {
            return JSONUtil.parseArray(str);
        }
        if (valueSource instanceof Map<?, ?> map) {
            List<Object> list = new ArrayList<>();
            int index = 0;
            while (map.containsKey(String.valueOf(index))) {
                list.add(map.get(String.valueOf(index)));
                index++;
            }
            if (!list.isEmpty()) {
                return new JSONArray(list);
            }
            return JSONUtil.parseArray(JSONUtil.toJsonStr(valueSource));
        }
        return JSONUtil.parseArray(valueSource);
    }
}
