package com.niu.mall.upgrade.v103;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderMapper;

import java.util.List;

@UpgradeProvider(addon = "mall", version = "1.0.3")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        //处理订单发货数据
        handleDeliveryData();
        //处理统计数据
        handleStatData();
    }

    //处理订单发货数据
    public void handleDeliveryData(){
        MallOrderDeliveryMapper mallOrderDeliveryMapper = SpringContext.bean(MallOrderDeliveryMapper.class);
        List<MallOrderDelivery> list = mallOrderDeliveryMapper.selectList(new LambdaQueryWrapper<MallOrderDelivery>()
                .gt(MallOrderDelivery::getSiteId,0));
        if (!list.isEmpty()){
            for (MallOrderDelivery orderDeliveryObj : list){
                if(StringUtils.isEmpty(orderDeliveryObj.getCode())){
                    String code = getTradeNoById(orderDeliveryObj.getId(),"mall");
                    orderDeliveryObj.setCode( code);
                    mallOrderDeliveryMapper.updateById(orderDeliveryObj);
                }

            }
        }
    }

    //处理统计数据
    public  void handleStatData(){
        SysStatMapper sysStatMapper = SpringContext.bean(SysStatMapper.class);
        MallOrderMapper mallOrderMapper = SpringContext.bean(MallOrderMapper.class);
        //查询数据
        List<SysStat> sysStatList = sysStatMapper.selectList(new LambdaQueryWrapper<SysStat>().eq(SysStat::getSiteId,0));
        for (SysStat sysStat : sysStatList){
            String startDateTime = sysStat.getDate() + " 00:00:00";
            String endDateTime = sysStat.getDate() + " 23:59:59";
            QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<MallOrder>()
                    .eq("status", OrderStatusEnum.FINISH.getStatus());
            QueryMapperUtils.buildByTime(queryWrapper, "finish_time", new String[]{startDateTime, endDateTime});
            queryWrapper.select("sum(mall_money) as mallMoney");
            //查询每天统计数量
            MallOrder mallOrder = mallOrderMapper.selectOne(queryWrapper);
            if (mallOrder != null && mallOrder.getMallMoney() != null){
                sysStat.setMallServiceMoney(mallOrder.getMallMoney());
            }
            sysStatMapper.updateById(sysStat);
        }
    }

    /**
     * 获取自适应的单号
     * @param deliveryId 配送ID
     * @param tradeType 交易类型 (mall/supply)
     * @return 拼接后的单号字符串
     */
    public String getTradeNoById(Integer deliveryId, String tradeType) {
        // 初始化返回的单号
        String no = "";
        // 检查配送ID的长度（转换为字符串后判断长度）
        if (deliveryId != null && String.valueOf(deliveryId).length() <= 6) {
            // 基础单号前缀
            no = "202512274180514";
            // 根据交易类型拼接不同后缀
            switch (tradeType) {
                case "mall":
                    no += "11";
                    break;
                case "supply":
                    no += "12";
                    break;
            }
            // 将deliveryId格式化为6位数字，不足补0
            no += String.format("%06d", deliveryId);
        }
        return no;
    }

}
