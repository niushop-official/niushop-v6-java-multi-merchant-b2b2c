package com.niu.mall.listener.marketing;

import com.niu.core.enums.sys.AppTypeEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 营销活动类型监听器
 * 根据不同的应用类型返回对应的营销活动类型配置
 */
@Component
public class MarketingTypeListener {

    private final MessageSource messageSource;

    public MarketingTypeListener(@Qualifier("coreMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getLang(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 获取营销活动类型列表
     *
     * @param appType 应用类型（admin / site）
     * @return 活动类型配置列表
     */
    public List<Map<String, Object>> handle(String appType) {
        if (AppTypeEnum.ADMIN.getName().equals(appType)) {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(buildItem("discount", getLang("dict_marketing_type.discount"), 30,
                    "/src/addon/mall/views/admin/marketing/components/marketing-type-discount.vue"));
            list.add(buildItem("coupon", getLang("dict_marketing_type.coupon"), 30,
                    "/src/addon/mall/views/admin/marketing/components/marketing-type-coupon.vue"));
            list.add(buildItem("full_reduction", getLang("dict_marketing_type.full_reduction"), 30,
                    "/src/addon/mall/views/admin/marketing/components/marketing-type-full-reduction.vue"));
            list.add(buildItem("full_gift", getLang("dict_marketing_type.full_gift"), 30,
                    "/src/addon/mall/views/admin/marketing/components/marketing-type-full-gift.vue"));
            return list;
        } else if (AppTypeEnum.SITE.getName().equals(appType)) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private Map<String, Object> buildItem(String key, String name, int sort, String component) {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("name", name);
        item.put("sort", sort);
        item.put("component", component);
        return item;
    }
}
