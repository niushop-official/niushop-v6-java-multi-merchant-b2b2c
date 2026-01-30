package com.niu.core.service.core.delivery_weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShipping;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShippingSplit;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingMapper;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingSplitMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingCreateParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingSplitVo;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 小程序发货信息服务层
 */
@Log4j2
@Service
public class CoreDeliveryWeappShippingServiceImpl implements ICoreDeliveryWeappShippingService {

    @Resource
    private DeliveryWeappShippingMapper deliveryWeappShippingMapper;
    @Resource
    private DeliveryWeappShippingSplitMapper deliveryWeappShippingSplitMapper;

    @Override
    public Integer create(WeappShippingCreateParam param) {
        WeappShippingVo shippingVo = getShippingInfoByTrade(param.getTradeId(), param.getTradeType());
        if(ObjectUtil.isNotEmpty(shippingVo)){
            JSONArray mergeSiteIds = ObjectUtil.isNotEmpty(shippingVo.getSiteIds()) ? JSONUtil.parseArray(shippingVo.getSiteIds()) : new JSONArray();
            if (!mergeSiteIds.contains(param.getSiteId().toString())) {
                mergeSiteIds.add(param.getSiteId().toString());
            }
            DeliveryWeappShipping shippingInfo = new DeliveryWeappShipping();
            shippingInfo.setId(shippingVo.getId());
            shippingInfo.setSiteIds(JSONUtil.toJsonStr(mergeSiteIds));
            deliveryWeappShippingMapper.updateById(shippingInfo);
        }
        WxMaService miniapp = WechatUtils.miniapp(0);
        String appid = miniapp.getWxMaConfig().getAppid();
        DeliveryWeappShipping shippingInfo = new DeliveryWeappShipping();
        shippingInfo.setOrderNumberType(param.getOrderNumberType());
        shippingInfo.setTransactionId(param.getTransactionId());
        shippingInfo.setLogisticsType(param.getLogisticsType());
        shippingInfo.setDeliveryMode(param.getDeliveryMode());
        shippingInfo.setOpenid(param.getWeappOpenid());
        shippingInfo.setAppid(appid);
        shippingInfo.setTradeId(param.getTradeId());
        shippingInfo.setTradeType(param.getTradeType());
        shippingInfo.setTradeNo(param.getTradeNo());
        shippingInfo.setCreateTime(System.currentTimeMillis()/1000);
        JSONArray mergeSiteIds = new JSONArray();
        if(ObjectUtil.isNotEmpty(param.getSiteId())){
            mergeSiteIds.add(param.getSiteId().toString());
        }
        shippingInfo.setSiteIds(JSONUtil.toJsonStr(mergeSiteIds));
        deliveryWeappShippingMapper.insert(shippingInfo);
        return shippingInfo.getId();
    }

    @Override
    public WeappShippingVo getShippingInfoById(List<String> siteId, Integer shippingId) {
        LambdaQueryWrapper<DeliveryWeappShipping> selectOneQueryWrapper = new LambdaQueryWrapper<>();
        if(!siteId.isEmpty()){
            selectOneQueryWrapper.and(wrapper -> {
                for (String item : siteId) {
                    // 拼接条件：site_ids LIKE '%"item"%'  ，循环内自动用 OR 连接
                    wrapper.or().like(DeliveryWeappShipping::getSiteIds, item);
                }
            });
        }
        selectOneQueryWrapper.eq(DeliveryWeappShipping::getId, shippingId);
        DeliveryWeappShipping shippingInfo = deliveryWeappShippingMapper.selectOne(selectOneQueryWrapper);
        WeappShippingVo shippingVo = new WeappShippingVo();
        if(ObjectUtil.isNotEmpty(shippingInfo)){
            BeanUtils.copyProperties(shippingInfo, shippingVo);
            LambdaQueryWrapper<DeliveryWeappShippingSplit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryWeappShippingSplit::getShippingId, shippingInfo.getId());
            List<WeappShippingSplitVo> splits = CollectUtils.convert(deliveryWeappShippingSplitMapper.selectList(wrapper), WeappShippingSplitVo.class);
            shippingVo.setShippingSplit(splits);
        }else {
            shippingVo = null;
        }
        return shippingVo;
    }

    @Override
    public WeappShippingVo getShippingInfoByTrade(Integer tradeId, String tradeType) {
        LambdaQueryWrapper<DeliveryWeappShipping> selectOneQueryWrapper = new LambdaQueryWrapper<>();
        selectOneQueryWrapper.eq(DeliveryWeappShipping::getTradeId, tradeId);
        selectOneQueryWrapper.eq(DeliveryWeappShipping::getTradeType, tradeType);
        selectOneQueryWrapper.last("LIMIT 1");
        DeliveryWeappShipping shippingInfo = deliveryWeappShippingMapper.selectOne(selectOneQueryWrapper);
        WeappShippingVo shippingVo = new WeappShippingVo();
        if(ObjectUtil.isNotEmpty(shippingInfo)){
            BeanUtils.copyProperties(shippingInfo, shippingVo);
            LambdaQueryWrapper<DeliveryWeappShippingSplit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryWeappShippingSplit::getShippingId, shippingInfo.getId());
            List<WeappShippingSplitVo> splits = CollectUtils.convert(deliveryWeappShippingSplitMapper.selectList(wrapper), WeappShippingSplitVo.class);
            shippingVo.setShippingSplit(splits);
        }else{
            shippingVo = null;
        }
        return shippingVo;
    }
}
