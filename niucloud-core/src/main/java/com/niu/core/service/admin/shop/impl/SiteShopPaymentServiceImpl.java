package com.niu.core.service.admin.shop.impl;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.shop.ISitePaymentService;
import com.niu.core.service.admin.shop.param.SitePaymentAEParam;
import com.niu.core.service.admin.shop.param.SitePaymentSearchParam;
import com.niu.core.service.core.site_payment.ICoreSitePaymentService;
import com.niu.core.service.core.site_payment.vo.SitePaymentVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SiteShopPaymentServiceImpl implements ISitePaymentService {

    @Resource
    private ICoreSitePaymentService coreSitePaymentService;


    @Override
    public JSONArray getList() {
        Integer siteId = RequestUtils.siteId();
        return coreSitePaymentService.getSpearPaymentType(siteId);
    }

    @Override
    public PageResult<SitePaymentVo> getPage(PageParam pageParam, SitePaymentSearchParam param, LocalDateTime[] allParam) {
        if (allParam != null && allParam.length == 2) {
            List<Long> createTime = new ArrayList<>();
            createTime.add(allParam[0].atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            createTime.add(allParam[1].atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            param.setCreateTime(createTime);
        }
        PageResult<SitePaymentVo> originalPage = coreSitePaymentService.getPage(pageParam, param);
        for (SitePaymentVo info : originalPage.getData()) {
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
//            info.setInfos(new ArrayList<>());
        }
        return originalPage;
    }

    @Override
    public SitePaymentVo getDetail(Integer id) {
        return coreSitePaymentService.getDetail(id);
    }

    @Override
    public Map<String, Object> addByHandle(SitePaymentAEParam param) {
        return coreSitePaymentService.addByHandle(param);
    }

    @Override
    public Map<String, Object> editByHandle(SitePaymentAEParam param) {
        return coreSitePaymentService.editByHandle(param);
    }
}
