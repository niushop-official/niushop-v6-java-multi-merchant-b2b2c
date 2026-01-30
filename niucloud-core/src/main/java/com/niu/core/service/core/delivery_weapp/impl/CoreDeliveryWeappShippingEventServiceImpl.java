package com.niu.core.service.core.delivery_weapp.impl;

import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoGetResponse;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShipping;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShippingSplit;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingMapper;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingSplitMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappService;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingEventService;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingService;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingSplitService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingCreateParam;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingParam;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingSpliteCreateParam;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingEventVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;



/**
 * 小程序发货信息服务层
 */
@Log4j2
@Service
public class CoreDeliveryWeappShippingEventServiceImpl implements ICoreDeliveryWeappShippingEventService {

    @Resource
    private ICoreDeliveryWeappShippingService coreDeliveryWeappShippingService;
    @Resource
    private ICoreDeliveryWeappShippingSplitService coreDeliveryWeappShippingSplitService;
    @Resource
    private ICoreDeliveryWeappService coreDeliveryWeappService;
    @Resource
    private DeliveryWeappShippingMapper deliveryWeappShippingMapper;
    @Resource
    private DeliveryWeappShippingSplitMapper deliveryWeappShippingSplitMapper;

    @Override
    public WeappShippingEventVo beforeWeappShipping(WeappShippingParam param) {
        // 保存发货信息录入记录
        WeappShippingCreateParam createParam = new WeappShippingCreateParam();
        createParam.setOrderNumberType(param.getOrderNumberType());
        createParam.setTransactionId(param.getTransactionId());
        createParam.setLogisticsType(param.getLogisticsType());
        createParam.setDeliveryMode(param.getDeliveryMode());
        createParam.setWeappOpenid(param.getWeappOpenid());
        createParam.setSiteId(param.getSiteId());
        createParam.setTradeId(param.getTradeId());
        createParam.setTradeType(param.getTradeType());
        createParam.setTradeNo(param.getTradeNo());
        Integer shippingId = coreDeliveryWeappShippingService.create(createParam);

        // 保存拆单记录
        WeappShippingSpliteCreateParam spliteCreateParam = new WeappShippingSpliteCreateParam();
        spliteCreateParam.setShippingId(shippingId);
        spliteCreateParam.setTransactionId(param.getTransactionId());
        spliteCreateParam.setIsAllDelivered(param.getIsAllDelivered());
        spliteCreateParam.setShippinglist(param.getShippingList());
        spliteCreateParam.setSiteId(param.getSiteId());
        spliteCreateParam.setTradeId(param.getTradeId());
        spliteCreateParam.setTradeType(param.getTradeType());
        spliteCreateParam.setTradeNo(param.getTradeNo());
        spliteCreateParam.setSplitTradeId(param.getSplitTradeId());
        spliteCreateParam.setSplitTradeType(param.getSplitTradeType());
        spliteCreateParam.setSplitTradeNo(param.getSplitTradeNo());
        Integer splitId = coreDeliveryWeappShippingSplitService.create(spliteCreateParam);
        WeappShippingEventVo eventVo = new WeappShippingEventVo();
        eventVo.setShippingId(shippingId);
        eventVo.setSplitId(splitId);
        return eventVo;
    }

    @Override
    public Boolean weappShipping(WeappShippingParam param) {
        // 检查小程序是否已开通发货信息管理服务，并设置跳转路径
        coreDeliveryWeappService.checkIsTradeManagedAndSetMsgJumpPath();
        // 发货信息录入前处理
        //查询订单发货状态,如果已发货,则不重复发货
        WeappShippingEventVo eventVo = beforeWeappShipping(param);

        WxMaOrderShippingInfoGetResponse response = coreDeliveryWeappService.getOrder(param.getTransactionId());
        if(ObjectUtil.isNotEmpty(response.getErrCode()) && response.getErrCode() == 0 && ObjectUtil.isNotEmpty(response.getOrder()) && response.getOrder().getOrderState() != 1){
            afterWeappShipping(eventVo.getShippingId(), eventVo.getSplitId(), response, "");
            return true;
        }else{
            try {
                // 调用微信小程序发货接口
                WeappUploadShippingParam shippingParam = new WeappUploadShippingParam();
                shippingParam.setTransactionId(param.getTransactionId());
                shippingParam.setLogisticsType(param.getLogisticsType());
                shippingParam.setDeliveryMode(param.getDeliveryMode());
                shippingParam.setWeappOpenid(param.getWeappOpenid());
                shippingParam.setIsAllDelivered(param.getIsAllDelivered());
                shippingParam.setShippingList(param.getShippingList());
                WxMaOrderShippingInfoBaseResponse responseShipping = coreDeliveryWeappService.uploadShippingInfo(shippingParam);
                afterWeappShipping(eventVo.getShippingId(), eventVo.getSplitId(), responseShipping, "");
                return true;
            }catch (Exception e){
                afterWeappShipping(eventVo.getShippingId(), eventVo.getSplitId(), null, e.getMessage());
                return false;
            }
        }
    }

    @Override
    public void afterWeappShipping(Integer shippingId, Integer splitId, WxMaOrderShippingInfoBaseResponse response, String errorMsg) {
        errorMsg = StringUtils.isNotEmpty(errorMsg) ? errorMsg : "";
        DeliveryWeappShippingSplit split = new DeliveryWeappShippingSplit();
        split.setId(splitId);
        split.setStatus(0);
        split.setErrcode("");
        split.setErrmsg(errorMsg);

        if(ObjectUtil.isNotEmpty(response)){
            split.setErrcode(response.getErrCode().toString());
            split.setErrmsg(response.getErrMsg());
            if(response.getErrCode() == 0){
                split.setStatus(1);
            }
            DeliveryWeappShipping deliveryWeappShipping = new DeliveryWeappShipping();
            deliveryWeappShipping.setId(shippingId);
            deliveryWeappShipping.setStatus(1);
            deliveryWeappShippingMapper.updateById(deliveryWeappShipping);
        }
        deliveryWeappShippingSplitMapper.updateById(split);
    }
}
