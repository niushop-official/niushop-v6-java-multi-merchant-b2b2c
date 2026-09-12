package com.niu.core.service.admin.basic.site.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.site.Site;
import com.niu.core.event.site.SiteConfigSettingEventDefiner;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.basic.site.IConfigService;
import com.niu.core.service.admin.basic.site.param.SiteConfigParam;
import com.niu.core.service.admin.basic.site.vo.SiteConfigVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.invoice.ICoreInvoiceConfigService;
import com.niu.core.service.core.invoice.param.InvoiceConfigParam;
import com.niu.core.service.core.shop.ICoreShopMemberConfigService;
import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.niu.core.service.core.sys.impl.CoreConfigServiceImpl.WAIMAI_SITE_ORDER_CONFIG_KEY;

/**
 * 通用设置服务层
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    private static final String MALL_SITE_ORDER_CONFIG_KEY = "MALL_SITE_ORDER_CONFIG";


    @Resource
    ICoreInvoiceConfigService coreInvoiceConfigService;

    @Resource
    ICoreShopMemberConfigService coreShopMemberConfigService;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    SiteMapper siteMapper;

    @Override
    public SiteConfigVo getConfig() {
        Integer siteId = RequestUtils.siteId();
        SiteConfigVo vo = new SiteConfigVo();
        // 发票设置
        var invoiceConfig = coreInvoiceConfigService.getConfig(siteId);
        if (invoiceConfig.getInvoice() != null && StrUtil.isNotBlank(invoiceConfig.getInvoice().getIsInvoice())) {
            vo.setIsInvoice(invoiceConfig.getInvoice().getIsInvoice());
        }
        // 会员设置
        ShopMemberConfigVo memberConfig = coreShopMemberConfigService.getMemberConfig(siteId);
        if (memberConfig.getOldMemberLength() != null) {
            vo.setOldMemberLength(memberConfig.getOldMemberLength());
        }
        // 订单设置
        if (isMallInstalled()) {
            vo.setOrderFormId(resolveOrderFormId(siteId));
        }
        // 各交易类型插件配置
        mergeSiteConfigFromEvents(vo, siteId);

        // 自动接单设置（对应 PHP CoreSiteOrderConfigService::getConfig）
        if (isWaiMaiInstalled()) {
            vo.setIsAutoAccept(coreConfigService.resolveIsAutoAccept(siteId));
        }
        return vo;
    }

    @Override
    public void setConfig(SiteConfigParam param) {
        Integer siteId = RequestUtils.siteId();
        // 发票设置
        InvoiceConfigParam invoiceConfigParam = new InvoiceConfigParam();
        invoiceConfigParam.setSiteId(siteId);
        invoiceConfigParam.setIsInvoice(param.getIsInvoice());
        coreInvoiceConfigService.setConfig(invoiceConfigParam);
        // 会员设置
        ShopMemberConfigParam memberConfigParam = new ShopMemberConfigParam();
        memberConfigParam.setOldMemberLength(ObjectUtil.defaultIfNull(param.getOldMemberLength(), 90));
        coreShopMemberConfigService.setMemberConfig(siteId, memberConfigParam);
        // 订单设置
        if (isMallInstalled()) {
            JSONObject orderConfig = new JSONObject();
            orderConfig.set("form_id", ObjectUtil.defaultIfNull(param.getOrderFormId(), ""));
            coreConfigService.setConfig(siteId, MALL_SITE_ORDER_CONFIG_KEY, orderConfig);
        }
        // 各交易类型插件配置
        if (isWaiMaiInstalled()) {
            JSONObject autoAccept = new JSONObject();
            autoAccept.set("is_auto_accept", ObjectUtil.defaultIfNull(param.getIsAutoAccept(), 0));
            JSONObject orderConfig = new JSONObject();
            orderConfig.set("auto_accept", autoAccept);
            coreConfigService.setConfig(siteId, WAIMAI_SITE_ORDER_CONFIG_KEY, orderConfig);
        }
        publishSiteConfigSetting(siteId, param);
    }

    private void mergeSiteConfigFromEvents(SiteConfigVo vo, Integer siteId) {
        for (String tradeType : getSiteTradeTypes(siteId)) {
            SiteConfigSettingEventDefiner.SiteConfigSettingEvent event = new SiteConfigSettingEventDefiner.SiteConfigSettingEvent();
            event.setSiteId(siteId);
            event.setTradeType(tradeType);
            event.setOperation("get");
            List<SiteConfigSettingEventDefiner.SiteConfigSettingEventResult> results =
                    EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (results == null) {
                continue;
            }
            for (SiteConfigSettingEventDefiner.SiteConfigSettingEventResult result : results) {
                if (result == null || result.getData() == null || result.getData().isEmpty()) {
                    continue;
                }
                JSONObject data = result.getData();
                if (data.containsKey("picking_length")) {
                    vo.setPickingLength(data.getInt("picking_length", 10));
                }
            }
        }
    }

    private void publishSiteConfigSetting(Integer siteId, SiteConfigParam param) {
        JSONObject configData = new JSONObject();
        configData.set("site_id", siteId);
        configData.set("is_invoice", param.getIsInvoice());
        configData.set("old_member_length", ObjectUtil.defaultIfNull(param.getOldMemberLength(), 90));
        configData.set("order_form_id", ObjectUtil.defaultIfNull(param.getOrderFormId(), ""));
        configData.set("picking_length", ObjectUtil.defaultIfNull(param.getPickingLength(), 10));

        for (String tradeType : getSiteTradeTypes(siteId)) {
            SiteConfigSettingEventDefiner.SiteConfigSettingEvent event = new SiteConfigSettingEventDefiner.SiteConfigSettingEvent();
            event.setSiteId(siteId);
            event.setTradeType(tradeType);
            event.setOperation("set");
            event.setConfigData(configData);
            EventAndSubscribeOfPublisher.publishAndCallback(event);
        }
    }

    private List<String> getSiteTradeTypes(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        if (site == null || StrUtil.isBlank(site.getApp())) {
            return Collections.emptyList();
        }
        JSONArray appArray = JSONUtil.parseArray(site.getApp());
        List<String> tradeTypes = new ArrayList<>();
        for (int i = 0; i < appArray.size(); i++) {
            String tradeType = appArray.getStr(i);
            if (StrUtil.isNotBlank(tradeType)) {
                tradeTypes.add(tradeType);
            }
        }
        return tradeTypes;

        // 自动接单设置
    }

    private boolean isMallInstalled() {
        // 等价 PHP check_addon('mall')
        var installAddonList = coreAddonService.getInstallAddonList();
        return installAddonList != null && installAddonList.containsKey("mall");
    }

    private boolean isWaiMaiInstalled() {
        var installAddonList = coreAddonService.getInstallAddonList();
        return installAddonList != null && installAddonList.containsKey("waimai");
    }

    /**
     * 解析订单万能表单ID，表单不存在时返回空
     */
    private Object resolveOrderFormId(Integer siteId) {
        JSONObject orderConfig = coreConfigService.getConfigValue(siteId, MALL_SITE_ORDER_CONFIG_KEY);
        if (orderConfig == null || orderConfig.isEmpty()) {
            return 0;
        }
        Object formId = orderConfig.get("form_id");
        if (ObjectUtil.isEmpty(formId)) {
            formId = orderConfig.get("formId");
        }
        if (ObjectUtil.isEmpty(formId)) {
            return 0;
        }
        Long count = diyFormMapper.selectCount(new QueryWrapper<DiyForm>()
                .eq("site_id", siteId)
                .eq("form_id", formId));
        if (count == null || count == 0) {
            return "";
        }
        return formId;
    }
}
