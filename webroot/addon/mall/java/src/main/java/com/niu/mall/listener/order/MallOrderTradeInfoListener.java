package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@EventCallback("mall")
public class MallOrderTradeInfoListener extends CallbackListener<PayTradeInfoDefiner.PayTradeInfoEvent> {
    private MallOrderMapper mallOrderMapper;
    private ICoreUploadService coreUploadService;
    private MallOrderGoodsMapper mallOrderGoodsMapper;

    @Override
    public PayTradeInfoDefiner.PayTradeInfoEventResult handleCallback(PayTradeInfoDefiner.PayTradeInfoEvent event) {
        if (!event.getTradeType().equals("mall")) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }

        MallOrder mallOrder = mallOrderMapper.selectById(event.getTradeId());
        if (mallOrder == null) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }

        QueryWrapper<MallOrderGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("order_id", event.getTradeId());
        List<MallOrderGoods> goods = mallOrderGoodsMapper.selectList(goodsQueryWrapper);
        if (ObjectUtil.isEmpty(goods)) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }

        List<JSONObject> item = goods.stream().map(o -> {
            String mid = coreUploadService.thumb(RequestUtils.siteId(), o.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            JSONObject entries = new JSONObject();
            entries.set("item_image", Optional.ofNullable(mid).orElse(o.getSkuImage()))
                    .set("item_name", o.getGoodsName())
                    .set("item_sub_name", o.getSkuName())
                    .set("item_price", o.getPrice())
                    .set("item_num", o.getNum());
            return entries;
        }).toList();

        JSONObject result = new JSONObject();
        result.set("item_list", item)
                .set("item_total", "共" + goods.size() + "种商品")
                .set("detail_url", "/addon/shop/pages/order/detail?order_id=" + mallOrder.getOrderId());

        PayTradeInfoDefiner.PayTradeInfoEventResult eventResult = new PayTradeInfoDefiner.PayTradeInfoEventResult();
        eventResult.setJsonObject(result);
        return eventResult;
    }

    @Autowired
    public void setMallOrderMapper(MallOrderMapper mallOrderMapper) {
        this.mallOrderMapper = mallOrderMapper;
    }

    @Autowired
    public void setCoreUploadService(ICoreUploadService coreUploadService) {
        this.coreUploadService = coreUploadService;
    }

    @Autowired
    public void setMallOrderGoodsMapper(MallOrderGoodsMapper mallOrderGoodsMapper) {
        this.mallOrderGoodsMapper = mallOrderGoodsMapper;
    }
}
