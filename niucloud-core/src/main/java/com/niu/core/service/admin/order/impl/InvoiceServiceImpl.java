package com.niu.core.service.admin.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.admin.order.IInvoiceService;
import com.niu.core.service.admin.order.param.InvoiceParam;
import com.niu.core.service.admin.order.param.InvoiceSearchParam;
import com.niu.core.service.admin.order.vo.InvoiceInfoVo;
import com.niu.core.service.admin.order.vo.InvoiceListVo;
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
    public PageResult<InvoiceListVo> getPage(PageParam pageParam, InvoiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<Invoice> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("i")
                .innerJoin("?_site s ON i.site_id = s.site_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("i.*");
        queryWrapper.select("s.site_id, s.site_name, s.front_end_logo");
        queryWrapper.eq("i.status", InvoiceStatusEnum.OPEN.getStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getHeaderType())) queryWrapper.eq("header_type", searchParam.getHeaderType());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderName())) queryWrapper.like("header_name", searchParam.getHeaderName());
        if (ObjectUtil.isNotEmpty(searchParam.getIsInvoice()) && !searchParam.getIsInvoice().equals("all")) queryWrapper.eq("is_invoice", searchParam.getIsInvoice());
        if (ObjectUtil.isNotEmpty(searchParam.getInvoiceTime())) QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", searchParam.getInvoiceTime());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getInvoiceTime());

        queryWrapper.orderByDesc("id");
        IPage<InvoiceListVo> iPage = invoiceMapper.selectJoinPage(new Page<>(page, limit), InvoiceListVo.class, queryWrapper);
        List<InvoiceListVo> list = new LinkedList<>();
        for (InvoiceListVo item : iPage.getRecords()) {
            InvoiceListVo.SiteInfo vo = new InvoiceListVo.SiteInfo();
            BeanUtils.copyProperties(item, vo);
            item.setSite(vo);
            list.add(item);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public InvoiceInfoVo getInfo(Integer id) {
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(RequestUtils.siteId() != 0){
            queryWrapper.eq("site_id", RequestUtils.siteId());
        }
        queryWrapper.last("limit 1");
        Invoice model = invoiceMapper.selectOne(queryWrapper);

        Assert.notNull(model, "数据不存在");

        InvoiceInfoVo vo = new InvoiceInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void invoicing(Integer id, InvoiceParam mallInvoiceParam) {
        Invoice model = invoiceMapper.selectOne(
                new QueryWrapper<Invoice>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", 2)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在");
        if(model.getIsInvoice() == 1) throw new AdminException("订单已开票");
        model.setIsInvoice(1);
        model.setInvoiceNumber(mallInvoiceParam.getInvoiceNumber());
        model.setInvoiceVoucher(mallInvoiceParam.getInvoiceVoucher());
        model.setRemark(mallInvoiceParam.getRemark());
        model.setInvoiceTime(System.currentTimeMillis() / 1000);
        invoiceMapper.updateById(model);

    }
}
