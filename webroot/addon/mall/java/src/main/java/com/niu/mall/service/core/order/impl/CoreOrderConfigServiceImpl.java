package com.niu.mall.service.core.order.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.mall.entity.order.config.*;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import com.niu.mall.service.core.order.vo.MallOrderCloseVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
class CoreOrderConfigServiceImpl implements ICoreOrderConfigService {

    @Resource
    ICoreConfigService coreConfigService;


    @Override
    public void setConfig(OrderConfigParam param) {
        Integer siteId = RequestUtils.defaultSiteId();
        
        JSONObject value = new JSONObject();
        value.set("order_close", JSONUtil.createObj()
            .set("is_close", param.getIsClose())
            .set("close_length", param.getCloseLength()));
        value.set("order_finish", JSONUtil.createObj()
            .set("is_finish", param.getIsFinish())
            .set("finish_length", param.getFinishLength()));
        value.set("delay_spear", JSONUtil.createObj()
            .set("is_delay_spear", param.getIsDelaySpear())
            .set("delay_days", param.getDelayDays()));
        coreConfigService.setConfig(siteId, "MALL_ORDER_CONFIG", value);
        
        JSONObject evaluateInfo = JSONUtil.createObj()
            .set("is_evaluate", param.getIsEvaluate())
            .set("evaluate_is_to_examine", param.getEvaluateIsToExamine())
            .set("is_self_evaluate", param.getIsSelfEvaluate())
            .set("is_auto_good_evaluate", param.getIsAutoGoodEvaluate())
            .set("good_evaluate_days", param.getGoodEvaluateDays());
        coreConfigService.setConfig(siteId, "MALL_GOODS_EVALUATE", evaluateInfo);
    }

    @Override
    public OrderConfigVo getConfig() {
        Integer siteId = RequestUtils.defaultSiteId();
        JSONObject data = coreConfigService.getConfigValue(siteId, "MALL_ORDER_CONFIG");
        
        OrderConfigVo vo = new OrderConfigVo();
        
        // 订单关闭配置
        MallOrderClose mallOrderClose = new MallOrderClose();
        if (ObjectUtil.isEmpty(data) || ObjectUtil.isEmpty(data.getJSONObject("order_close"))) {
            mallOrderClose.setIsClose(1);
            mallOrderClose.setIsCloseEr(true);
            mallOrderClose.setCloseLength(120);
        } else {
            JSONObject orderClose = data.getJSONObject("order_close");
            Integer isClose = orderClose.getInt("is_close", 1);
            mallOrderClose.setIsClose(isClose);
            mallOrderClose.setIsCloseEr(isClose == 1);
            mallOrderClose.setCloseLength(orderClose.getInt("close_length", 120));
        }
        vo.setCloseOrderInfo(mallOrderClose);

        // 订单完成配置
        MallOrderFinish mallOrderFinish = new MallOrderFinish();
        if (ObjectUtil.isEmpty(data) || ObjectUtil.isEmpty(data.getJSONObject("order_finish"))) {
            mallOrderFinish.setIsFinish(1);
            mallOrderFinish.setIsFinishEr( true);
            mallOrderFinish.setFinishLength(14);
        } else {
            JSONObject orderFinish = data.getJSONObject("order_finish");
            Integer isFinish = orderFinish.getInt("is_finish", 1);
            mallOrderFinish.setIsFinish(orderFinish.getInt("is_finish", 1));
            mallOrderFinish.setIsFinishEr(isFinish == 1);
            mallOrderFinish.setFinishLength(orderFinish.getInt("finish_length", 14));
        }
        vo.setConfirm(mallOrderFinish);

        // 延迟分账配置
        MallOrderDelaySpear mallOrderDelaySpear = new MallOrderDelaySpear();
        if (ObjectUtil.isEmpty(data) || ObjectUtil.isEmpty(data.getJSONObject("delay_spear"))) {
            mallOrderDelaySpear.setIsDelaySpear(0);
            mallOrderDelaySpear.setDelayDays(0);
        } else {
            JSONObject delaySpear = data.getJSONObject("delay_spear");
            mallOrderDelaySpear.setIsDelaySpear(delaySpear.getInt("is_delay_spear", 0));
            mallOrderDelaySpear.setDelayDays(delaySpear.getInt("delay_days", 0));
        }
        vo.setDelaySpear(mallOrderDelaySpear);

        // 评价配置（从独立的配置键读取）
        JSONObject evaluateData = coreConfigService.getConfigValue(siteId, "MALL_GOODS_EVALUATE");
        MallOrderEvaluate mallOrderEvaluate = new MallOrderEvaluate();
        if (ObjectUtil.isEmpty(evaluateData)) {
            mallOrderEvaluate.setIsEvaluate(1);
            mallOrderEvaluate.setEvaluateIsToExamine(0);
            mallOrderEvaluate.setEvaluateIsShow(0);
            mallOrderEvaluate.setIsSelfEvaluate(0);
            mallOrderEvaluate.setIsAutoGoodEvaluate(1);
            mallOrderEvaluate.setGoodEvaluateDays(7);
        } else {
            mallOrderEvaluate.setIsEvaluate(evaluateData.getInt("is_evaluate", 1));
            mallOrderEvaluate.setEvaluateIsToExamine(evaluateData.getInt("evaluate_is_to_examine", 0));
            mallOrderEvaluate.setEvaluateIsShow(evaluateData.getInt("is_evaluate_is_show", 0));
            mallOrderEvaluate.setIsSelfEvaluate(evaluateData.getInt("is_self_evaluate", 0));
            mallOrderEvaluate.setIsAutoGoodEvaluate(evaluateData.getInt("is_auto_good_evaluate", 1));
            mallOrderEvaluate.setGoodEvaluateDays(evaluateData.getInt("good_evaluate_days", 7));
        }
        vo.setEvaluate(mallOrderEvaluate);

        return vo;
    }

    @Override
    public MallOrderFinish orderConfirm() {
        JSONObject data = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        MallOrderFinish mallOrderFinish = new MallOrderFinish();
        
        if (ObjectUtil.isEmpty(data) || ObjectUtil.isEmpty(data.getJSONObject("order_finish"))) {
            mallOrderFinish.setIsFinish(1);
            mallOrderFinish.setFinishLength(14);
        } else {
            JSONObject orderFinish = data.getJSONObject("order_finish");
            mallOrderFinish.setIsFinish(orderFinish.getInt("is_finish", 1));
            mallOrderFinish.setFinishLength(orderFinish.getInt("finish_length", 14));
        }

        return mallOrderFinish;
    }

    @Override
    public MallOrderEvaluate getEvaluateConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_GOODS_EVALUATE");
        MallOrderEvaluate mallOrderEvaluate = new MallOrderEvaluate();
        
        if (ObjectUtil.isEmpty(config)) {
            mallOrderEvaluate.setIsEvaluate(1);
            mallOrderEvaluate.setEvaluateIsToExamine(0);
            mallOrderEvaluate.setIsSelfEvaluate(0);
            mallOrderEvaluate.setIsAutoGoodEvaluate(1);
            mallOrderEvaluate.setGoodEvaluateDays(7);
        } else {
            mallOrderEvaluate.setIsEvaluate(config.getInt("is_evaluate", 1));
            mallOrderEvaluate.setEvaluateIsToExamine(config.getInt("evaluate_is_to_examine", 0));
            mallOrderEvaluate.setIsSelfEvaluate(config.getInt("is_self_evaluate", 0));
            mallOrderEvaluate.setIsAutoGoodEvaluate(config.getInt("is_auto_good_evaluate", 1));
            mallOrderEvaluate.setGoodEvaluateDays(config.getInt("good_evaluate_days", 7));
        }

        return mallOrderEvaluate;
    }

    @Override
    public MallOrderCloseVo orderClose() {
        JSONObject data = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        
        if (ObjectUtil.isEmpty(data) || ObjectUtil.isEmpty(data.getJSONObject("order_close"))) {
            return new MallOrderCloseVo(true, 120);
        }

        JSONObject orderClose = data.getJSONObject("order_close");
        return new MallOrderCloseVo(orderClose.getBool("is_close"), orderClose.getInt("close_length"));
    }

    @Override
    public MallOrderDelaySpear orderDelaySpear() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        MallOrderDelaySpear delaySpear = new MallOrderDelaySpear();
        
        if (ObjectUtil.isEmpty(config) || ObjectUtil.isEmpty(config.getJSONObject("delay_spear"))) {
            delaySpear.setIsDelaySpear(0);
            delaySpear.setDelayDays(0);
        } else {
            JSONObject delaySpearData = config.getJSONObject("delay_spear");
            delaySpear.setIsDelaySpear(delaySpearData.getInt("is_delay_spear"));
            delaySpear.setDelayDays(delaySpearData.getInt("delay_days"));
        }
        
        return delaySpear;
    }
}
