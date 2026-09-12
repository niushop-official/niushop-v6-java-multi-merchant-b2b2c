package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 店铺订单设置服务层
 */
@Service
public class CoreSiteOrderConfigServiceImpl implements ICoreSiteOrderConfigService {

    private static final String MALL_SITE_ORDER_CONFIG_KEY = "MALL_SITE_ORDER_CONFIG";

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Override
    public void setConfig(SiteOrderConfigParam orderConfigParam) {
        coreConfigService.setConfig(orderConfigParam.getSiteId(), MALL_SITE_ORDER_CONFIG_KEY, JSONUtil.parseObj(orderConfigParam));
    }

    @Override
    public SiteOrderConfigVo getConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, MALL_SITE_ORDER_CONFIG_KEY);
        SiteOrderConfigVo siteOrderConfigVo = new SiteOrderConfigVo();
        if (ObjectUtil.isEmpty(coreSysConfigVo)) {
            return siteOrderConfigVo;
        }
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            siteOrderConfigVo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), SiteOrderConfigVo.class);
            siteOrderConfigVo.setFormId(resolveFormId(siteId, coreSysConfigVo.getValueJson(), siteOrderConfigVo.getFormId()));
        }
        return siteOrderConfigVo;
    }

    /**
     * 解析万能表单ID，不存在时返回null
     */
    private Integer resolveFormId(Integer siteId, JSONObject valueJson, Integer formId) {
        if (formId != null) {
            return validateFormId(siteId, formId) ? formId : null;
        }
        Object rawFormId = valueJson.get("form_id");
        if (ObjectUtil.isEmpty(rawFormId)) {
            return null;
        }
        try {
            Integer parsedFormId = Integer.valueOf(String.valueOf(rawFormId));
            return validateFormId(siteId, parsedFormId) ? parsedFormId : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 校验万能表单是否存在
     */
    private boolean validateFormId(Integer siteId, Integer formId) {
        if (formId == null || formId <= 0) {
            return false;
        }
        Long count = diyFormMapper.selectCount(new QueryWrapper<DiyForm>()
                .eq("site_id", siteId)
                .eq("form_id", formId));
        return count != null && count > 0;
    }
}
