package com.niu.core.service.admin.pay.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.pay.PayChannelMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.pay.IPayChannelService;
import com.niu.core.service.admin.pay.param.PayChannelAllSetParam;
import com.niu.core.service.admin.pay.vo.PayChannelListVo;
import com.niu.core.service.admin.pay.vo.PayChanneltemVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 支付渠道配置实现
 */
@Service
public class PayChannelServiceImpl implements IPayChannelService {
        
    @Resource
    PayChannelMapper payChannelMapper;

    @Resource
    PayMapper payMapper;

    /**
     * 支付渠道配置列表
     * @return PageResult<PayChannelListVo>
     */
    @Override
    public Map<String, PayChanneltemVo> list() {
        List<PayChannel> channelList = payChannelMapper.selectList(new QueryWrapper<PayChannel>());

        JSONObject payChannelList = new JSONObject();
        for (PayChannel item : channelList) {
            JSONObject payTypeList = payChannelList.getJSONObject(item.getChannel());
            if (payTypeList != null) {
                payTypeList.set(item.getType(), item);
            } else {
                payTypeList = new JSONObject();
                payTypeList.set(item.getType(),  JSONUtil.parse(item));
            }
            payChannelList.set(item.getChannel(), payTypeList);
        }

        JSONObject payTypeEnum = PayTypeEnum.getType();

        Map<String, PayChanneltemVo> list = new HashMap<String, PayChanneltemVo>();

        for (ChannelEnum item: ChannelEnum.values()) {
            PayChanneltemVo vo = new PayChanneltemVo();
            vo.setKey(item.getCode());
            vo.setName(item.getName());

            JSONArray payType = new JSONArray();
            for (String key: payTypeEnum.keySet()) {
                JSONObject type = JSONUtil.parseObj(payTypeEnum.getJSONObject(key).toString());
                if (payChannelList.getJSONObject(item.getCode()) == null || payChannelList.getJSONObject(item.getCode()).getJSONObject(key) == null) {
                    type.set("status", 0);
                    type.set("config", new JSONObject().set("name", ""));
                    type.set("sort", 0);
                } else {
                    JSONObject config = payChannelList.getJSONObject(item.getCode()).getJSONObject(key);
                    type.set("status", config.getInt("status"));
                    try {
                        type.set("config", JSONUtil.parseObj(config.getStr("config")));
                    } catch (Exception e) {
                        type.set("config", new JSONObject().set("name", ""));
                    }
                    type.set("sort", config.getInt("sort"));
                }
                payType.add(type);
            }

            // 支付方式排序
            List<JSONObject> payTypeList = new ArrayList<>();
            for (int i = 0; i < payType.size(); i++) {
                payTypeList.add(payType.getJSONObject(i));
            }
            Comparator<JSONObject> comparator = (o1, o2) -> {
                int sort1 = o1.getInt("sort");
                int sort2 = o2.getInt("sort");
                return Integer.compare(sort1, sort2);
            };
            Collections.sort(payTypeList, comparator);
            payType = new JSONArray(payTypeList);

            vo.setPayType(payType);
            list.put(item.getCode(), vo);
        }
        return list;
    }

    /**
     * 多渠道设置
     * @param param
     */
    public void setAll(PayChannelAllSetParam param) {
        for (String channelKey: param.getConfig().keySet()) {
            JSONObject channel = param.getConfig().getJSONObject(channelKey);
            JSONArray payTypeList = channel.getJSONArray("pay_type");
            for (int i = 0; i < payTypeList.size(); i++) {
                JSONObject payType = payTypeList.getJSONObject(i);
                set(channel.getStr("key"), payType.getStr("key"), payType);
            }
        }
    }

    /**
     * 设置
     * @param channel
     * @param type
     * @param data
     */
    public void set(String channel, String type, JSONObject data) {
        PayChannel payChannel = payChannelMapper.selectOne(new QueryWrapper<PayChannel>()
                .eq("channel", channel)
                .eq("type", type)
        );
        if (ObjectUtil.isNotEmpty(payChannel)) {
            payChannel.setConfig(data.getJSONObject("config").toString());
            payChannel.setSort(data.getInt("sort"));
            payChannel.setStatus(data.getInt("status"));
            payChannelMapper.updateById(payChannel);
        } else {
            PayChannel model = new PayChannel();
            model.setChannel(channel);
            model.setType(type);
            model.setConfig(data.getJSONObject("config").toString());
            model.setSort(data.getInt("sort"));
            model.setStatus(data.getInt("status"));
            payChannelMapper.insert(model);
        }
    }

    /**
     * 通过渠道获取配置
     * @param channel
     * @return
     */
    public List<PayChannelListVo> getListByChannel(String channel) {
        List<PayChannel> payChannel = payChannelMapper.selectList(new QueryWrapper<PayChannel>()
                .eq("channel", channel)
        );

        List<PayChannelListVo> list = new LinkedList<>();
        for (PayChannel item: payChannel) {
            PayChannelListVo vo = new PayChannelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }

        return list;
    }

    /**
     * 设置转账设置
     * @param param
     */
    public void setTransfer(JSONObject param) {
        JSONObject wechatpayConfig = param.getJSONObject("wechatpay_config");
        JSONObject alipayConfig = param.getJSONObject("alipay_config");

        if (wechatpayConfig != null) {
            this.set("transfer", "wechatpay", new JSONObject()
                    .set("config", wechatpayConfig)
                    .set("status", 1)
                    .set("sort", 0));
        }

        if (alipayConfig != null) {
            this.set("transfer", "alipay", new JSONObject()
                    .set("config", alipayConfig)
                    .set("status", 1)
                    .set("sort", 0));
        }
    }

    @Override
    public Map<String, BigDecimal> statType() {
        //根据支付方式统计支付金额
        Map<String, BigDecimal> resultMap = new LinkedHashMap<>();
        JSONObject type = PayTypeEnum.getType();
        Iterator<Map.Entry<String, Object>> iterator = type.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            if ("friendspay".equals(key)) {
                iterator.remove();
                continue;//
            }
            resultMap.put(key, null);
        }
        QueryWrapper<Pay> sumQueryWrapper = new QueryWrapper<>();
        sumQueryWrapper.eq("status", 2);
        sumQueryWrapper.select("type", "SUM(money) AS totalMoney")
                .groupBy("type");
        List<Map<String, Object>> result = payMapper.selectMaps(sumQueryWrapper);
        for (Map<String, Object> map : result) {
            String type1 = (String) map.get("type");
            BigDecimal money = (BigDecimal) map.get("totalMoney");
            if (type1 != null && !type1.trim().isEmpty()) {
                // 如果 map 中已有该 key，则直接更新其 value
                resultMap.put(type1, money);
            }
        }
        return resultMap;
    }

    @Override
    public   Map<String,String>  channelList() {
        List<PayChannel> channelList = payChannelMapper.selectList(new QueryWrapper<PayChannel>());
        List<String> list = channelList.stream().distinct().map(k -> k.getChannel()).filter(channel ->!"transfer".equals(channel)).collect(Collectors.toSet()).stream().toList();
        Map<String,String> map=new HashMap<>(list.size());
        if (ObjectUtil.isNotEmpty(list)){
            for (String s : list) {
                String name = PayChannelEnum.getNameByStatus(s);
                map.put(s,name);
            }
        }
        return map;
    }

}
