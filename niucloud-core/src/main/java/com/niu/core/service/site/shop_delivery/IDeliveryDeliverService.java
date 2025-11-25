package com.niu.core.service.site.shop_delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverParam;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverSearchParam;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverInfoVo;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverListVo;

import java.util.List;

/**
 * 配送员服务接口
 */
public interface IDeliveryDeliverService {


    /**
     * 配送员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryDeliverListVo>
     */
    PageResult<DeliveryDeliverListVo> list(PageParam pageParam, DeliveryDeliverSearchParam searchParam);

    /**
     * 配送员列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryDeliverListVo>
     */
    List<DeliveryDeliverListVo> getList(DeliveryDeliverSearchParam searchParam);

    /**
     * 配送员详情
     * @param id 主键ID
     * @return ShopDeliveryDeliverInfoVo
     */
    DeliveryDeliverInfoVo info(Integer id);

    /**
     * 配送员添加
     * @param addParam 添加参数
     */
    void add(DeliveryDeliverParam addParam);

    /**
     * 配送员编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DeliveryDeliverParam editParam);

    /**
     * 配送员删除
     * @param id 主键ID
     */
    void del(Integer id);

}
