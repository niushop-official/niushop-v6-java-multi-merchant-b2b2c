package com.niu.core.listener.shop;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.event.AddSiteApplyEvent;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 添加店铺入驻申请后事件（对齐 PHP AfterAddSiteApplyListener）
 */
@Slf4j
@Component
public class AfterAddSiteApplyListener {

    @Resource
    private ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    private SiteApplyMapper siteApplyMapper;

    @Resource
    private ICoreConfigService coreConfigService;

    @EventListener
    public void handle(AddSiteApplyEvent event) {
        Object formData = event.getFormData();
        if (SiteApplyFormDataHelper.isEmpty(formData)) {
            return;
        }

        SiteApply siteApply = event.getSiteApply();
        try {
            Integer defaultFormId = resolveDefaultFormId();
            DiyFormRecordsParam recordsParam = SiteApplyFormDataHelper.toRecordsParam(
                    formData,
                    siteApply.getApplyId(),
                    siteApply.getMemberId(),
                    null,
                    defaultFormId,
                    RequestUtils.defaultSiteId()
            );
            if (recordsParam == null) {
                return;
            }

            Integer formRecordId = coreDiyFormRecordsService.add(recordsParam);
            if (formRecordId != null && formRecordId > 0) {
                SiteApply update = new SiteApply();
                update.setApplyId(siteApply.getApplyId());
                update.setFormRecordId(formRecordId);
                siteApplyMapper.updateById(update);
            }
        } catch (Exception e) {
            log.error("添加店铺入驻申请后事件失败：{}", e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    private Integer resolveDefaultFormId() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SHOP_APPLY");
        if (config == null) {
            return null;
        }
        String formId = config.getStr("form_id", "");
        if (formId == null || formId.isEmpty()) {
            return null;
        }
        return Integer.valueOf(formId);
    }
}
