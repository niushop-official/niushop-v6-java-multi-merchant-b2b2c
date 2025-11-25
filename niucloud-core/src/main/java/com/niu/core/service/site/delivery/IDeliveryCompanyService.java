package com.niu.core.service.site.delivery;

import com.niu.core.service.site.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyListVo;

import java.util.List;

/**
 * 物流公司服务接口
 */
public interface IDeliveryCompanyService {

    /**
     * 物流公司列表
     * @param searchParam
     * @return
     */
    List<DeliveryCompanyListVo> list(DeliveryCompanySearchParam searchParam);

    /**
     * 详情
     * @param id 主键ID
     * @return ShopDeliveryCompanyInfoVo
     */
    DeliveryCompanyInfoVo info(Integer id);

}
