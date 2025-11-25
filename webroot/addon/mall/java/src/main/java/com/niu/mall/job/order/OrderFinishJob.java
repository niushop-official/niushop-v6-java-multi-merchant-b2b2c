package com.niu.mall.job.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.Map;

@JobProvider(key = "OrderFinishJob", name = "订单自动收货完成", describe = "订单自动收货完成", source = "mall")
public class OrderFinishJob extends AbstractJobProvider implements IJobProvider  {

    private MallOrderMapper mallOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }

    private static ICoreOrderFinishService coreOrderFinishService() {
        return SpringContext.bean(ICoreOrderFinishService.class);
    }

    private static ICoreAddonService coreAddonService() {
        return SpringContext.bean(ICoreAddonService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", OrderStatusEnum.WAIT_TAKE.getStatus());
        queryWrapper.gt("timeout", 0);
        queryWrapper.le("timeout", System.currentTimeMillis() / 1000);
        if (checkAddon("supply")){
            queryWrapper.eq("supply_id", 0);
        }
        queryWrapper.select("order_id,site_id");
        List<MallOrder> orderList = mallOrderMapper().selectList(queryWrapper);
        if (!orderList.isEmpty()) {
            for (MallOrder order: orderList) {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(order.getOrderId());
                finishParam.setSiteId(order.getSiteId());
                coreOrderFinishService().finish(finishParam);
            }
        }
    }

    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService().getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

}
