package com.niu.core.service.core.site_payment.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import com.niu.core.enums.sys.SeparTypeEnum;
import com.niu.core.mapper.site.SitePaymentApplyMapper;
import com.niu.core.mapper.site.SitePaymentMapper;
import com.niu.core.service.core.site_payment.ICoreSitePaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoreSitePaymentServiceImpl implements ICoreSitePaymentService {

    @Resource
    private SitePaymentMapper sitePaymentMapper;
    @Resource
    private SitePaymentApplyMapper sitePaymentApplyMapper;

    @Override
    public JSONArray getSpearPaymentType(Integer siteId) {
        return SeparTypeEnum.getTypeBySiteId(siteId);
    }

    @Override
    public JSONArray getSiteSpearPaymentTypeListByType(Integer siteId) {
        JSONArray paymentTypes = getSpearPaymentType(siteId);
        if (!paymentTypes.isEmpty()){
            for (int i = 0; i < paymentTypes.size(); i++){
                JSONObject jsonObject = paymentTypes.getJSONObject(i);
                SitePayment sitePayment = sitePaymentMapper.selectOne(new QueryWrapper<SitePayment>().eq("site_id", siteId).eq("type", jsonObject.getStr("key")).last("limit 1"));
                SitePaymentApply sitePaymentApply = sitePaymentApplyMapper.selectOne(new QueryWrapper<SitePaymentApply>().eq("site_id", siteId).eq("type", jsonObject.getStr("key")).last("limit 1"));
                sitePayment = sitePayment == null ? new SitePayment() : sitePayment;
                sitePaymentApply = sitePaymentApply == null ? new SitePaymentApply() : sitePaymentApply;
                paymentTypes.getJSONObject(i).set("site_payment", JSONUtil.parseObj(sitePayment).toString());
                paymentTypes.getJSONObject(i).set("site_payment_apply", JSONUtil.parseObj(sitePaymentApply).toString());
            }
        }
        return paymentTypes;
    }

    @Override
    public Map<String, Object> getInfo(Integer siteId, String type) {
        if (type == null || type.isEmpty()) {
            type = "huifu";
        }
        
        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId)
                .eq("type", type)
                .last("limit 1");
        
        SitePayment sitePayment = sitePaymentMapper.selectOne(queryWrapper);
        
        if (sitePayment == null) {
            return new HashMap<>();
        }
        
        return BeanUtil.beanToMap(sitePayment);
    }

    @Override
    public Long add(Map<String, Object> data, Integer siteId, String type) {
        // 设置 site_id 和 type
        data.put("siteId", siteId);
        data.put("type", type);

        // 将 Map 转换为实体对象
        SitePayment entity = BeanUtil.mapToBean(data, SitePayment.class, false);
        
        // 插入数据
        sitePaymentMapper.insert(entity);
        
        // 返回插入后的 ID
        return entity.getId();
    }

    @Override
    public Boolean edit(Long id, Integer siteId, Map<String, Object> data) {
        // 构建更新条件
        QueryWrapper<SitePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("site_id", siteId);

        // 将 Map 转换为实体对象
        SitePayment entity = BeanUtil.mapToBean(data, SitePayment.class, false);
        
        int result = sitePaymentMapper.update(entity, queryWrapper);
        
        return result > 0;
    }
}
