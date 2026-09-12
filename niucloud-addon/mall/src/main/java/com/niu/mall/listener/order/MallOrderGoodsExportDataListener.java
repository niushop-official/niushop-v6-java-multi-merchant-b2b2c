package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.account.SeparValueSourceEnum;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.mall.common.MallCircleFilter;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.admin.order.IMallOrderService;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("mall")
public class MallOrderGoodsExportDataListener extends ExportDataEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    IMemberService memberService;

    @Resource
    IMallOrderService mallOrderService;

    @Resource
    IDeliveryCompanyService deliveryCompanyService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if(!"mall_order_goods".equals(event.getType())){
            return new ExportDataEventResult();
        }
        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        JSONObject where = event.getWhere();
        if(where != null){
            queryWrapper.setAlias("t");
            queryWrapper.select("t.*");
            queryWrapper.leftJoin(" ns_pay AS p ON t.out_trade_no = p.out_trade_no ");

            Object search_name = where.get("search_name");
            if (ObjectUtil.isNotEmpty(search_name)){
                queryWrapper.and(wrapper -> {
                    wrapper.like("t.order_no", search_name)
                            .or()
                            .like("t.out_trade_no", search_name);
                });
            }

            Object order_from = where.get("order_from");
            if(ObjectUtil.isNotEmpty(order_from) && !"all".equals(order_from.toString())){
                queryWrapper.eq("t.order_from",order_from);
            }

            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "t.create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }


            if ((where.get("pay_time") instanceof JSONArray pay_time) && ObjectUtil.isNotEmpty(pay_time)) {
                QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", StringUtils.jsonArrayToStringArray(pay_time));
            }
            Object pay_type = where.get("pay_type");
            if(ObjectUtil.isNotEmpty(pay_type) && !"all".equals(pay_type.toString())){
                queryWrapper.eq("p.type",pay_type);
            }
        }
        String circleIdColumn = where != null ? "t.circle_id" : MallCircleFilter.DEFAULT_CIRCLE_ID_COLUMN;
        if (!MallCircleFilter.applyCircleIdFilter(queryWrapper, circleIdColumn)) {
            return new ExportDataEventResult();
        }
        PageParam pageParam = event.getPageParam();
        List<MallOrder> list = new ArrayList<>();
        if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
            list = mallOrderMapper.selectList(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
        }else{
            list = mallOrderMapper.selectList(queryWrapper);
        }


        ExportDataEventResult result = new ExportDataEventResult();
        JSONArray jsonArray = new JSONArray();
        for(MallOrder vo : list){

            MallOrderInfoVo mallOrderInfoVo = new MallOrderInfoVo();
            if(vo.getOrderId() != null){
                mallOrderInfoVo = mallOrderService.detail(vo.getOrderId());
            }

            List<MallOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", vo.getOrderId())), MallOrderListVo.OrderGoods.class);
            for(MallOrderListVo.OrderGoods goods : orderGoods){
                JSONObject jsonObject = new JSONObject();

                MemberInfoVo memberInfoVo = new MemberInfoVo();
                MallOrderInfoVo.OrderDelivery orderDelivery = new MallOrderInfoVo.OrderDelivery();
                DeliveryCompanyInfoVo deliveryCompanyInfoVo = new DeliveryCompanyInfoVo();
                MallOrderInfoVo.Pay pay = new MallOrderInfoVo.Pay();
                if(mallOrderInfoVo != null) {
                    if (mallOrderInfoVo.getOrderDelivery() != null) {
                        if(!mallOrderInfoVo.getOrderDelivery().isEmpty()){
                            if (mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId() != null && mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId() > 0) {
                                deliveryCompanyInfoVo = deliveryCompanyService.info(mallOrderInfoVo.getOrderDelivery().get(0).getExpressCompanyId());
                                orderDelivery = mallOrderInfoVo.getOrderDelivery().get(0);
                            }
                        }
                    }
                    if(mallOrderInfoVo.getPay() != null){
                        pay = mallOrderInfoVo.getPay();
                    }
                }
                if(vo.getMemberId() != null){
                    memberInfoVo = memberService.info(vo.getMemberId());
                }

                jsonObject.set("order_no",vo.getOrderNo())
                        .set("order_goods_id",goods.getOrderGoodsId())
                        .set("goods_name",goods.getGoodsName())
                        .set("sku_name",goods.getSkuName())
                        .set("member_no",memberInfoVo.getMemberNo())
                        .set("nickname",memberInfoVo.getNickname())
                        .set("mobile",memberInfoVo.getMobile())
                        .set("price",goods.getPrice())
                        .set("num", goods.getNum())
                        .set("goods_money", goods.getGoodsMoney())
                        .set("discount_money",goods.getDiscountMoney())
                        .set("order_goods_money",vo.getOrderMoney())
                        .set("order_from_name",PayChannelEnum.getNameByStatus(vo.getOrderFrom()))
                        .set("goods_type_name", GoodsTypeEnum.getNameByType(goods.getGoodsType()))
                        .set("taker_name",vo.getTakerName())
                        .set("taker_mobile",vo.getTakerMobile())
                        .set("taker_full_address",vo.getTakerFullAddress())
                        .set("delivery_type_name",OrderDeliveryTypeEnum.getNameByType(vo.getDeliveryType()))
                        .set("delivery_status_name", OrderDeliveryStatusEnum.getNameByStatus(goods.getDeliveryStatus()))
                        .set("delivery_money",vo.getDeliveryMoney())
                        .set("express_number",orderDelivery.getExpressNumber())
                        .set("company_name",deliveryCompanyInfoVo.getCompanyName())
                        .set("order_status_name",OrderDeliveryStatusEnum.getNameByStatus(vo.getStatus()))
                        .set("create_time",DateUtils.timestampToString(vo.getCreateTime()))
                        .set("pay_time",DateUtils.timestampToString(vo.getPayTime()))
                        .set("delivery_time",DateUtils.timestampToString(vo.getDeliveryTime()))
                        .set("finish_time",DateUtils.timestampToString(vo.getFinishTime()))
                        .set("member_remark",vo.getMemberRemark())
                        .set("shop_remark",vo.getShopRemark())
                        .set("out_trade_no",vo.getOutTradeNo())
                        .set("pay_type",SeparValueSourceEnum.getName(pay.getType()))
                        .set("order_refund_no",goods.getOrderRefundNo())
                        .set("status_name",goods.getStatusName());
                jsonArray.add(jsonObject);
            }
        }
        result.setResultData(jsonArray);
        return result;
    }
}
