package com.niu.core.service.site.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateParam;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateListVo;

import java.util.List;

/**
 * 运费模板服务接口
 */
public interface IDeliveryShippingTemplateService {


    /**
     * 运费模板分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    PageResult<DeliveryShippingTemplateListVo> page(PageParam pageParam, DeliveryShippingTemplateSearchParam searchParam);

    /**
     * 运费模板列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    List<DeliveryShippingTemplateListVo> list(DeliveryShippingTemplateSearchParam searchParam);

    /**
     * 运费模板详情
     * @param id 主键ID
     * @return ShopDeliveryShippingTemplateInfoVo
     */
    DeliveryShippingTemplateInfoVo info(Integer id);

    /**
     * 运费模板添加
     * @param addParam 添加参数
     */
    void add(DeliveryShippingTemplateParam addParam);

    /**
     * 运费模板编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DeliveryShippingTemplateParam editParam);

    /**
     * 运费模板删除
     * @param id 主键ID
     */
    void del(Integer id);

}
