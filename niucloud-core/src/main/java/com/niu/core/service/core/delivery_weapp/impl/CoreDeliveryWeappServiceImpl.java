package com.niu.core.service.core.delivery_weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.express.WxMaExpressDelivery;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoGetResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.channel.CertEnum;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.delivery.OrderDeliveryTypeEnum;
import com.niu.core.mapper.pay.PayChannelMapper;
import com.niu.core.service.admin.pay.IPayChannelService;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappService;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 小程序发货信息管理服务
 */
@Log4j2
@Service
public class CoreDeliveryWeappServiceImpl implements ICoreDeliveryWeappService {

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;
    public static final String CACHE_TAG_NAME = "weapp_upload_shipping_info_cash";
    // ====== 微信官方核心接口地址 ======
    private static final String GET_DELIVERY_LIST_URL = "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/get_delivery_list";
    @Resource
    private Cached cached;
    @Resource
    private PayChannelMapper payChannelMapper;


    /**
     * 查询小程序是否已开通发货信息管理服务
     * 文档：https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/order-shipping/order-shipping.html#七、查询小程序是否已开通发货信息管理服务
     * 返回结果：{"errcode":0,"errmsg":"ok","is_trade_managed":true}
     * @return mixed
     */
    @Override
    public Boolean isTradeManaged() {
        try {
            WxMaService miniapp = WechatUtils.miniapp(0);
            String appid = miniapp.getWxMaConfig().getAppid();
            WxMaOrderShippingIsTradeManagedResponse res = miniapp.getWxMaOrderShippingService().isTradeManaged(appid);
            log.error("确认收货提醒接口，返回结果： {}", JSONUtil.toJsonStr( res));
            if (ObjectUtil.isNotEmpty(res.getErrCode()) && res.getErrCode() == 0 && res.getTradeManaged()) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            log.error("查询小程序是否已开通发货信息管理服务失败， 错误信息: {}, 文件名: {}, 行号: {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            return false;
        }
    }

    /**
     * 发货信息录入接口
     * 文档：https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/order-shipping/order-shipping.html#一、发货信息录入接口
     * 用户交易后，默认资金将会进入冻结状态，开发者在发货后，需要在小程序平台录入相关发货信息，平台会将发货信息以消息的形式推送给购买的微信用户。
     * 如果你已经录入发货信息，在用户尚未确认收货的情况下可以通过该接口修改发货信息，但一个支付单只能更新一次发货信息，请谨慎操作。
     * 如暂时没有完成相关API的对接开发工作，你也可以登陆小程序的后台，通过发货信息管理页面手动录入发货信息。
     * 注意事项
     * 1、根据指定的订单单号类型，采用不同参数给指定订单上传物流信息：
     *      (1). 商户侧单号形式（枚举值1），通过下单商户号和商户侧单号确定一笔订单
     *      (2). 微信支付单号形式（枚举值2），通过微信支付单号确定一笔订单
     * 2、发货模式根据具体发货情况选择：
     *      (1). 统一发货（枚举值1），一笔订单统一发货，只有一个物流单号。
     *      (2). 分拆发货（枚举值2），一笔订单分拆发货，包括多个物流单号。
     * 3、物流公司编码，参见获取运力 id 列表get_delivery_list。
     *      https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/industry/express/business/express_search.html#%E8%8E%B7%E5%8F%96%E8%BF%90%E5%8A%9Bid%E5%88%97%E8%A1%A8get-delivery-list
     * 4、上传时间，用于标识请求的先后顺序，如果要更新物流信息，上传时间必须比之前的请求更新，请按照 RFC 3339 格式填写。
     * 5、分拆发货仅支持使用物流快递发货，一笔支付单最多分拆成 10 个包裹。
     * 6、以下情况将视为重新发货，每笔支付单仅有一次重新发货机会。
     *      (1). 对已完成发货的支付单再次调用该 API。
     *      (2). 使用该 API 修改发货模式或物流模式。
     */
    @Override
    public WxMaOrderShippingInfoBaseResponse uploadShippingInfo( WeappUploadShippingParam shippingData) {
        try {
            checkIsTradeManagedAndSetMsgJumpPath();

            // 构建微信发货信息对象
            WxMaOrderShippingInfoUploadRequest shippingInfo = new WxMaOrderShippingInfoUploadRequest();

            // 订单信息
            OrderKeyBean orderKey = new OrderKeyBean();
            orderKey.setOrderNumberType(2);// 订单单号类型，用于确认需要上传详情的订单。枚举值1，使用下单商户号和商户侧单号；枚举值2，使用微信支付单号。
            orderKey.setTransactionId(shippingData.getTransactionId());// 原支付交易对应的微信订单号
            shippingInfo.setOrderKey(orderKey);

            // 物流信息
            shippingInfo.setLogisticsType(shippingData.getLogisticsType());// 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
            shippingInfo.setDeliveryMode(shippingData.getDeliveryMode());// 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货） 示例值: UNIFIED_DELIVERY

            // 处理发货列表
            List<ShippingListBean> wxShippingList = new ArrayList<>();
            for (WeappUploadShippingParam.UploadShippingParam item : shippingData.getShippingList()) {
                ShippingListBean shipping = new ShippingListBean();
                BeanUtils.copyProperties(item, shipping);
                WeappUploadShippingParam.ContactInfo contact = item.getContact();
                if (contact != null) {
                    ContactBean wxContact = new ContactBean();
                    BeanUtils.copyProperties(contact, wxContact);
                    shipping.setContact(wxContact);
                }
                wxShippingList.add(shipping);
            }
            shippingInfo.setShippingList(wxShippingList);// 物流信息列表，发货物流单列表，支持统一发货（单个物流单）和分拆发货（多个物流单）两种模式，多重性: [1, 10]

            // 上传时间
            shippingInfo.setUploadTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // 付款人信息
            PayerBean payer = new PayerBean();
            payer.setOpenid(shippingData.getWeappOpenid());
            shippingInfo.setPayer(payer);

            // 发货完成标识
            shippingInfo.setIsAllDelivered(shippingData.getIsAllDelivered());

            log.info("PINTUAN-发货信息录入接口参数: {}", JSONUtil.toJsonStr(shippingInfo));

            // 微信订单录入有时差，延时3秒执行
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 使用微信SDK调用API
            WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(0)
                    .getWxMaOrderShippingService()
                    .upload(shippingInfo);

            log.info("PINTUAN-发货信息录入接口返回结果: {}", JSONUtil.toJsonStr(response));
            return response;
        } catch (Exception e) {
            String errorMsg = String.format("uploadShippingInfo报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg);
            throw new CommonException(e.getMessage());
        }
    }


    /**
     * 确认收货提醒接口
     * 文档：https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/order-shipping/order-shipping.html#五、确认收货提醒接口
     * 如你已经从你的快递物流服务方获知到用户已经签收相关商品，可以通过该接口提醒用户及时确认收货，以提高资金结算效率，每个订单仅可调用一次。
     * 注意事项
     * 1、通过交易单号或商户号+商户单号来指定订单。
     * 2、只有物流类型为物流快递时才能进行提醒。
     * 3、签收时间由商户传入，在给用户发送提醒消息时会显示签收时间，签收时间必须在发货时间之后。
     */
    @Override
    public WxMaOrderShippingInfoBaseResponse notifyConfirmReceive(String transactionId) {
        try {
            checkIsTradeManagedAndSetMsgJumpPath();
            WxMaOrderShippingInfoNotifyConfirmRequest shippingInfo = new WxMaOrderShippingInfoNotifyConfirmRequest();
            shippingInfo.setTransactionId(transactionId);
            shippingInfo.setReceivedTime(System.currentTimeMillis()/1000);
            log.info("确认收货提醒接口，参数打印：{}", JSONUtil.toJsonStr(shippingInfo));
            // 使用微信SDK调用API
            WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(0)
                    .getWxMaOrderShippingService()
                    .notifyConfirmReceive(shippingInfo);
            log.info("确认收货提醒接口，返回结果: {}", JSONUtil.toJsonStr(response));
            return response;
        } catch (Exception e) {
            String errorMsg = String.format("notifyConfirmReceive报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg);
            WxMaOrderShippingInfoBaseResponse errorResult = new WxMaOrderShippingInfoBaseResponse();
            errorResult.setErrCode(1);
            errorResult.setErrMsg(errorMsg);
            return errorResult;
        }
    }

    @Override
    public List<WxMaExpressDelivery> getDeliveryList() {
        List<WxMaExpressDelivery> deliveryList = new ArrayList<>();
        try {
            WxMaService wxMaService = WechatUtils.miniapp(0);
            String accessToken =  wxMaService.getAccessToken();
            String requestUrl = GET_DELIVERY_LIST_URL + "?access_token=" + accessToken;

            // 发送POST请求，该接口无入参，传空JSON即可
            String response = wxMaService.post(requestUrl, "{}");
            JSONObject result = JSONUtil.parseObj(response);
            // 微信接口异常处理，沿用SDK的错误码体系
            if (result.getInt("errcode") != null && result.getInt("errcode") != 0) {
                throw new CommonException("获取物流商列表失败: " + result.getStr("errmsg") + ", 错误码: " + result.getInt("errcode"));
            }
            if (result.containsKey("delivery_list")) {
                JSONArray deliveryListJson = result.getJSONArray("delivery_list");
                for (int i = 0; i < deliveryListJson.size(); i++) {
                    JSONObject json = deliveryListJson.getJSONObject(i);
                    WxMaExpressDelivery delivery = new WxMaExpressDelivery();
                    delivery.setDeliveryId(json.getStr("delivery_id"));
                    delivery.setDeliveryName(json.getStr("delivery_name"));
                    deliveryList.add(delivery);
                }
            }
        }catch (Exception e){
            String errorMsg = String.format("getDeliveryList报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg);
            throw new CommonException(e.getMessage());
        }
        return deliveryList;
    }

    @Override
    public WxMaOrderShippingInfoGetResponse getOrder(String transactionId) {
        try {
        WxMaOrderShippingInfoGetRequest orderInfo = new WxMaOrderShippingInfoGetRequest();
        orderInfo.setTransactionId(transactionId);
            WxMaOrderShippingInfoGetResponse response = WechatUtils.miniapp(0)
                .getWxMaOrderShippingService().get(orderInfo);
        log.info("查询订单发货状态getOrder，返回结果：{}", JSONUtil.toJsonStr(response));
        return response;
        }catch (Exception e){
            String errorMsg = String.format("getOrder报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg, e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public Integer getLogisticsType(String deliveryType, Integer orderDeliveryCount) {
        // 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
        Integer logisticsType = 1;
        if(OrderDeliveryTypeEnum.EXPRESS.getType().equals(deliveryType)){
            logisticsType = 1;
        }else if(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType().equals(deliveryType)){
            logisticsType = 2;
        }else if(OrderDeliveryTypeEnum.VIRTUAL.getType().equals(deliveryType)){
            logisticsType = 3;
        }else if(OrderDeliveryTypeEnum.STORE.getType().equals(deliveryType)){
            logisticsType = 4;
        }
        if(orderDeliveryCount>1){
            logisticsType = 1;
        }
        return logisticsType;
    }

    @Override
    public Integer getDeliveryMode(Integer orderDeliveryCount) {
        Integer deliveryMode = 1;
        if(orderDeliveryCount>1){
            deliveryMode = 2;
        }
        return deliveryMode;
    }

    @Override
    public List<WxMaExpressDelivery> getDeliveryListCache() {
        return cached.cache(useCache, CACHE_TAG_NAME, "weapp_delivery_list", uniqueKey -> getDeliveryList());
    }

    /**
     * 获取小程序是否已开通发货信息管理服务，并设置跳转路径
     * @return IsTradeManagedVo
     */
    @Override
    public IsTradeManagedVo getIsTradeManaged() {
        IsTradeManagedVo vo = new IsTradeManagedVo();
        try {
            // 检测微信小程序是否已开通发货信息管理服务
            Boolean isTradeManaged = isTradeManagedCache();
            if(isTradeManaged){
                // 获取微信支付配置
                PayChannel payChannel = payChannelMapper.selectOne(new LambdaQueryWrapper<PayChannel>()
                        .eq(PayChannel::getType, CertEnum.WECHATPAY.getCode())
                        .eq(PayChannel::getChannel, CertEnum.WEAPP.getCode()).last("limit 1"));

                if (payChannel != null && !ObjectUtils.isEmpty(payChannel.getConfig())) {
                    String config = payChannel.getConfig();
                    JSONObject jsonObject = JSONUtil.parseObj(config);
                    vo.setMchId(jsonObject.getInt("mch_id"));
                }
                vo.setIsTradeManaged(true);
            }else{
                vo.setIsTradeManaged(false);
            }
        }catch (Exception e){
            vo.setIsTradeManaged(false);
            log.error("getIsTradeManaged服务失败， 错误信息: {}, 文件名: {}, 行号: {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
        }
        return vo;
    }


    /**
     * 检查小程序是否已开通发货信息管理服务，并设置跳转路径
     * @return mixed
     */
    @Override
    public Boolean checkIsTradeManagedAndSetMsgJumpPath() {
        // 检测微信小程序是否已开通发货信息管理服务
        Boolean isTradeManaged = isTradeManagedCache();
        if(!isTradeManaged){
            return false;
        }
        // 设置消息跳转路径设置接口
        Boolean isSetJumpPath = setMsgJumpPathCache();
        if(!isSetJumpPath){
            return false;
        }
        return true;
    }

    /**
     * 消息跳转路径设置接口
     * 如你已经在小程序内接入平台提供的确认收货组件，可以通过该接口设置发货消息及确认收货消息的跳转动作，用户点击发货消息时会直接进入你的小程序订单列表页面或详情页面进行确认收货，进一步优化用户体验。
     * 注意事项
     * 1、如设置为空路径或小程序中不存在的路径，将仍然跳转平台默认的确认收货页面，不会进入你的小程序。
     * 2、平台会在路径后面增加支付单的 transaction_id、merchant_id、merchant_trade_no 作为query参数，如果存在二级商户号则还会再增加 sub_merchant_id 参数,开发者可以在小程序中通过onLaunch等方式获取。
     * 3、如你需要在path中携带自定义的query参数，请注意与上面的参数进行区分
     * @return mixed
     */
    @Override
    public Boolean setMsgJumpPath() {
        try {
            String path = "app/pages/weapp/order_shipping";
            WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(0).getWxMaOrderShippingService().setMsgJumpPath(path);
            log.error("消息跳转路径设置接口，返回结果： {}", JSONUtil.toJsonStr( response));
            if(ObjectUtil.isNotEmpty(response.getErrCode()) && response.getErrCode() == 0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            log.error("消息跳转路径设置失败: {}, 文件名: {}, 行号: {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            return false;
        }
    }



    /**
     * 查询小程序是否已开通发货信息管理服务（缓存）
     * @return mixed
     */
    public Boolean isTradeManagedCache(){
        WxMaService miniapp = WechatUtils.miniapp(0);
        String appid = miniapp.getWxMaConfig().getAppid();
        String cacheName = "is_trade_managed" + appid;
        return cached.cache(useCache, CACHE_TAG_NAME, cacheName, uniqueKey -> isTradeManaged());
    }

    /**
     * 设置消息跳转路径设置接口
     * @return mixed
     */
    public Boolean setMsgJumpPathCache(){
        WxMaService miniapp = WechatUtils.miniapp(0);
        String appid = miniapp.getWxMaConfig().getAppid();
        String cacheName = "set_msg_jump_path" + appid;
        return cached.cache(useCache, CACHE_TAG_NAME, cacheName, uniqueKey -> setMsgJumpPath());
    }
}
