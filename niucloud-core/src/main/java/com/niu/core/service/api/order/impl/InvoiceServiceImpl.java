package com.niu.core.service.api.order.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceContentEnum;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.enums.basic.InvoicingEnum;
import com.niu.core.event.member.MemberAccountEvent;
import com.niu.core.event.order.AfterInvoiceApplyEvent;
import com.niu.core.event.order.GetInvoiceMoneyByTradeEventDefiner;
import com.niu.core.event.order.OrderInvoiceEventDefiner;
import com.niu.core.event.order.OrderPageEventDefiner;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.admin.order.param.MallOrderSearchParam;
import com.niu.core.service.api.order.IInvoiceService;
import com.niu.core.service.api.order.param.InvoiceApplyParam;
import com.niu.core.service.api.order.param.InvoiceSearchParam;
import com.niu.core.service.api.order.vo.InvoiceConfigVo;
import com.niu.core.service.api.order.vo.InvoiceInfoVo;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class InvoiceServiceImpl implements IInvoiceService {

    @Resource
    private InvoiceMapper invoiceMapper;

    @Resource
    private ICoreConfigService coreConfigService;

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
        queryWrapper.eq("i.member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderType())) queryWrapper.eq("header_type", searchParam.getHeaderType());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderName())) queryWrapper.like("header_name", searchParam.getHeaderName());
        if (ObjectUtil.isNotEmpty(searchParam.getIsInvoice()) && !searchParam.getIsInvoice().equals("all")) {
            queryWrapper.eq("is_invoice", searchParam.getIsInvoice());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getInvoiceTime())) QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", searchParam.getInvoiceTime());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getInvoiceTime());

        queryWrapper.orderByDesc("id");
        IPage<InvoiceListVo> iPage = invoiceMapper.selectJoinPage(new Page<>(page, limit), InvoiceListVo.class, queryWrapper);
        List<InvoiceListVo> list = new LinkedList<>();
        for (InvoiceListVo item : iPage.getRecords()) {
            OrderInvoiceEventDefiner.OrderInvoiceEvent event = new OrderInvoiceEventDefiner.OrderInvoiceEvent();
            event.setTradeId(item.getTradeId());
            event.setTradeType(item.getTradeType());
            List<OrderInvoiceEventDefiner.OrderInvoiceResult> eventResults = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (!eventResults.isEmpty()  ){
                for (OrderInvoiceEventDefiner.OrderInvoiceResult eventResult : eventResults){
                    if (ObjectUtil.isNotEmpty(eventResult.getOrderList())){
                        item.setOrderData(eventResult.getOrderList());
                    }
                }
            }
            list.add( item);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public InvoiceInfoVo getInfo(Integer id) {
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.eq("status", InvoiceStatusEnum.OPEN.getStatus());
        Invoice info = invoiceMapper.selectOne(queryWrapper);
        InvoiceInfoVo model = new InvoiceInfoVo();
        if(ObjectUtil.isNotEmpty(info)){
            BeanUtils.copyProperties(info, model);
            OrderInvoiceEventDefiner.OrderInvoiceEvent event = new OrderInvoiceEventDefiner.OrderInvoiceEvent();
            event.setTradeId(info.getTradeId());
            event.setTradeType(info.getTradeType());
            List<OrderInvoiceEventDefiner.OrderInvoiceResult> eventResults = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (!eventResults.isEmpty()  ){
                for (OrderInvoiceEventDefiner.OrderInvoiceResult eventResult : eventResults){
                    if (ObjectUtil.isNotEmpty(eventResult.getOrderList())){
                        model.setOrderData(eventResult.getOrderList());
                    }
                }
            }
        }
        return model;
    }

    @Override
    public InvoiceConfigVo getInvoiceConfig() {
        InvoiceConfigVo invoiceInfoVo = new InvoiceConfigVo();
        JSONObject invoiceConfig = coreConfigService.getConfigValue(0, "MALL_INVOICE");
        if (ObjectUtil.isEmpty(invoiceConfig)){
            invoiceInfoVo.setInvoiceContent("");
            invoiceInfoVo.setInvoiceType( "");
            invoiceInfoVo.setIsInvoice("2");
        }else{
            invoiceInfoVo.setInvoiceContent(ObjectUtil.isEmpty(invoiceConfig.getStr("invoice_content"))?"": invoiceConfig.getStr("invoice_content"));
            invoiceInfoVo.setInvoiceType(ObjectUtil.isEmpty(invoiceConfig.getStr("invoice_type"))?"": invoiceConfig.getStr("invoice_type"));
            invoiceInfoVo.setIsInvoice(ObjectUtil.isEmpty(invoiceConfig.getStr("is_invoice"))?"1": invoiceConfig.getStr("is_invoice"));
        }
        return null;
    }

    @Override
    @Transactional(  rollbackFor = Exception.class)
    public void apply(InvoiceApplyParam param) {
        Integer memberId =  RequestUtils.memberId();

        //查询发票金额
        GetInvoiceMoneyByTradeEventDefiner.GetInvoiceMoneyByTradeEvent event = new GetInvoiceMoneyByTradeEventDefiner.GetInvoiceMoneyByTradeEvent();
        event.setMemberId(memberId);
        event.setTradeId(param.getTradeId());
        event.setTradeType(param.getTradeType());
        List<GetInvoiceMoneyByTradeEventDefiner.GetInvoiceMoneyByTradeResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        BigDecimal invoiceMoney = BigDecimal.ZERO;
        Integer siteId =  RequestUtils.siteId();
        if(!resultList.isEmpty()){
            for (GetInvoiceMoneyByTradeEventDefiner.GetInvoiceMoneyByTradeResult item : resultList){
                if(item == null) continue;
                if(ObjectUtil.isNotEmpty(item.getInvoiceMoney())){
                    invoiceMoney =  item.getInvoiceMoney();
                    siteId =  item.getSiteId();
                }
            }
        }
        if ( invoiceMoney.compareTo(BigDecimal.ZERO) <= 0){
             throw new CommonException("开票申请失败");
        }

        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trade_id", param.getTradeId());
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("trade_type",param.getTradeType());
        Long count = invoiceMapper.selectCount(queryWrapper);
        if(count>0){
            throw new CommonException("该订单已申请开具发票");
        }


        Invoice model = new Invoice();
        BeanUtil.copyProperties(param, model);
        model.setSiteId(siteId);
        model.setMemberId(memberId);;
        model.setMoney(invoiceMoney);
        model.setStatus(InvoiceStatusEnum.OPEN.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setIsInvoice(InvoicingEnum.WAIT_INVOICE.getStatus());
        model.setName(InvoiceContentEnum.getNameByType(param.getContentType()));
        invoiceMapper.insert(model);

        // 开票后的事件
        AfterInvoiceApplyEvent invoiceApplyEvent = new AfterInvoiceApplyEvent();
        invoiceApplyEvent.setTradeId(param.getTradeId());
        invoiceApplyEvent.setTradeType(param.getTradeType());
        invoiceApplyEvent.setInvoiceId(model.getId());
        EventAndSubscribeOfPublisher.publishAll(invoiceApplyEvent);
    }
}
