package com.niu.core.service.core.electronic_sheet.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.delivery.DeliveryElectronicSheet;
import com.niu.core.mapper.delivery.DeliveryElectronicSheetMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.core.electronic_sheet.ICoreElectronicSheetService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoreElectronicSheetServiceImpl implements ICoreElectronicSheetService {

    @Resource
    private DeliveryCompanyMapper deliveryCompanyMapper;
    @Resource
    private DeliveryElectronicSheetMapper deliveryElectronicSheetMapper;

    @Override
    public void initDefaultElectronicSheet(Integer siteId) {
        List<DeliveryCompany> list = deliveryCompanyMapper.selectList(new QueryWrapper<DeliveryCompany>()
                .eq("site_id",0).in("express_no_electronic_sheet","EMS","YZPY","SF").eq("electronic_sheet_switch",1));
        if(!list.isEmpty()){
            List<DeliveryElectronicSheet> sheetList = new ArrayList<>();
            Integer index = 0;
            for(DeliveryCompany company : list){
                JSONArray printStyleJson = StringUtils.isNotEmpty(company.getPrintStyle()) ? JSONUtil.parseArray(company.getPrintStyle()) : new JSONArray();
                JSONArray expTypeJson = StringUtils.isNotEmpty(company.getExpType()) ? JSONUtil.parseArray(company.getExpType()) : new JSONArray();
                DeliveryElectronicSheet entity = new DeliveryElectronicSheet();
                entity.setSiteId(siteId);
                entity.setTemplateName(company.getCompanyName()+" "+ (!printStyleJson.isEmpty() ? printStyleJson.getJSONObject(0).getStr("template_name") : ""));
                entity.setExpressCompanyId(company.getCompanyId());
                entity.setCustomerName("电子面单账号");
                entity.setCustomerPwd("电子面单密码");
                entity.setPayType(1);
                entity.setIsNotice(0);
                entity.setStatus(1);
                entity.setExpType(!expTypeJson.isEmpty() ? expTypeJson.getJSONObject(0).getInt("value") : null);
                entity.setPrintStyle(!printStyleJson.isEmpty() ? printStyleJson.getJSONObject(0).getStr("template_size") : "");
                entity.setIsDefault(index == 0 ? 1 : 0);
                entity.setCreateTime(System.currentTimeMillis()/1000);
                index++;
                sheetList.add(entity);
            }
            deliveryElectronicSheetMapper.insert(sheetList);
        }
    }
}