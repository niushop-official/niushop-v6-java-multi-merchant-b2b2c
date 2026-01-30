package com.niu.mall.listener.verify;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.sys.VerifyCreateEventDefiner;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;

@EventCallback("mall")
@Component
public class VerifyCreateListener extends VerifyCreateEventDefiner {

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public VerifyCreateEventResult handleCallback(VerifyCreateEvent event) {
        // 虚拟商品核销
        if (event.getType().equals("shopVirtualGoods")) {
            Integer memberId = event.getMemberId();
            Integer orderGoodsId = Integer.valueOf((String) ObjectUtil.defaultIfNull(event.getData().get("order_goods_id"), 0));

            MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>().eq("order_goods_id", orderGoodsId).eq("member_id", memberId));
            Assert.notNull(orderGoods, "订单不存在");

            if (orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus().toString()) || orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.EXPIRE.getStatus().toString())) {
                throw new CommonException("商品已核销或已过期");
            }

            MallOrder order = mallOrderMapper.selectById(orderGoods.getOrderId());
            Assert.notNull(order, "订单不存在");

            if (order.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString()) || order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
                throw new CommonException("订单已关闭或已完成");
            }

            MallGoods goods = mallGoodsMapper.selectById(orderGoods.getGoodsId());
            Assert.notNull(goods, "商品不存在");

            if (!goods.getVirtualReceiveType().equals("verify")) throw new CommonException("当前商品不支持核销");

            Integer count = orderGoods.getNum() - orderGoods.getVerifyCount();

            JSONObject data = new JSONObject();
            JSONArray list = new JSONArray();
            list.add(new JSONObject()
                    .set("name", orderGoods.getGoodsName())
                    .set("sub_name", orderGoods.getSkuName())
                    .set("cover", orderGoods.getGoodsImage())
                    .set("num", orderGoods.getNum())
                    .set("verify_num", orderGoods.getNum())
            );
            data.put("list", list);
            data.putByPath("content.fixed", new JSONArray());
            JSONArray diy = new JSONArray();
            JSONObject diyItem = new JSONObject();
            JSONArray diyList = new JSONArray();
            diyList.add(new JSONObject().set("title", "订单编号").set("value", order.getOrderNo()));
            diyList.add(new JSONObject().set("title", "支付时间").set("value", DateUtil.formatDateTime(new Date(order.getPayTime() * 1000))));
            diyItem.set("title", "订单信息");
            diyItem.set("list", diyList);
            diy.add(diyItem);
            data.putByPath("content.diy", diy);

            VerifyCreateEventResult result = new VerifyCreateEventResult();
            result.setCount(count);
            result.setRelateTag(orderGoodsId.toString());
            result.setExpireTime(orderGoods.getVerifyExpireTime());
            result.setBody(orderGoods.getGoodsName() + orderGoods.getSkuName());
            result.setData(data.toString());

            return result;
        }
        // 自提订单核销
        else if (event.getType().equals("shopPickUpOrder")) {
            Integer memberId = event.getMemberId();
            Integer siteId = event.getSiteId();
            Integer orderId = Integer.valueOf(ObjectUtil.defaultIfNull(event.getData().get("order_id"), 0).toString());

            MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                    .eq("site_id", siteId)
                    .eq("order_id", orderId)
                    .eq("member_id", memberId));
            Assert.notNull(order, "订单不存在");

            // 非自提订单
            if (!"store".equals(order.getDeliveryType())) {
                throw new CommonException("当前订单不是自提订单");
            }

            if (OrderStatusEnum.FINISH.getStatus().equals(order.getStatus()) || OrderStatusEnum.CLOSE.getStatus().equals(order.getStatus())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 查询订单商品列表
            var orderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>()
                    .eq("site_id", siteId)
                    .eq("order_id", orderId)
                    .eq("member_id", memberId));

            Long expireTime = null;
            StringBuilder body = new StringBuilder();
            JSONArray list = new JSONArray();

            for (MallOrderGoods orderGoods : orderGoodsList) {
                // 跳过已核销或已过期的商品
                if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus())
                        || OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
                    continue;
                }

                JSONObject item = new JSONObject();
                item.set("order_goods_id", orderGoods.getOrderGoodsId());
                item.set("name", orderGoods.getGoodsName());
                item.set("sub_name", orderGoods.getSkuName());
                item.set("cover", orderGoods.getGoodsImage());
                item.set("num", orderGoods.getNum());
                item.set("verify_num", orderGoods.getNum()); // 自提订单统一进行核销
                list.add(item);

                // 计算最大过期时间
                Long tempExpireTime = orderGoods.getVerifyExpireTime();
                if (tempExpireTime != null && (expireTime == null || tempExpireTime > expireTime)) {
                    expireTime = tempExpireTime;
                }

                // 拼接body
                if (body.length() > 0) {
                    body.append(",");
                }
                body.append(orderGoods.getGoodsName()).append(orderGoods.getSkuName());
            }

            // 截取body长度
            String finalBody = body.length() > 100 ? body.substring(0, 100) : body.toString();

            // 构建返回数据
            JSONObject data = new JSONObject();
            data.put("list", list);

            // 自提人信息
            JSONObject takerInfo = new JSONObject();
            takerInfo.set("taker_name", ObjectUtil.defaultIfNull(order.getTakerName(), ""));
            takerInfo.set("taker_mobile", ObjectUtil.defaultIfNull(order.getTakerMobile(), ""));
            takerInfo.set("buyer_ask_delivery_time", ObjectUtil.defaultIfNull(order.getBuyerAskDeliveryTime(), ""));
            data.put("taker_info", takerInfo);

            // content内容
            JSONObject content = new JSONObject();
            content.put("fixed", new JSONArray());

            JSONArray diy = new JSONArray();
            JSONObject diyItem = new JSONObject();
            JSONArray diyList = new JSONArray();
            diyList.add(new JSONObject().set("title", "订单编号").set("value", order.getOrderNo()));
            diyList.add(new JSONObject().set("title", "支付时间").set("value", DateUtil.formatDateTime(new Date(order.getPayTime() * 1000))));
            diyItem.set("title", "订单信息");
            diyItem.set("list", diyList);
            diy.add(diyItem);
            content.put("diy", diy);

            data.put("content", content);

            VerifyCreateEventResult result = new VerifyCreateEventResult();
            result.setCount(1); // 自提订单核销次数为1
            result.setRelateTag(orderId.toString());
            result.setExpireTime(expireTime);
            result.setBody(finalBody);
            result.setData(data.toString());

            return result;
        }
        return null;
    }

}
