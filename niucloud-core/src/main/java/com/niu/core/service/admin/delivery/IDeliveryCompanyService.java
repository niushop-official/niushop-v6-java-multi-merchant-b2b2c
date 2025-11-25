package com.niu.core.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.service.admin.delivery.param.DeliveryCompanyParam;
import com.niu.core.service.admin.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyListVo;

import java.util.List;

/**
 * 物流公司服务接口
 */
public interface IDeliveryCompanyService {

    /**
     * 物流公司分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    PageResult<DeliveryCompanyListVo> page(PageParam pageParam, DeliveryCompanySearchParam searchParam);

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

    /**
     * 物流公司添加
     * @param addParam 添加参数
     */
    void add(DeliveryCompanyParam addParam);

    /**
     * 物流公司编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DeliveryCompanyParam editParam);

    /**
     * 物流公司删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 批量添加物流公司
     *
     * @param list
     */
    void insertAll(List<DeliveryCompany> list);
}
