package com.niu.core.service.api.recharge.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.recharge.RechargePackage;
import com.niu.core.enums.recharge.RechargePackageEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.recharge.RechargePackageMapper;
import com.niu.core.service.api.recharge.IRechargeService;
import com.niu.core.service.api.recharge.vo.RechargeListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.recharge.order.vo.RechargeGiftContentVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RechargeServiceImpl implements IRechargeService {

    @Resource
    private RechargePackageMapper rechargePackageMapper;

    @Override
    public List<RechargeListVo> list() {
        QueryWrapper<RechargePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.eq("status", RechargePackageEnum.ON.getStatus());
        List<RechargePackage> dataList = rechargePackageMapper.selectList(queryWrapper);
        List<RechargeListVo> voList = new ArrayList<>();
        for (RechargePackage recharge : dataList){
            RechargeListVo vo = new RechargeListVo();
            BeanUtils.copyProperties(recharge, vo);
            vo.setGiftContent(new ArrayList<>());
            JSONObject gift = StringUtils.isNotEmpty(recharge.getGiftJson()) ? JSONUtil.parseObj(recharge.getGiftJson()) : new JSONObject();
            for(Map.Entry<String, Object> entry : gift.entrySet()) {
                CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
                event.setEventName("RechargeGiftContent");
                event.setData(entry);
                event.setSiteId(RequestUtils.siteId());
                List<CommonEventDefiner.CommonEventResult> giftContent = EventAndSubscribeOfPublisher.publishAndCallback(event);
                for (CommonEventDefiner.CommonEventResult content : giftContent) {
                    if(ObjectUtil.isNotEmpty(content.getResult())) {
                        RechargeGiftContentVo contentVo = new RechargeGiftContentVo();
                        BeanUtils.copyProperties(content.getResult(), contentVo);
                        vo.getGiftContent().add(contentVo);
                    }
                }
            }
            voList.add( vo);
        }
        return voList;
    }
}
