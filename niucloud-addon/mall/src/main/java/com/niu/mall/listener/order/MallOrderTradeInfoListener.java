package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@EventCallback("mall")
public class MallOrderTradeInfoListener extends CallbackListener<PayTradeInfoDefiner.PayTradeInfoEvent> {
    private MallOrderMapper mallOrderMapper;
    private ICoreUploadService coreUploadService;
    private MallOrderGoodsMapper mallOrderGoodsMapper;
    @Resource
    private SiteMapper siteMapper;

    @Override
    public PayTradeInfoDefiner.PayTradeInfoEventResult handleCallback(PayTradeInfoDefiner.PayTradeInfoEvent event) {
        if (!event.getTradeType().equals("mall")) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }

        List<MallOrder> mallOrderList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", event.getTradeId()));
        if (mallOrderList.isEmpty()) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }

        QueryWrapper<MallOrderGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.in("order_id", mallOrderList.stream().map(MallOrder::getOrderId).toList());
        List<MallOrderGoods> goods = mallOrderGoodsMapper.selectList(goodsQueryWrapper);
        if (ObjectUtil.isEmpty(goods)) {
            return new PayTradeInfoDefiner.PayTradeInfoEventResult();
        }
        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().select("site_id", "site_name").in("site_id", mallOrderList.stream().map(MallOrder::getSiteId).toList()));

        //按照订单进行分组
        List<JSONObject> itemList = new ArrayList<>();
        for (MallOrder order : mallOrderList){
            List<MallOrderGoods> goodsThis = goods.stream().filter(o -> o.getOrderId().equals(order.getOrderId())).toList();
            if(!goodsThis.isEmpty()){
                List<JSONObject> childList = goodsThis.stream().map(o -> {
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), o.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    JSONObject entries = new JSONObject();
                    entries.set("item_image", Optional.ofNullable(mid).orElse(o.getSkuImage()))
                            .set("item_name", o.getGoodsName())
                            .set("item_sub_name", o.getSkuName())
                            .set("item_price", o.getPrice())
                            .set("item_num", o.getNum());
                    return entries;
                }).toList();
                List<Site>  siteThis = siteList.stream().filter(o -> o.getSiteId().equals(order.getSiteId())).toList();
                JSONObject site = new JSONObject();
                site.set("site_name", siteThis.get(0).getSiteName());
                site.set("site_id", order.getSiteId());
                JSONObject item = new JSONObject();
                item.set("child_list", childList);
                item.set("site",site);
                itemList.add(item);
            }
        }


        JSONObject result = new JSONObject();
        result.set("item_list", itemList)
                .set("item_total", "共" + goods.size() + "种商品")
                .set("detail_url", "/addon/mall/pages/order/list");

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
