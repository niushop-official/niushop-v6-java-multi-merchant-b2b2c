package com.niu.mall.service.admin.delivery.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.admin.delivery.IMallDeliveryElectronicSheetService;
import com.niu.mall.service.core.delivery.ICoreElectronicSheetService;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;


/**
 * 电子面单实现
 */
@Service
public class DeliveryElectronicSheetServiceImpl implements IMallDeliveryElectronicSheetService {


    @Resource
    ICoreElectronicSheetService coreElectronicSheetService;

    @Override
    public List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param) {
        param.setSiteId(RequestUtils.siteId());
        return coreElectronicSheetService.printElectronicSheet(param);
    }

}
