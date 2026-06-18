package com.niu.mall.listener.goods;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@EventListen("mall")
@Slf4j
public class AfterMallGoodsEditListener extends AbstractListener {

    @Resource
    private MallGoodsMapper mallGoodsMapper;
    @Resource
    private ICoreShopStatService coreShopStatService;

    public void handleEvent(AfterGoodsEditEvent event) {
        log.info("商品编辑AfterGoodsEditEventListener: {}", event);
        try {
            Long count = mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>().eq("site_id", event.getGoodsData().getSiteId())
                    .eq("status", GoodsStatusEnum.ONLINE.getStatus())
                    .eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
            );
            ShopStatParam param = new ShopStatParam();
            param.setSiteId(event.getGoodsData().getSiteId());
            param.setMallUpNum(count.intValue());
            coreShopStatService.setStat(param);
        } catch (Exception e) {
            log.error("商品编辑AfterGoodsEditEventListener失败: {}", e.getMessage(), e);
        }
    }
}
