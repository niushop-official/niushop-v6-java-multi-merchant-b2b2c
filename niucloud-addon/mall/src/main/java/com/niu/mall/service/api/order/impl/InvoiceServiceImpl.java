package com.niu.mall.service.api.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.mall.service.api.order.param.MallInvoiceSearchParam;
import com.niu.mall.service.api.order.vo.MallInvoiceInfoVo;
import com.niu.mall.service.api.order.vo.MallInvoiceListVo;
import com.niu.mall.service.api.order.IInvoiceService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

@Service
public class InvoiceServiceImpl implements IInvoiceService {


    @Resource
    InvoiceMapper invoiceMapper;

    @Override
    public PageResult<MallInvoiceListVo> getPage(PageParam pageParam, MallInvoiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.eq("status", InvoiceStatusEnum.OPEN.getStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getHeaderType())) queryWrapper.eq("header_type", searchParam.getHeaderType());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderName())) queryWrapper.like("header_name", searchParam.getHeaderName());
        if (ObjectUtil.isNotEmpty(searchParam.getIsInvoice()) && !searchParam.getIsInvoice().equals("all")) queryWrapper.eq("is_invoice", searchParam.getIsInvoice());
        if (ObjectUtil.isNotEmpty(searchParam.getInvoiceTime())) QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", searchParam.getInvoiceTime());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getInvoiceTime());

        queryWrapper.orderByDesc("id");
        IPage<Invoice> iPage = invoiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallInvoiceListVo> list = new LinkedList<>();
        for (Invoice item : iPage.getRecords()) {
            MallInvoiceListVo vo = new MallInvoiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public MallInvoiceInfoVo getInfo(Integer id) {
        Invoice model = invoiceMapper.selectOne(
                new QueryWrapper<Invoice>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallInvoiceInfoVo vo = new MallInvoiceInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }
}
