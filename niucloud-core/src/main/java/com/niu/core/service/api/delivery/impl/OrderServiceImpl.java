package com.niu.core.service.api.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryTradePackage;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.delivery.DeliveryTradePackageMapper;
import com.niu.core.service.api.delivery.IDeliveryPackageService;
import com.niu.core.service.api.delivery.IOrderService;
import com.niu.core.service.api.delivery.param.OrderNotifyParam;
import com.niu.core.service.api.delivery.param.OrderServiceParam;
import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.api.delivery.vo.OrderServiceVo;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderNotifyService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 配送包裹服务层
 */
@Log4j2
@Service
public class OrderServiceImpl implements IOrderService {

    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;
    @Resource
    private ICoreLocalDeliveryOrderNotifyService coreLocalDeliveryOrderNotifyService;
    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;

    @Override
    public OrderServiceVo getDeliveryTrack(OrderServiceParam data) {
        OrderServiceVo result = new OrderServiceVo();
        //获取配送订单
        List<DeliveryLocalDeliveryOrder> list = deliveryLocalDeliveryOrderMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryOrder>().eq("trade_no", data.getOrderNo()));
        Map<String, Object> localDeliveryConfig = new HashMap<>();
        if(!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(data.getDeliveryService())){
            Integer siteId = !list.isEmpty() ? list.get(0).getSiteId() : RequestUtils.defaultSiteId();
            localDeliveryConfig = coreLocalDeliveryService.getConfig(data.getDeliveryService(),siteId);
            if (localDeliveryConfig.isEmpty()) {
                String typeName = LocalDeliveryTypeEnum.getName(data.getDeliveryService()) != null ? LocalDeliveryTypeEnum.getName(data.getDeliveryService()) : data.getDeliveryService();
                throw new RuntimeException(typeName + "未配置");
            }
            localDeliveryConfig = JSONUtil.parseObj(localDeliveryConfig.get("config"));
        }

        JSONObject config = JSONUtil.parseObj(localDeliveryConfig);
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("order_no", data.getOrderNo());
            if (!RequestUtils.channel().equals(ChannelEnum.WEAPP.getCode())){
                requestData.put("is_h5",1);
            }
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(data.getDeliveryService()).init(JSONUtil.parseObj(config));
            Map<String, Object> responseData = driver.getTransporterPosition(requestData);
            if(ObjectUtil.isNotEmpty(responseData.get("trackUrl"))){
                result.setUrl(responseData.get("trackUrl").toString());
            }
        }catch (Exception e){
           log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void notify(OrderNotifyParam data,Map<String, String> allParams) {
        NotifyShopDeliveryOrderParam param = new NotifyShopDeliveryOrderParam();
        param.setNotifyData(JSONUtil.parseObj(allParams));
        coreLocalDeliveryOrderNotifyService.notify(data.getSiteId(), data.getType(),data.getOrderNo(), data.getAction(), param);
    }
}
