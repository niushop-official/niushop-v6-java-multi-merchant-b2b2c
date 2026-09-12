package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.basic.InvoiceTypeEnum;
import com.niu.core.enums.pay.OrderStatusEnum;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.core.service.admin.order.IInvoiceService;
import com.niu.core.service.admin.order.vo.InvoiceInfoVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.admin.order.IMallOrderService;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("shop")
public class ShopOrderExportDataListener extends ExportDataEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    IMemberService memberService;

    @Resource
    IMallOrderService mallOrderService;

    @Resource
    IDeliveryCompanyService deliveryCompanyService;

    @Resource
    IInvoiceService mallInvoiceService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"shop_order".equals(event.getType())) {
            return new ExportDataEventResult();
        }
        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        JSONObject where = event.getWhere();
        if (where != null) {
            queryWrapper.setAlias("t");
            queryWrapper.select("t.*");
            queryWrapper.leftJoin("?_pay AS p ON t.out_trade_no = p.out_trade_no ".replace("?_", GlobalConfig.tablePrefix));
            queryWrapper.leftJoin("?_member m ON t.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
            queryWrapper.eq("t.site_id", RequestUtils.siteId());
            Object search_type = where.get("search_type");
            Object search_name = where.get("search_name");
            if (ObjectUtil.isNotEmpty(search_type) && ObjectUtil.isNotEmpty(search_name)) {
                search_type = search_type.toString().trim();
                queryWrapper.like("t." + search_type, search_name);
            } else if (ObjectUtil.isNotEmpty(search_name)) {
                queryWrapper.and(wrapper -> {
                    wrapper.like("t.order_no", search_name)
                            .or()
                            .like("t.out_trade_no", search_name);
                });
            }

            Object order_from = where.get("order_from");
            if (ObjectUtil.isNotEmpty(order_from) && !"all".equals(order_from.toString())) {
                queryWrapper.eq("t.order_from", order_from);
            }

            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "t.create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }
            Object keyword = where.get("keyword");
            if (ObjectUtil.isNotEmpty(keyword)) {
                queryWrapper.and(i -> i.like("m.member_no", keyword).or()
                        .like("m.nickname", keyword).or()
                        .like("m.username", keyword).or()
                        .like("m.mobile", keyword));
            }

            if ((where.get("pay_time") instanceof JSONArray pay_time) && ObjectUtil.isNotEmpty(pay_time)) {
                QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", StringUtils.jsonArrayToStringArray(pay_time));
            }
            Object pay_type = where.get("pay_type");
            if (ObjectUtil.isNotEmpty(pay_type) && !"all".equals(pay_type.toString())) {
                queryWrapper.eq("p.type", pay_type);
            }

            Object delivery_type = where.get("delivery_type");
            if (ObjectUtil.isNotEmpty(delivery_type) && !"all".equals(delivery_type.toString())) queryWrapper.eq("t.delivery_type", delivery_type.toString());
            Object goods_source = where.get("goods_source");
            if (ObjectUtil.isNotEmpty(goods_source) && !"all".equals(goods_source.toString())) {
                if (goods_source.toString().equals("self_goods") || goods_source.toString().equals("0")) {
                    queryWrapper.eq("t.supply_order_id", 0);
                } else {
                    queryWrapper.ne("t.supply_order_id", 0);
                }
            }
        }
        PageParam pageParam = event.getPageParam();
        List<MallOrder> list = new ArrayList<>();
        queryWrapper.eq("t.delete_time", 0);
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            list = mallOrderMapper.selectList(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
        } else {
            list = mallOrderMapper.selectList(queryWrapper);
        }


        ExportDataEventResult result = new ExportDataEventResult();
        JSONArray jsonArray = new JSONArray();
        JSONObject payType = PayTypeEnum.getType();
        for (MallOrder vo : list) {
            JSONObject jsonObject = new JSONObject();

            MemberInfoVo memberInfoVo = new MemberInfoVo();
            if (vo.getMemberId() != null) {
                memberInfoVo = memberService.info(vo.getMemberId());
            }


            MallOrderInfoVo mallOrderInfoVo = new MallOrderInfoVo();
            if (vo.getOrderId() != null) {
                mallOrderInfoVo = mallOrderService.detail(vo.getOrderId());
            }

            DeliveryCompanyInfoVo deliveryCompanyInfoVo = new DeliveryCompanyInfoVo();
            MallOrderInfoVo.Pay pay = new MallOrderInfoVo.Pay();
            MallOrderInfoVo.OrderDelivery orderDelivery = new MallOrderInfoVo.OrderDelivery();
            MallOrderInfoVo.OrderGoods orderGoods = new MallOrderInfoVo.OrderGoods();
            if (mallOrderInfoVo != null) {
                if (mallOrderInfoVo.getOrderDelivery() != null) {
                    if (!mallOrderInfoVo.getOrderDelivery().isEmpty()) {
                        if (mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId() != null && mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId() > 0) {
                            deliveryCompanyInfoVo = deliveryCompanyService.info(mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId());
                            orderDelivery = mallOrderInfoVo.getOrderDelivery().get(0);
                        }
                    }
                }
                if (mallOrderInfoVo.getPay() != null) {
                    pay = mallOrderInfoVo.getPay();
                }
                if (mallOrderInfoVo.getOrderGoods() != null) {
                    if (mallOrderInfoVo.getOrderGoods().size() > 0) {
                        orderGoods = mallOrderInfoVo.getOrderGoods().get(0);
                    }
                }
            }
            InvoiceInfoVo mallInvoiceInfoVo = new InvoiceInfoVo();
            if (vo.getInvoiceId() != null) {
                if (vo.getInvoiceId() != 0) {
                    try {
                        mallInvoiceInfoVo = mallInvoiceService.getInfo(vo.getInvoiceId());
                    } catch (Exception e) {

                    }
                    if (mallInvoiceInfoVo == null) {
                        mallInvoiceInfoVo = new InvoiceInfoVo();
                    }
                }
            }


            jsonObject.set("order_no", vo.getOrderNo())
                    .set("member_no", memberInfoVo.getMemberNo())
                    .set("nickname", memberInfoVo.getNickname())
                    .set("mobile", memberInfoVo.getMobile())
                    .set("goods_name", orderGoods.getGoodsName())
                    .set("sku_name", orderGoods.getSkuName())
                    .set("num", orderGoods.getNum())
                    .set("price", orderGoods.getPrice())
                    .set("goods_money", orderGoods.getGoodsMoney())
                    .set("order_from_name", PayChannelEnum.getNameByStatus(vo.getOrderFrom()))
                    .set("goods_total_money", orderGoods.getGoodsMoney())
                    .set("delivery_money", vo.getDeliveryMoney())
                    .set("discount_money", vo.getDiscountMoney())
                    .set("order_money", vo.getOrderMoney())
                    .set("shop_money", vo.getShopMoney())
                    .set("mall_money", vo.getMallMoney())
                    .set("taker_name", vo.getTakerName())
                    .set("taker_mobile", vo.getTakerMobile())
                    .set("taker_full_address", vo.getTakerFullAddress())
                    .set("delivery_type_name", OrderDeliveryTypeEnum.getNameByType(vo.getDeliveryType()))
                    .set("status_name", OrderStatusEnum.getNameByStatus(Integer.valueOf(vo.getStatus())))
                    .set("create_time", DateUtils.timestampToString(vo.getCreateTime()))
                    .set("pay_time", DateUtils.timestampToString(vo.getPayTime()))
                    .set("delivery_time", DateUtils.timestampToString(vo.getDeliveryTime()))
                    .set("finish_time", DateUtils.timestampToString(vo.getFinishTime()))
                    .set("member_remark", vo.getMemberRemark())
                    .set("shop_remark", vo.getShopRemark())
                    .set("close_remark", vo.getCloseRemark())
                    .set("out_trade_no", vo.getOutTradeNo())
                    .set("pay_type", ObjectUtil.defaultIfNull(payType.getByPath(pay.getType() + ".name", String.class), ""))
                    .set("express_number", orderDelivery.getExpressNumber())
                    .set("company_name", deliveryCompanyInfoVo.getCompanyName())
                    .set("invoice_type", InvoiceTypeEnum.getNameByType(mallInvoiceInfoVo.getType()))
                    .set("header_name", mallInvoiceInfoVo.getHeaderName())
                    .set("tax_number", mallInvoiceInfoVo.getTaxNumber());
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
