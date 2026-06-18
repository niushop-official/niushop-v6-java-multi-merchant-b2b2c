package com.niu.mall.service.core.order.impl;

import cn.binarywang.wx.miniapp.bean.express.WxMaExpressDelivery;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappService;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingEventService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingParam;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderWeappDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序发货信息管理服务
 */
@Log4j2
@Service
public class CoreOrderWeappDeliveryServiceImpl implements ICoreOrderWeappDeliveryService {

    @Resource
    private MallOrderMapper mallOrderMapper;
    @Resource
    private MallOrderDeliveryMapper mallOrderDeliveryMapper;
    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;
    @Resource
    private SiteAddressMapper siteAddressMapper;
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private ICoreDeliveryWeappService coreDeliveryWeappService;
    @Resource
    private ICoreDeliveryWeappShippingEventService coreDeliveryWeappShippingEventService;

    @Resource
    private PayMapper payMapper;

    @Override
    public String orderShippingUploadShippingInfo(Integer orderId,Integer siteId) {
        MallOrder mallOrder = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id",orderId).eq("site_id", siteId));
        //订单不存在
        if (ObjectUtil.isEmpty(mallOrder)){
            return "订单不存在";
        }

        try {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", mallOrder.getOutTradeNo()));
            if (pay == null) return "支付信息不存在";
            List<String> payType = List.of(TransferTypeEnum.WECHATPAY.getKey(),"huifu_wechatpay");
            if (!payType.contains(pay.getType())) return "订单未使用微信支付";

            // 物流信息列表
            List<MallOrderDelivery> packageList = mallOrderDeliveryMapper.selectJoinList(MallOrderDelivery.class, new MPJQueryWrapper<MallOrderDelivery>()
                    .setAlias("d").innerJoin("?_delivery_company c ON d.express_company_id = c.company_id".replace("?_", GlobalConfig.tablePrefix))
                    .select("express_number,id,express_company_id,c.company_name AS name")
                    .eq("order_id", mallOrder.getOrderId())
            );
            log.info("物流信息列表  {}",packageList);
            if(packageList.isEmpty()){
                return "没有物流信息";
            }

            //获取物流模式
            Integer logisticsType = coreDeliveryWeappService.getLogisticsType(mallOrder.getDeliveryType(),packageList.size());
            log.info("获取物流模式  {}",logisticsType);
            //获取发货模式
            Integer deliveryMode = coreDeliveryWeappService.getDeliveryMode(packageList.size());
            log.info("获取发货模式  {}",deliveryMode);
            //获取物流公司
            List<WxMaExpressDelivery> expressCompanyList = new ArrayList<>();
            if(logisticsType == 1){
                expressCompanyList = coreDeliveryWeappService.getDeliveryListCache();
            }
            log.info("获取快递列表=========================================");
            List<WeappUploadShippingParam.UploadShippingParam> shippingList = new ArrayList<>();
            WeappUploadShippingParam.UploadShippingParam firstShippingInfo = new WeappUploadShippingParam.UploadShippingParam();
            //查询商家默认发货地址
            SiteAddress shopAddress = siteAddressMapper.selectOne(new QueryWrapper<SiteAddress>().eq("site_id", siteId).eq("is_default_delivery", 1).last("LIMIT 1"));
            log.info("查询商家默认发货地址=========================================");
            for (int i = 0; i < packageList.size(); i++) {
                if (i == 14){
                    break;
                }
                MallOrderDelivery v = packageList.get(i);
                // 物流公司编码，快递公司ID，参见「查询物流公司编码列表」，物流快递发货时必填， 示例值: DHL 字符字节限制: [1, 128]
                String expressCompany = "";
                // 物流单号，物流快递发货时必填，示例值: 323244567777 字符字节限制: [1, 128]
                String trackingNo = "";

                if (logisticsType == 1){
                    if (ObjectUtil.isNotEmpty(v.getExpressCompanyId()) &&  v.getExpressCompanyId() > 0 && !expressCompanyList.isEmpty()){
                        trackingNo = v.getExpressNumber();
                        List<WxMaExpressDelivery> expressCompanyListThis = expressCompanyList.stream().filter(v1 -> v1.getDeliveryId().equals(v.getExpressNo())).toList();
                        if (!expressCompanyListThis.isEmpty()){
                            expressCompany = expressCompanyListThis.get(0).getDeliveryName();
                        }
                        if(StringUtils.isEmpty(expressCompany)){
                            return "物流公司不支持，请更换其他物流公司";
                        }
                    }else{
                        expressCompany = "EMS";
                        trackingNo = "1111111111";
                    }
                }
                log.info("组装包裹信息=========================================");
                WeappUploadShippingParam.UploadShippingParam shippingListBean = new WeappUploadShippingParam.UploadShippingParam();
                shippingListBean.setTrackingNo(trackingNo);
                shippingListBean.setExpressCompany(expressCompany);
                shippingListBean.setItemDesc("");
                WeappUploadShippingParam.ContactInfo contact = new WeappUploadShippingParam.ContactInfo();
                contact.setReceiverContact("");
                contact.setConsignorContact("");
                shippingListBean.setContact(contact);

                List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("delivery_id", v.getId()));
                if (!orderGoods.isEmpty()){
                    StringBuilder itemDesc = new StringBuilder();
                    for (int k2 = 0; k2 < orderGoods.size(); k2++) {
                        MallOrderGoods v2 = orderGoods.get(k2);
                        // 兼容null值
                        String goodsName = v2.getGoodsName() == null ? "" : v2.getGoodsName();
                        String skuName = v2.getSkuName() == null ? "" : v2.getSkuName();
                        String goodsFullName = goodsName + skuName + "*";
                        // 截取前90字符
                        String subName = StringUtils.substring(goodsFullName, 0, 90);
                        // 拼接逗号分隔符
                        if (k2 > 0) {
                            itemDesc.append(",");
                        }
                        // 拼接最终片段
                        itemDesc.append(subName).append("*").append(v2.getNum());
                    }
                    shippingListBean.setItemDesc(itemDesc.toString());
                }
                // 寄件人联系方式
                if (ObjectUtil.isNotEmpty(shopAddress) && ObjectUtil.isNotEmpty(shopAddress.getMobile())){
                    // 2. 手机号脱敏处理
                    String consignorContact = "";
                    if (shopAddress.getMobile().length() == 11) {
                        consignorContact = shopAddress.getMobile().substring(0, 3) + "****" + shopAddress.getMobile().substring(7);
                    }
                    // 3. 赋值到目标对象，完成最终逻辑
                    shippingListBean.getContact().setConsignorContact(consignorContact);
                }
                // 收件人联系方式
                if (ObjectUtil.isNotEmpty(mallOrder.getTakerMobile())){
                    // 2. 手机号脱敏处理
                    String consignorContact = "";
                    if (mallOrder.getTakerMobile().length() == 11) {
                        consignorContact = mallOrder.getTakerMobile().substring(0, 3) + "****" + mallOrder.getTakerMobile().substring(7);
                    }
                    // 3. 赋值到目标对象，完成最终逻辑
                    shippingListBean.getContact().setReceiverContact(consignorContact);
                }
                shippingList.add(shippingListBean);

                if (i == 0){
                    firstShippingInfo = shippingListBean;
                }
            }

            if (deliveryMode == 1){
                if(shippingList.size()>1){
                    for (int i = 1; i < shippingList.size(); i++){
                        firstShippingInfo.setItemDesc(shippingList.get(i-1).getItemDesc() + ";" + shippingList.get(i).getItemDesc());
                    }
                }
                firstShippingInfo.setItemDesc(StringUtils.substring(firstShippingInfo.getItemDesc(), 0, 90));
                shippingList.clear();
                shippingList.add(firstShippingInfo);
            }
            //查询 会员信息
            Member member = memberMapper.selectById(mallOrder.getMemberId());
            WeappShippingParam weappShippingCreateParam = new WeappShippingParam();
            weappShippingCreateParam.setTransactionId(pay.getTradeNo());
            weappShippingCreateParam.setLogisticsType(logisticsType);
            weappShippingCreateParam.setDeliveryMode(deliveryMode);
            // 同城配送没有物流信息，只能传一个订单
            weappShippingCreateParam.setShippingList(shippingList);
            weappShippingCreateParam.setWeappOpenid(member.getWeappOpenid());
            weappShippingCreateParam.setIsAllDelivered(true);
            weappShippingCreateParam.setOrderNumberType(2);

            weappShippingCreateParam.setSiteId(siteId);
            weappShippingCreateParam.setTradeId(mallOrder.getGroupId());
            weappShippingCreateParam.setTradeType("mall_group");
            weappShippingCreateParam.setTradeNo(mallOrder.getGroupId().toString());
            weappShippingCreateParam.setSplitTradeId(mallOrder.getOrderId());
            weappShippingCreateParam.setSplitTradeType("mall");
            weappShippingCreateParam.setSplitTradeNo(mallOrder.getOrderNo());
            log.info("发货=========================================");
            coreDeliveryWeappShippingEventService.weappShipping(weappShippingCreateParam);
        } catch (Exception e) {
            String errorMsg = String.format("商城小程序订单发货失败报错: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg);
        }
        return "";
    }

    @Override
    public String orderShippingNotifyConfirmReceive(String outTradeNo, String deliveryType) {
        try {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", outTradeNo));
            if (pay == null) return "支付信息不存在";
            List<String> payType = List.of(TransferTypeEnum.WECHATPAY.getKey(),"huifu_wechatpay");
            if (!payType.contains(pay.getType())) return "订单未使用微信支付";

            if (!OrderDeliveryTypeEnum.EXPRESS.getType().equals(deliveryType)){
                return "只有物流订单才能进行提醒";
            }

            coreDeliveryWeappService.notifyConfirmReceive(pay.getTradeNo());
        }catch (Exception e){
            String errorMsg = String.format("商城小程序订单确认收货提醒失败: %s, File: %s, line: %d",
                    e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            log.error(errorMsg);
        }
        return  "";
    }
}

