package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.enums.recharge.RechargeOrderTypeEnum;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.recharge.config.ICoreRechargeConfigService;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;
import com.niu.core.service.core.recharge.order.ICoreOrderEventService;
import com.niu.core.service.core.recharge.order.ICoreOrderLogService;
import com.niu.core.service.core.recharge.order.param.AfterOrderCloseParam;
import com.niu.core.service.core.recharge.order.param.AfterOrderCreateParam;
import com.niu.core.service.core.recharge.order.param.AfterOrderPayParam;
import com.niu.core.service.core.recharge.order.param.RechargeOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoreOrderEventServiceImpl implements ICoreOrderEventService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;
    @Resource
    private ICoreRechargeConfigService rechargeConfigService;
    @Resource
    private ICoreOrderLogService coreOrderLogService;

    /**
     * 订单创建成功后 处理事件
     * @param param 订单创建参数
     */
    @Override
    @Async
    public void AfterRechargeOrderCreate(AfterOrderCreateParam param) {
        log.info("AfterRechargeOrderCreate - 订单创建成功后处理  开始处理");
        try {
            //添加超时时间
            RechargeConfigVo orderConfig = rechargeConfigService.getRechargeConfig();
            if(orderConfig.getCloseLength() != null && orderConfig.getCloseLength() > 0){
                rechargeOrderMapper.update(new RechargeOrder(), new LambdaUpdateWrapper<RechargeOrder>()
                        .eq(RechargeOrder::getOrderId,param.getOrderId())
                        .set(RechargeOrder::getTimeout, param.getTime()+orderConfig.getCloseLength()*60));
            }
            //发布日志
            RechargeOrderLogParam logParam = new RechargeOrderLogParam();
            logParam.setOrderId(param.getOrderId());
            logParam.setMainId(param.getMainId());
            logParam.setMainType(param.getMainType());
            logParam.setStatus(RechargeOrderStatusEnum.WAIT_PAY.getStatus());
            logParam.setType(RechargeOrderTypeEnum.ORDER_CREATE_ACTION.getType());
            logParam.setContent("");
            coreOrderLogService.add(logParam);
            log.info("AfterRechargeOrderCreate - 订单创建成功后处理  处理完成");
        }catch (Exception e){
            log.error("AfterRechargeOrderCreate - 订单创建成功后处理  处理失败");
        }
    }

    /**
     * 订单关闭成功后 处理事件
     * @param param 订单关闭参数
     */
    @Override
    @Async
    public void AfterRechargeOrderClose(AfterOrderCloseParam param) {
        log.info("AfterRechargeOrderClose - 订单关闭成功后处理  开始处理");
        try {
            String mainType = ObjectUtil.defaultIfNull(param.getMainType(),RechargeOrderLogEnum.SYSTEM.getType());
            Integer mainId = ObjectUtil.defaultIfNull(param.getMainId(),0);
            //发布日志
            RechargeOrderLogParam logParam = new RechargeOrderLogParam();
            logParam.setOrderId(param.getOrderId());
            logParam.setMainId(mainId);
            logParam.setMainType(mainType);
            logParam.setStatus(RechargeOrderStatusEnum.CLOSE.getStatus());
            logParam.setType(RechargeOrderTypeEnum.ORDER_CLOSE_ACTION.getType());
            logParam.setContent("");
            coreOrderLogService.add(logParam);
            log.info("AfterRechargeOrderClose - 订单关闭成功后处理  处理完成");
        }catch (Exception e){
            log.error("AfterRechargeOrderClose - 订单关闭成功后处理  处理失败");
        }
    }

    @Override
    @Async
    public void AfterRechargeOrderPay(AfterOrderPayParam param) {
        log.info("AfterRechargeOrderPay - 订单支付成功后处理  开始处理");
        try {
            String mainType = ObjectUtil.defaultIfNull(param.getMainType(),RechargeOrderLogEnum.MEMBER.getType());
            Integer mainId = ObjectUtil.defaultIfNull(param.getMainId(),param.getOrderData().getMemberId());
            //发布日志
            RechargeOrderLogParam logParam = new RechargeOrderLogParam();
            logParam.setOrderId(param.getOrderId());
            logParam.setMainId(mainId);
            logParam.setMainType(mainType);
            logParam.setStatus(RechargeOrderStatusEnum.FINISH.getStatus());
            logParam.setType(RechargeOrderTypeEnum.ORDER_PAY_ACTION.getType());
            logParam.setContent("");
            coreOrderLogService.add(logParam);
            log.info("AfterRechargeOrderPay - 订单支付成功后处理  处理完成");
        }catch (Exception e){
            log.error("AfterRechargeOrderPay - 订单支付成功后处理  处理失败");
        }
    }
}
