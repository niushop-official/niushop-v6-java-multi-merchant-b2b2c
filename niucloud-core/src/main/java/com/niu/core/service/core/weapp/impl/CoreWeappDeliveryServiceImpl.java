package com.niu.core.service.core.weapp.impl;

import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappService;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CoreWeappDeliveryServiceImpl implements ICoreWeappDeliveryService {

    @Resource
    private ICoreDeliveryWeappService coreDeliveryWeappService;

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    @Override
    public IsTradeManagedVo getIsTradeManaged() {
        return coreDeliveryWeappService.getIsTradeManaged();
    }
}
