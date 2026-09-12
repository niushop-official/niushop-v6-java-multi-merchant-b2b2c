package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.pay.vo.PayTradeInfoVo;
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
import java.util.Optional;

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
            return null;
        }

        List<MallOrder> mallOrderList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", event.getTradeId()));
        if (mallOrderList.isEmpty()) {
            return null;
        }

        QueryWrapper<MallOrderGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.in("order_id", mallOrderList.stream().map(MallOrder::getOrderId).toList());
        List<MallOrderGoods> goods = mallOrderGoodsMapper.selectList(goodsQueryWrapper);
        if (ObjectUtil.isEmpty(goods)) {
            return null;
        }
        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().select("site_id", "site_name").in("site_id", mallOrderList.stream().map(MallOrder::getSiteId).toList()));

        List<PayTradeInfoVo.Item> itemList = new ArrayList<>();
        for (MallOrder order : mallOrderList) {
            List<MallOrderGoods> goodsThis = goods.stream().filter(o -> o.getOrderId().equals(order.getOrderId())).toList();
            if (!goodsThis.isEmpty()) {
                List<PayTradeInfoVo.ChildItem> childList = goodsThis.stream().map(o -> {
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), o.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    PayTradeInfoVo.ChildItem child = new PayTradeInfoVo.ChildItem();
                    child.setItemImage(Optional.ofNullable(mid).orElse(o.getSkuImage()));
                    child.setItemName(o.getGoodsName());
                    child.setItemSubName(o.getSkuName());
                    child.setItemPrice(o.getPrice());
                    child.setItemNum(o.getNum());
                    return child;
                }).toList();

                List<Site> siteThis = siteList.stream().filter(o -> o.getSiteId().equals(order.getSiteId())).toList();
                PayTradeInfoVo.Site site = new PayTradeInfoVo.Site();
                if (!siteThis.isEmpty()) {
                    site.setSiteId(order.getSiteId());
                    site.setSiteName(siteThis.get(0).getSiteName());
                }

                PayTradeInfoVo.Item item = new PayTradeInfoVo.Item();
                item.setChildList(childList);
                item.setSite(site);
                itemList.add(item);
            }
        }

        PayTradeInfoVo tradeInfo = new PayTradeInfoVo();
        tradeInfo.setItemList(itemList);
        tradeInfo.setItemTotal("共" + goods.size() + "种商品");
        tradeInfo.setDetailUrl("/addon/mall/pages/order/list");

        PayTradeInfoDefiner.PayTradeInfoEventResult eventResult = new PayTradeInfoDefiner.PayTradeInfoEventResult();
        eventResult.setTradeInfo(tradeInfo);
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
