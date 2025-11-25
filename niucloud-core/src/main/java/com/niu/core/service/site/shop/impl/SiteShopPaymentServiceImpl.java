package com.niu.core.service.site.shop.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import com.niu.core.mapper.site.SitePaymentApplyMapper;
import com.niu.core.mapper.site.SitePaymentMapper;
import com.niu.core.service.site.shop.ISiteShopPaymentService;
import com.niu.core.service.core.site_payment.ICoreSitePaymentService;
import com.niu.core.service.site.shop.param.SiteShopPaymentParam;
import com.niu.core.service.site.shop.vo.SiteShopPaymentTypeVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteShopPaymentServiceImpl implements ISiteShopPaymentService {

    @Resource
    private ICoreSitePaymentService coreSitePaymentService;

    @Resource
    private SitePaymentMapper sitePaymentMapper;

    @Resource
    private ICoreSitePaymentService ICoreSitePaymentService;


    @Override
    public JSONArray getList() {
        Integer siteId = RequestUtils.siteId();
        return  coreSitePaymentService.getSiteSpearPaymentTypeListByType(siteId);
    }

    @Override
    public Boolean editStatus(Integer id, SiteShopPaymentParam sitePaymentParam) {
        Integer siteId = RequestUtils.siteId();

        // 构建查询条件
        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("site_id", siteId);

        // 查找支付方式记录
        SitePayment sitePayment = sitePaymentMapper.selectOne(queryWrapper);

        // 如果记录存在，更新状态
        if (sitePayment != null) {
            sitePayment.setStatus(sitePaymentParam.getStatus());
            sitePaymentMapper.updateById(sitePayment);
        }
        return true;
    }
}
