package com.niu.mall.listener.goods;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.event.goods.AfterMallGoodsStatusEditEvent;
import com.niu.mall.event.goods.AfterPickGoodsEvent;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@EventListen("mall")
@Slf4j
public class AfterMallGoodsStatusEditListener extends AbstractListener {

    @Resource
    private MallGoodsMapper mallGoodsMapper;
    @Resource
    private ICoreShopStatService coreShopStatService;
    @Resource
    ICoreAddonService coreAddonService;

    public void handleEvent(AfterMallGoodsStatusEditEvent event) {
        log.info("商品状态编辑AfterMallGoodsStatusEditListener: {}", event);
        try {
            Integer siteId = event.getSiteId();
            List<Integer> goodsIds = event.getGoodsIds();
            if (goodsIds == null) {
                goodsIds = new ArrayList<>();
            }
            // 更新店铺商城商品统计数据
            QueryWrapper<MallGoods> countWrapper = new QueryWrapper<>();
            countWrapper.eq("site_id", siteId)
                    .eq("status", GoodsStatusEnum.ONLINE.getStatus())
                    .eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
            Long mallUpNum = mallGoodsMapper.selectCount(countWrapper);
            ShopStatParam shopStatParam = new ShopStatParam();
            shopStatParam.setSiteId(siteId);
            shopStatParam.setMallUpNum(mallUpNum.intValue());
            coreShopStatService.setStat(shopStatParam);
            // 更新供应商挑货数量和挑货上架数量
            if (checkAddon("supply")) {
                for (Integer goodsId : goodsIds) {
                    QueryWrapper<MallGoods> goodsQuery = new QueryWrapper<>();
                    goodsQuery.eq("goods_id", goodsId)
                            .eq("site_id", siteId)
                            .gt("supply_goods_id", 0)
                            .select("supply_id", "supply_goods_id");
                    MallGoods goodsInfo = mallGoodsMapper.selectOne(goodsQuery);

                    if (goodsInfo != null && goodsInfo.getSupplyId() != null && goodsInfo.getSupplyGoodsId() != null) {
                        AfterPickGoodsEvent afterPickGoodsEvent = new AfterPickGoodsEvent();
                        afterPickGoodsEvent.setSiteId(siteId);
                        afterPickGoodsEvent.setSupplyId(goodsInfo.getSupplyId());
                        afterPickGoodsEvent.setGoodId(goodsInfo.getSupplyGoodsId());
                        EventAndSubscribeOfPublisher.publishAndCallbackBySite(afterPickGoodsEvent);
                    }
                }
            }
        } catch (Exception e) {
            log.error("商品状态编辑AfterMallGoodsStatusEditListener失败: {}", e.getMessage(), e);
        }
    }

    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

}
