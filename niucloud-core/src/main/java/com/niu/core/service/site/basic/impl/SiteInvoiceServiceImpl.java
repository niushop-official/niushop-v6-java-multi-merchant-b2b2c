package com.niu.core.service.site.basic.impl;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceHeaderTypeEnum;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.enums.basic.InvoiceTypeEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.site.basic.ISiteInvoiceService;
import com.niu.core.service.site.basic.param.SiteInvoiceParam;
import com.niu.core.service.site.basic.param.SiteInvoiceSubmitParam;
import com.niu.core.service.site.basic.param.SiteInvoiceConfigParam;
import com.niu.core.service.site.basic.vo.SiteInvoiceConfigVo;
import com.niu.core.service.site.basic.vo.SiteInvoiceListVo;
import com.niu.core.service.core.invoice.ICoreInvoiceConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 发票实现
 */
@Service
public class SiteInvoiceServiceImpl implements ISiteInvoiceService {
        
    @Resource
    InvoiceMapper siteInvoiceMapper;
    
    @Resource
    ICoreInvoiceConfigService coreInvoiceConfigService;


    @Override
    public PageResult<SiteInvoiceListVo> getPage(PageParam pageParam, SiteInvoiceParam param) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("status", InvoiceStatusEnum.OPEN.getStatus());
        queryWrapper.select("*");
        if (ObjectUtil.isNotEmpty(param.getIsInvoice())){
            queryWrapper.eq("is_invoice", param.getIsInvoice());
        }
        if (ObjectUtil.isNotEmpty(param.getHeaderType())){
            queryWrapper.eq("header_type", param.getHeaderType());
        }
        if (ObjectUtil.isNotEmpty(param.getHeaderName())){
            queryWrapper.like("header_name", param.getHeaderName());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())){
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(param.getInvoiceTime())){
            QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", param.getInvoiceTime());
        }
        queryWrapper.orderByDesc("id");
        Page<Invoice> iPage = siteInvoiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteInvoiceListVo> list = new LinkedList<>();
        for (Invoice record : iPage.getRecords()) {
            SiteInvoiceListVo vo = new SiteInvoiceListVo();
            BeanUtils.copyProperties(record, vo);
            
            // 设置抬头类型名称
            if (record.getHeaderType() != null) {
                vo.setHeaderTypeName(InvoiceHeaderTypeEnum.getNameByType(record.getHeaderType()));
            }
            // 设置发票类型名称
            if (record.getType() != null) {
                vo.setTypeName(InvoiceTypeEnum.getNameByType(record.getType()));
            }
            
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SiteInvoiceListVo getInfo(Integer id) {
     Invoice model =   siteInvoiceMapper.selectOne(
                new QueryWrapper<Invoice>().eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", InvoiceStatusEnum.OPEN.getStatus())
                        .last("limit 1"));
                Assert.notNull(model, "数据不存在");
        SiteInvoiceListVo vo = new SiteInvoiceListVo();
        vo.setHeaderName(InvoiceHeaderTypeEnum.getNameByType(model.getHeaderType()));
        vo.setTypeName(InvoiceStatusEnum.getNameByStatus(model.getType()));
        BeanUtils.copyProperties(model, vo);
        return vo;

    }

    @Override
    public void invoicing(Integer id, SiteInvoiceSubmitParam param) {
     Invoice model  = siteInvoiceMapper.selectOne(
                new QueryWrapper<Invoice>().eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", InvoiceStatusEnum.OPEN.getStatus())
                        .last("limit 1"));
                Assert.notNull(model, "数据不存在");
                if (model.getIsInvoice() == InvoiceStatusEnum.WAIT_OPEN.getStatus()){
                    throw  new RuntimeException("订单已开票，请勿重复开票");
                }
                model.setIsInvoice(InvoiceStatusEnum.WAIT_OPEN.getStatus());
                model.setInvoiceNumber(param.getInvoiceNumber());
                model.setInvoiceVoucher(param.getInvoiceVoucher());
                model.setRemark(param.getRemark());
                model.setInvoiceTime(System.currentTimeMillis() / 1000);
                siteInvoiceMapper.updateById(model);

    }

    @Override
    public SiteInvoiceConfigVo getConfig() {
        return coreInvoiceConfigService.getSiteInvoiceConfig(RequestUtils.siteId());
    }

    @Override
    public boolean setConfig(SiteInvoiceConfigParam param) {
        coreInvoiceConfigService.setSiteInvoiceConfig(RequestUtils.siteId(), param);
        return true;
    }
}
