package com.niu.core.service.core.delivery_weapp;

import cn.binarywang.wx.miniapp.bean.express.WxMaExpressDelivery;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoGetResponse;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;

import java.util.List;

/**
 * 小程序发货信息管理服务
 */
public interface ICoreDeliveryWeappService {

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return 小程序是否已开通发货信息管理服务
     */
    Boolean isTradeManaged();

    /**
     * 获取小程序是否已开通发货信息管理服务
     * @return 小程序是否已开通发货信息管理服务
     */
    IsTradeManagedVo getIsTradeManaged();

    /**
     * 消息跳转路径设置接口
     * @return 消息跳转路径设置接口
     */
    Boolean setMsgJumpPath();

    /**
     * 发货信息录入接口
     * @param shippingData
     * @return
     */
    WxMaOrderShippingInfoBaseResponse uploadShippingInfo(WeappUploadShippingParam shippingData);

    /**
     * 确认收货提醒接口
     * @param transactionId 订单号
     * @return 确认收货提醒接口
     */
    WxMaOrderShippingInfoBaseResponse notifyConfirmReceive(String transactionId);

    /**
     * 获取物流公司，运力id列表
     */
    List<WxMaExpressDelivery> getDeliveryList();

    /**
     * 查询订单发货状态
     */
    WxMaOrderShippingInfoGetResponse getOrder(String transactionId);

    /**
     * 获取物流模式
     */
    Integer getLogisticsType(String deliveryType,Integer orderDeliveryCount);

    /**
     * 获取发货模式
     */
    Integer getDeliveryMode(Integer orderDeliveryCount);

    /**
     * 获取物流公司缓存
     */
    List<WxMaExpressDelivery> getDeliveryListCache();

    /**
     * 检查小程序是否已开通发货信息管理服务，并设置跳转路径
     * @return
     */
    Boolean checkIsTradeManagedAndSetMsgJumpPath();
}
