package com.niu.core.service.core.pay.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.pay.PayTypeByTradeEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.pay.PayChannelMapper;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.param.PayChannelSearchParam;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 支付渠道配置实现
 */
@Service
public class CorePayChannelServiceImpl implements ICorePayChannelService {

    @Resource
    PayChannelMapper payChannelMapper;

    @Resource
    ICoreWechatConfigService coreWechatConfigService;

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    ICorePayService corePayService;

    @Resource
    public void setCorePayService(@Lazy ICorePayService corePayService) {
        this.corePayService = corePayService;
    }

    @Override
    public PayChannel find(Integer siteId, String type, String channel) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper();
        queryWrapper.select("config");
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("type", type);
        queryWrapper.eq("channel", channel);
        queryWrapper.eq("status", 1);
        return payChannelMapper.selectOne(queryWrapper);
    }

    @Override
    public PayChannel find(Integer siteId, PayChannelSearchParam payChannelSearchParam) {
        return null;
    }

    @Override
    public List<PayTypeVo> getAllowPayTypeByChannel(String channel, String tradeType) {
        List<PayTypeVo> list = new LinkedList<>();

        List<PayChannel> payChannelList = payChannelMapper.selectList(new QueryWrapper<PayChannel>()
                .select("site_id, type, config,channel")
                .eq("status", 1)
                .eq("channel", channel));
        List<String> typeList = new ArrayList<>();
        for (PayChannel item : payChannelList) {
            if (item.getConfig().isEmpty()) {
                continue;
            }
            String payTypeKey = item.getType();
            if (!isPayTypeSupportedOnChannel(payTypeKey, channel)) {
                continue;
            }
            if (!PayTypeByTradeEnum.isPayTypeAllowed(payTypeKey, tradeType)) {
                continue;
            }
            PayTypeVo type = corePayService.driver(channel, payTypeKey).filterPayTypeByTradeType(tradeType);
            if (type != null) {
                type.setConfig(item.getConfig());
                list.add(type);
            }
            if (type == null) {
                typeList.add(payTypeKey);
            }
        }
        if (list.isEmpty()){
            throw new IllegalArgumentException("未找到对应的支付类型: channel=" + channel + ", type=" + String.join(", ", typeList) + ", tradeType=" + tradeType);
        }
        return list;
    }

    @Override
    public List<PayTypeVo> getAllowPayTypeByChannel(String channel) {
        return getAllowPayTypeByChannel(channel, "");
    }

    @Override
    public JSONObject getConfigByChannelAndType(String channel, String type) {
        PayChannel payChannel = payChannelMapper.selectOne(new QueryWrapper<PayChannel>()
                .select("config")
                .eq("channel", channel)
                .eq("status", 1)
                .eq("type", type));

        if (payChannel == null || payChannel.getConfig().isEmpty() || "[]".equals(payChannel.getConfig().trim()))
            return null;

        JSONObject config = JSONUtil.parseObj(payChannel.getConfig());

        if (type.equals("wechatpay")) {
            WechatConfigVo wechatConfig = coreWechatConfigService.getWechatConfig();
            WeappConfigVo weappConfig = coreWeappConfigService.getWeappConfig();
            config.set("mp_app_id", ObjectUtil.defaultIfNull(wechatConfig.getAppId(), ""));
            config.set("mini_app_id", ObjectUtil.defaultIfNull(weappConfig.getAppId(), ""));
        }

        return config;
    }

    /**
     * 支付方式是否支持当前渠道（对齐 PHP not_support_channel）
     */
    private boolean isPayTypeSupportedOnChannel(String payTypeKey, String channel) {
        if (StrUtil.isBlank(payTypeKey) || StrUtil.isBlank(channel)) {
            return true;
        }
        JSONObject payTypeConfig = PayTypeEnum.getType().getJSONObject(payTypeKey);
        if (payTypeConfig == null || payTypeConfig.isEmpty()) {
            return true;
        }
        JSONArray notSupportChannel = payTypeConfig.getJSONArray("not_support_channel");
        if (notSupportChannel == null || notSupportChannel.isEmpty()) {
            return true;
        }
        return !notSupportChannel.contains(channel);
    }
}
