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
    public void setConfig(OrderConfigParam orderConfigParam) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG", JSONUtil.parseObj(orderConfigParam));

    }

    @Override
    public OrderConfigVo getConfig() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        OrderConfigParam orderConfigParam = new OrderConfigParam();
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            orderConfigParam = JSONUtil.toBean(coreSysConfigVo.getValueJson(), OrderConfigParam.class);
        }

        OrderConfigVo vo = new OrderConfigVo();
        //订单关闭配置
        MallOrderClose mallOrderClose = new MallOrderClose();
        mallOrderClose.setIsCloseEr("1".equals(orderConfigParam.getIsClose()) || "true".equals(orderConfigParam.getIsClose()));
        mallOrderClose.setIsClose(mallOrderClose.getIsCloseEr() ? 1 : 2);
        mallOrderClose.setCloseLength(orderConfigParam.getCloseLength());
        vo.setCloseOrderInfo(mallOrderClose);

        //订单完成配置
        MallOrderFinish mallOrderFinish = new MallOrderFinish();
        mallOrderFinish.setIsFinishEr("1".equals(orderConfigParam.getIsFinish()) || "true".equals(orderConfigParam.getIsFinish()));
        mallOrderFinish.setIsFinish(mallOrderFinish.getIsFinishEr() ? 1 : 2);
        mallOrderFinish.setFinishLength(orderConfigParam.getFinishLength());
        vo.setConfirm(mallOrderFinish);

        //订单退款配置
        MallOrderRefund mallOrderRefund = new MallOrderRefund();
        mallOrderRefund.setNoAllowRefund("1".equals(orderConfigParam.getNoAllowRefund()) || "true".equals(orderConfigParam.getNoAllowRefund()));
        mallOrderRefund.setRefundLength(orderConfigParam.getRefundLength());
        vo.setRefund(mallOrderRefund);

        //订单评价配置
        MallOrderEvaluate mallOrderEvaluate = new MallOrderEvaluate();
        mallOrderEvaluate.setIsEvaluate(orderConfigParam.getIsEvaluate());
        mallOrderEvaluate.setEvaluateIsToExamine(orderConfigParam.getEvaluateIsToExamine());
        mallOrderEvaluate.setEvaluateIsShow(orderConfigParam.getEvaluateIsShow());
        mallOrderEvaluate.setIsSelfEvaluate(orderConfigParam.getIsSelfEvaluate());
        vo.setEvaluate(mallOrderEvaluate);

        //自定义表单
        vo.setFormId(orderConfigParam.getFormId());
        return vo;
    }

    @Override
    public MallOrderFinish orderConfirm() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        MallOrderFinish mallOrderFinish = new MallOrderFinish();
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            mallOrderFinish = JSONUtil.toBean(coreSysConfigVo.getValueJson(), MallOrderFinish.class);
        }

        return mallOrderFinish;
    }

    @Override
    public MallOrderEvaluate getEvaluateConfig() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        MallOrderEvaluate mallOrderEvaluate = new MallOrderEvaluate();
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            mallOrderEvaluate = JSONUtil.toBean(coreSysConfigVo.getValueJson(), MallOrderEvaluate.class);
        }

        return mallOrderEvaluate;
    }

    @Override
    public MallOrderCloseVo orderClose() {
        JSONObject data = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "MALL_ORDER_CONFIG");
        if (ObjectUtil.isEmpty(data)) {
            return new MallOrderCloseVo(true, 20);
        }

        JSONObject orderClose = JSONUtil.parseObj(data.get("order_close"));
        return new MallOrderCloseVo(orderClose.getBool("is_close"), orderClose.getInt("close_length"));
    }
}
