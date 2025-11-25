package com.niu.mall.service.admin.delivery;

import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;

import java.util.List;

/**
 * 电子面单服务接口
 */
public interface IMallDeliveryElectronicSheetService {

    List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param);
}
