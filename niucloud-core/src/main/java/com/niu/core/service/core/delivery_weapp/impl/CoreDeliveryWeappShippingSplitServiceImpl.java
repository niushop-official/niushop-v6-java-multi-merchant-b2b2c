package com.niu.core.service.core.delivery_weapp.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShippingSplit;
import com.niu.core.entity.delivery_weapp.DeliveryWeappShippingSplitItem;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingSplitItemMapper;
import com.niu.core.mapper.delivery_weapp.DeliveryWeappShippingSplitMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingSplitService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingSpliteCreateParam;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingSplitItemVo;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingSplitVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 小程序发货信息服务层
 */
@Log4j2
@Service
public class CoreDeliveryWeappShippingSplitServiceImpl implements ICoreDeliveryWeappShippingSplitService {

    @Resource
    private DeliveryWeappShippingSplitMapper deliveryWeappShippingSplitMapper;
    @Resource
    private DeliveryWeappShippingSplitItemMapper deliveryWeappShippingSplitItemMapper;

    @Override
    public Integer create(WeappShippingSpliteCreateParam param) {
        WeappShippingSplitVo shippingVo = getShippingInfoByTrade(param.getSiteId(), param.getSplitTradeId(), param.getSplitTradeType());
        if(ObjectUtil.isNotEmpty(shippingVo)){
            return shippingVo.getId();
        }
        String isAllDelivered = ObjectUtil.isNotEmpty(param.getIsAllDelivered()) && param.getIsAllDelivered() ? "true" : "false";
        DeliveryWeappShippingSplit shippingSplit = new DeliveryWeappShippingSplit();
        shippingSplit.setTransactionId(param.getTransactionId());
        shippingSplit.setIsAllDelivered(isAllDelivered);
        shippingSplit.setSiteId(param.getSiteId());
        shippingSplit.setShippingId(param.getShippingId());
        shippingSplit.setTradeId(param.getTradeId());
        shippingSplit.setTradeType(param.getTradeType());
        shippingSplit.setTradeNo(param.getTradeNo());
        shippingSplit.setSplitTradeId(param.getSplitTradeId());
        shippingSplit.setSplitTradeType(param.getSplitTradeType());
        shippingSplit.setSplitTradeNo(param.getSplitTradeNo());
        deliveryWeappShippingSplitMapper.insert(shippingSplit);

        List<DeliveryWeappShippingSplitItem> splitItems = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(param.getShippinglist())){
            for (WeappUploadShippingParam.UploadShippingParam itemVo : param.getShippinglist()) {
                DeliveryWeappShippingSplitItem splitItem = new DeliveryWeappShippingSplitItem();
                splitItem.setSiteId(shippingSplit.getSiteId());
                splitItem.setSplitId(shippingSplit.getId());
                splitItem.setItemTradeId(shippingSplit.getSplitTradeId());
                splitItem.setTradeType(shippingSplit.getSplitTradeType());
                splitItem.setConsignorContact(itemVo.getContact().getConsignorContact());
                splitItem.setReceiverContact(itemVo.getContact().getReceiverContact());
                splitItems.add(splitItem);
            }
        }
        if (!splitItems.isEmpty()){
            deliveryWeappShippingSplitItemMapper.insert(splitItems);
        }
        return shippingSplit.getId();
    }

    @Override
    public WeappShippingSplitVo getShippingInfoByTrade(Integer siteId, Integer splitTradeId, String splitTradeType) {
        LambdaQueryWrapper<DeliveryWeappShippingSplit> selectOneQueryWrapper = new LambdaQueryWrapper<>();
        selectOneQueryWrapper.eq(DeliveryWeappShippingSplit::getSiteId, siteId);
        selectOneQueryWrapper.eq(DeliveryWeappShippingSplit::getSplitTradeId, splitTradeId);
        selectOneQueryWrapper.eq(DeliveryWeappShippingSplit::getSplitTradeType, splitTradeType);
        selectOneQueryWrapper.last("LIMIT 1");
        DeliveryWeappShippingSplit shippingInfoSplit = deliveryWeappShippingSplitMapper.selectOne(selectOneQueryWrapper);
        WeappShippingSplitVo shippingSplitVo = new WeappShippingSplitVo();
        if(ObjectUtil.isNotEmpty(shippingInfoSplit)){
            BeanUtils.copyProperties(shippingInfoSplit, shippingSplitVo);
            LambdaQueryWrapper<DeliveryWeappShippingSplitItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryWeappShippingSplitItem::getSplitId, shippingSplitVo.getId());
            List<WeappShippingSplitItemVo> splits = CollectUtils.convert(deliveryWeappShippingSplitItemMapper.selectList(wrapper), WeappShippingSplitItemVo.class);
            shippingSplitVo.setShippingItem(splits);
        }else{
            shippingSplitVo = null;
        }
        return shippingSplitVo;
    }
}
