package com.niu.mall.service.core.delivery;

import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;

import java.util.List;

public interface ICoreElectronicSheetService {
    List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param);
}
