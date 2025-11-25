package com.niu.core.service.site.delivery.impl;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryTradePackage;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryTradePackageMapper;
import com.niu.core.service.site.delivery.IDeliveryPackageService;
import com.niu.core.service.site.delivery.param.TradePackageSearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryPackageListVo;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 配送包裹服务层
 */
@Service
public class DeliveryPackageServiceImpl implements IDeliveryPackageService {

    @Resource
    DeliveryTradePackageMapper deliveryTradePackageMapper;
    @Resource
    ICoreDeliverySearchService coreDeliverySearchService;

    @Override
    public List<DeliveryPackageListVo> getDeliveryPackageList(String tradeNo, String tradeType) {
        MPJQueryWrapper<DeliveryTradePackage> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("d")
                .innerJoin("?_delivery_company dm ON dm.company_id = d.express_company_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("d.id, d.trade_no, d.code,d.name, d.delivery_type, d.sub_delivery_type,d.express_company_id, d.express_number, d.create_time");
        queryWrapper.select("dm.company_id, dm.company_name, dm.express_no");
        queryWrapper.eq("d.site_id", RequestUtils.siteId());
        queryWrapper.eq("d.trade_no", tradeNo);
        List<DeliveryPackageListVo> list = deliveryTradePackageMapper.selectJoinList(DeliveryPackageListVo.class, queryWrapper);
        return list;
    }

    @Override
    public DeliveryPackageInfoVo getDeliveryPackage(TradePackageSearchParam data, String tradeType) {
        MPJQueryWrapper<DeliveryTradePackage> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("d")
                .innerJoin("?_delivery_company dm ON dm.company_id = d.express_company_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("d.id, d.trade_no,d.site_id, d.code,d.name, d.delivery_type, d.sub_delivery_type,d.express_company_id, d.express_number, d.local_deliver_id, d.status, d.create_time");
        queryWrapper.select("dm.company_id, dm.company_name, dm.express_no");
        queryWrapper.eq("d.code", data.getCode());
        queryWrapper.eq("d.trade_type", tradeType);
        queryWrapper.last("LIMIT 1");
        DeliveryPackageInfoVo info = deliveryTradePackageMapper.selectJoinOne(DeliveryPackageInfoVo.class, queryWrapper);
        if(info != null && OrderDeliveryEnum.EXPRESS.getValue().equals(info.getDeliveryType()) && !OrderDeliveryEnum.NONE_EXPRESS.getValue().equals(info.getSubDeliveryType())){
            info.setMobile(data.getMobile());
            //查询物流跟踪
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(info.getExpressNo());
            deliverySearchParam.setLogisticNo(info.getExpressNumber());
            deliverySearchParam.setMobile(info.getMobile());
            deliverySearchParam.setCompanyId(info.getCompanyId());
            info.setTraces(coreDeliverySearchService.deliverySearch(deliverySearchParam));
        }
        return info;
    }
}
