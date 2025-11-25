package com.niu.core.service.admin.shop.impl;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.shop.ISitePaymentService;
import com.niu.core.service.core.site_payment.ICoreSitePaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SiteShopPaymentServiceImpl implements ISitePaymentService {

    @Resource
    private ICoreSitePaymentService coreSitePaymentService;


    @Override
    public JSONArray getList() {
        Integer siteId = RequestUtils.siteId();
        return  coreSitePaymentService.getSpearPaymentType(siteId);
    }
}
