package com.niu.core.service.admin.shop.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.ISitePaymentApplyService;
import com.niu.core.service.core.site_payment.ICoreSitePaymenApplyService;
import com.niu.core.service.core.site_payment.param.SitePaymentApplyParam;
import com.niu.core.service.core.site_payment.vo.SitePaymentApplyVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SitePaymentApplyServiceImpl implements ISitePaymentApplyService {
    @Resource
    private ICoreSitePaymenApplyService coreSitePaymenApplyService;

    @Override
    public PageResult<SitePaymentApplyVo> getPage(PageParam pageParam, SitePaymentApplyParam param) {
        // 调用核心服务获取原始分页数据（假设返回 IPage<SitePaymentApplyVo>）
        PageResult<SitePaymentApplyVo> originalPage = coreSitePaymenApplyService.getPage(pageParam, param);

        // 转换结果：添加 site_name 和 user_type，移除 site 和 infos
        for (SitePaymentApplyVo info : originalPage.getData()) {
            info.setSiteName(info.getSite() != null ? info.getSite().getSiteName() : "");

            // 提取 infos 对象并获取 type，转换为 user_type
            Map<String, Object> infos = JSON.parseObject(info.getInfos().toString(), new TypeReference<>() {
            });
            if (infos.containsKey("type")) {
                Integer type = (Integer) infos.get("type");
                String userType = type == 1 ? "企业" : "个人";
                info.setUserType(userType);
            } else {
                info.setUserType("");
            }

            // 移除原 site 和 infos 字段
            info.setSite(null);
            info.setInfos(null);
        }
        return originalPage;
    }

    @Override
    public SitePaymentApplyVo getDetail(Integer id) {
        // 直接调用核心服务的详情方法
        SitePaymentApplyParam param = new SitePaymentApplyParam();
        param.setId(id);
        return coreSitePaymenApplyService.getDetail(param);
    }
}
