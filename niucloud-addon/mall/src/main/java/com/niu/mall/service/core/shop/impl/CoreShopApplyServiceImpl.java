package com.niu.mall.service.core.shop.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.service.core.shop.ICoreShopApplyService;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreShopApplyServiceImpl implements ICoreShopApplyService {

    private static final String CONFIG_KEY = "SHOP_APPLY";

    @Autowired
    private ICoreConfigService coreConfigService;

    @Autowired
    private DiyFormMapper diyFormMapper;

    /**
     * 获取平台默认站点ID（通常为0）
     */
    private Integer getDefaultSiteId() {
        return RequestUtils.defaultSiteId();
    }

    @Override
    public ShopApplyConfigVo getShopApplyConfig() {
        Integer siteId = getDefaultSiteId();
        // 获取配置值
        JSONObject config = coreConfigService.getConfigValue(siteId, CONFIG_KEY);
        ShopApplyConfigVo vo = new ShopApplyConfigVo();
        if (MapUtil.isNotEmpty(config)) {
            vo.setApplyTitle(config.getStr("apply_title", config.getStr("applyTitle", "")));
            vo.setApplyBanner(config.getStr("apply_banner", config.getStr("applyBanner", "addon/mall/member/merchant_bg.png")));
            Object formIdValue = config.containsKey("form_id") ? config.get("form_id") : config.get("formId");
            Integer formId = parseInteger(formIdValue);
            if (formId != null && formId > 0) {
                // 检查表单是否存在
                Long count = diyFormMapper.selectCount(new LambdaQueryWrapper<DiyForm>()
                        .eq(DiyForm::getSiteId, siteId)
                        .eq(DiyForm::getFormId, formId));
                if (count > 0) {
                    vo.setFormId(formId);
                } else {
                    vo.setFormId(null);
                }
            } else {
                vo.setFormId(null);
            }
        } else {
            // 默认值
            vo.setApplyTitle("");
            vo.setApplyBanner("addon/mall/member/merchant_bg.png");
            vo.setFormId(null);
        }
        return vo;
    }

    @Override
    public void setShopApplyConfig(ShopApplyConfigVo data) {
        Integer siteId = getDefaultSiteId();
        JSONObject config = JSONUtil.createObj()
                .set("apply_title", ObjectUtil.defaultIfNull(data.getApplyTitle(), ""))
                .set("apply_banner", ObjectUtil.defaultIfNull(data.getApplyBanner(), ""))
                .set("form_id", data.getFormId());
        coreConfigService.setConfig(siteId, CONFIG_KEY, config);
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
