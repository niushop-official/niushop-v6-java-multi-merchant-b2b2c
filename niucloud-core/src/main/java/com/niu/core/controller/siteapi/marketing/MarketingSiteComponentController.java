package com.niu.core.controller.siteapi.marketing;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.components.ComponentsEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 站点营销公共组件
 * 路径对齐 PHP：adminapi/marketing/site
 * 组件路径按站点已装插件合并（对齐 ComponentsDict + GoodsSelectComponentsListener）
 */
@RestController
@RequestMapping("adminapi/marketing/site")
public class MarketingSiteComponentController {

    /**
     * 优惠券商品选择组件
     * GET marketing/site/goods_select/component
     */
    @GetMapping("/goods_select/component")
    public Result<Map<String, String>> getCouponGoodsSelectComponent() {
        return Result.success(resolveComponent("goods_select"));
    }

    private Map<String, String> resolveComponent(String type) {
        Map<String, String> result = new LinkedHashMap<>();
        Object item = ComponentsEnum.getComponents(type);
        if (item instanceof JSONObject json && !json.isEmpty()) {
            String component = json.getStr("component", "");
            if (StrUtil.isNotBlank(component)) {
                result.put("component", component);
                return result;
            }
        }
        // 兜底 mall（兼容未配置 loader 的环境）
        result.put("component", "/src/addon/mall/views/site/goods/components/goods-select-popup.vue");
        return result;
    }
}
