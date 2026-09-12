package com.niu.core.service.core.vip_card.card.benefits;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class BenefitsDictLoader {
    @Autowired
    private BenefitsHandlerFactory handlerFactory;

    private final Map<String, BenefitsConfig> dict = new LinkedHashMap<>();

    public void load() {
        // 扫描路径下的所有 .json 文件
        for (Object res : JsonModuleLoader.build().mergeResultSet("vip_card/vip_card_benefits.json")) {
            JSONObject result = JSONUtil.parseObj(res);
            BenefitsConfig benefitsConfig = BeanUtil.toBean(result, BenefitsConfig.class);
            for (Map.Entry<String, String> entry : benefitsConfig.getContentStr().entrySet()) {
                BenefitsContentHandler handler = handlerFactory.getHandler(entry.getValue());
                benefitsConfig.setContent(Map.of(entry.getKey(), handler));
            }
            dict.put(benefitsConfig.getType(), benefitsConfig);
        }
    }

    public Map<String, BenefitsConfig> getDict(boolean force) {
        if (force) load();
        if (dict.isEmpty()) load();
        return dict;
    }

    public Map<String, BenefitsConfig> getDict() {
        return getDict(false);
    }

    public BenefitsConfig getConfig(String type, boolean force) {
        if (force) load();
        if (dict.isEmpty()) load();
        return dict.get(type);
    }

    public BenefitsConfig getConfig(String type) {
        return getConfig(type, false);
    }
}