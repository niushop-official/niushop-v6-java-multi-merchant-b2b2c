package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICorePrinterService;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.order.OrderFinishAfterEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderEventService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.shop.ICoreShopPerkService;
import com.niu.mall.service.core.shop.ICoreShopSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Map;

@Service
@Slf4j
public class CoreOrderFinishServiceImpl implements ICoreOrderFinishService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    ICorePrinterService corePrinterService;

    @Resource
    ICoreShopSettlementService coreShopSettlementService;

    @Resource
    ICoreShopPerkService coreShopPerkService;
    @Resource
    ICoreAddonService coreAddonService;

    @Override
    public boolean finish(OrderFinishParam param) {
        MallOrder mallOrder;
        if(ObjectUtil.isNotEmpty(param.getOrderNo()))
        {
            mallOrder = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_no", param.getOrderNo()));
            param.setOrderId(mallOrder.getOrderId());
        }else{
            mallOrder = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", param.getOrderId()));

        }

        if (ObjectUtil.isEmpty(mallOrder)) throw new CommonException("订单信息不存在");

        if (mallOrder.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString())){
            return true;
        }
        Long refundCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>().eq("order_id", param.getOrderId()).eq("status", OrderGoodsRefundEnum.REFUNDING.getStatus()));
        if (refundCount > 0) {
            log.info("订单商品存在退款中的商品，不能完成订单");
            return false;
        }

        //执行订单完成
        mallOrder.setStatus(OrderStatusEnum.FINISH.getStatus().toString());
        mallOrder.setFinishTime(System.currentTimeMillis() / 1000);
        mallOrder.setTimeout(0L);
        mallOrder.setIsEnableRefund(1);
        mallOrderMapper.updateById(mallOrder);

        //执行订单项目收货
        this.orderGoodsTake(param.getOrderId(), null);

        //执行订单事件
        coreOrderEventService.orderFinishAfter(mallOrder, param);

        //订单结算
        coreShopSettlementService.settlement(mallOrder);
        //订单结算补贴
        coreShopPerkService.perk(mallOrder);

        // 订单完成之后事件
        OrderFinishAfterEvent event = new OrderFinishAfterEvent();
        event.setOrder(mallOrder);
        event.setSiteId(mallOrder.getSiteId());
        event.addAppSign("mall");
        event.setName("OrderFinishAfterEvent");
        EventAndSubscribeOfPublisher.publishAll(event);
        try{
            SysPrinterPrintTicketParam printParam = new SysPrinterPrintTicketParam();
            printParam.setSiteId(mallOrder.getSiteId());
            printParam.setType("mallGoodsOrder");
            printParam.setTrigger("take_delivery");
            SysPrinterPrintTicketParam.Business business = new SysPrinterPrintTicketParam.Business();
            business.setOrderId(mallOrder.getOrderId());
            printParam.setBusiness(business);
            corePrinterService.printTicket(printParam);
        }catch (Exception e){
            log.error(" 小票打印失败 - 堆栈信息:", e);
        }
        return true;
    }

    @Override
    public boolean orderGoodsTake(Integer orderId, Integer[] orderGoodsId) {

        QueryWrapper<MallOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("status", OrderGoodsRefundEnum.NORMAL.getStatus());
        if (ObjectUtil.isNotEmpty(orderGoodsId)) queryWrapper.in("order_goods_id", orderGoodsId);

        MallOrderGoods mallOrderGoods = new MallOrderGoods();
        mallOrderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.TAKED.getStatus());
        mallOrderGoods.setIsEnableRefund(0);
        mallOrderGoodsMapper.update(mallOrderGoods, queryWrapper);
        return true;
    }

    /**
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }
}
