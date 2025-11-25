package com.niu.mall.listener.export;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.basic.InvoiceHeaderTypeEnum;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.enums.basic.InvoiceTypeEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EventCallback("mall")
public class MallInvoiceExportDataListener extends ExportDataEventDefiner {
    private MemberMapper memberMapper;
    private MallOrderMapper mallOrderMapper;
    private InvoiceMapper invoiceMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"mall_invoice".equals(event.getType())) {
            return new ExportDataEventResult();
        }

        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", event.getSiteId())
                .eq("status", InvoiceStatusEnum.OPEN.getStatus())
                .orderByDesc("id");

        JSONObject where = event.getWhere();
        if (where != null) {
            Object headerName = where.get("header_name");
            if (ObjectUtil.isNotEmpty(headerName)) {
                queryWrapper.like("header_name", headerName);
            }

            if ((where.get("create_time") instanceof String[] createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", createTimes);
            }

            if ((where.get("invoice_time") instanceof String[] invoiceTimes) && ObjectUtil.isNotEmpty(invoiceTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", invoiceTimes);
            }
        }

        List<Invoice> invoices;
        PageParam pageParam = event.getPageParam();
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            invoices = invoiceMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper).getRecords();
        } else {
            invoices = invoiceMapper.selectList(queryWrapper);
        }

        return exportData(invoices);
    }

    private ExportDataEventResult exportData(List<Invoice> invoices) {
        ExportDataEventResult result = new ExportDataEventResult();
        if (ObjectUtil.isEmpty(invoices)) {
            return result;
        }

        Map<Integer, Member> memberMap = Collections.emptyMap();
        Set<Integer> memberIds = CollStreamUtil.toSet(invoices, Invoice::getMemberId);
        if (ObjectUtil.isNotEmpty(memberIds)) {
            memberMap = memberMapper.selectBatchIds(memberIds).stream().collect(Collectors.toMap(Member::getMemberId, o -> o));
        }

        Map<Integer, MallOrder> orderMap = Collections.emptyMap();
        Set<Integer> orderIds = CollStreamUtil.toSet(invoices, Invoice::getTradeId);
        if (ObjectUtil.isNotEmpty(orderIds)) {
            orderMap = mallOrderMapper.selectBatchIds(orderIds).stream().collect(Collectors.toMap(MallOrder::getOrderId, o -> o));
        }

        JSONArray jsonArray = new JSONArray();
        for (Invoice invoice : invoices) {
            JSONObject jsonObject = new JSONObject();
            MallOrder order = orderMap.get(invoice.getTradeId());
            Member member = memberMap.get(invoice.getMemberId());

            jsonObject.set("order_no", Optional.ofNullable(order).map(o -> o.getOrderNo() + "\t").orElse(""))
                    .set("nickname", Optional.ofNullable(member).map(Member::getNickname).orElse(""))
                    .set("order_money", Optional.ofNullable(order).map(MallOrder::getOrderMoney).map(BigDecimal::toString).orElse(""))
                    .set("header_name", invoice.getHeaderName() + "\t")
                    .set("header_type_name", InvoiceHeaderTypeEnum.getNameByType(invoice.getHeaderType()))
                    .set("type_name", InvoiceTypeEnum.getNameByType(invoice.getType()))
                    .set("tax_number", invoice.getTaxNumber() + "\t")
                    .set("bank_name", invoice.getBankName())
                    .set("bank_card_number", invoice.getBankCardNumber() + "\t")
                    .set("address", invoice.getAddress())
                    .set("name", invoice.getName())
                    .set("money", invoice.getMoney())
                    .set("invoice_number", invoice.getInvoiceNumber() + "\t")
                    .set("remark", invoice.getRemark())
                    .set("create_time", DateUtils.timestampToString(invoice.getCreateTime()))
                    .set("invoice_time", DateUtils.timestampToString(invoice.getInvoiceTime()))
                    .set("is_invoice_name", ObjectUtil.equal(invoice.getStatus(), 1) ? "已开票" : "未开票");

            jsonArray.add(jsonObject);
        }

        result.setResultData(jsonArray);
        return result;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setShopOrderMapper(MallOrderMapper mallOrderMapper) {
        this.mallOrderMapper = mallOrderMapper;
    }

    @Autowired
    public void setShopInvoiceMapper(InvoiceMapper mallInvoiceMapper) {
        this.invoiceMapper = mallInvoiceMapper;
    }
}
