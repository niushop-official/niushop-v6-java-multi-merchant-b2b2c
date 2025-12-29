package com.niu.core.service.site.delivery;


import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.delivery.param.DeliveryStoreAddParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreListSearchParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreModifyParam;
import com.niu.core.service.site.delivery.param.DeliveryStoreSearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryStoreInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryStoreInitInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryStorePageVo;
import com.niu.core.service.site.delivery.vo.DeliveryStoreVo;

import java.util.List;

/**
 * 配送门店服务层
 */
public interface IDeliveryStoreService {

    /**
     * 获取配送门店列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    PageResult<DeliveryStorePageVo> page(PageParam pageParam, DeliveryStoreSearchParam searchParam);

    /**
     * 获取配送门店信息
     */
    DeliveryStoreInfoVo info(Integer storeId);

    DeliveryStoreInitInfoVo getInitInfo();

    /**
     * 添加配送门店
     * @param param $data
     * @return mixed
     */
    Integer add(DeliveryStoreAddParam param);

    /**
     * 配送门店编辑
     * @param param $data
     * @return mixed
     */
    void edit(Integer storeId, DeliveryStoreAddParam param);

    /**
     * 删除配送门店
     * @return mixed
     */
    void del(Integer storeId);

    /**
     * 添获取配送门店列表
     * @param param $data
     * @return mixed
     */
    List<DeliveryStoreVo> getList(DeliveryStoreListSearchParam param);

    /**
     * 修改配送门店状态
     * @param param $data
     * @return bool
     */
    void modifyStatus(DeliveryStoreModifyParam param);

}
