package com.niu.core.service.site.site;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.site.param.SiteAddressParam;
import com.niu.core.service.site.site.param.SiteAddressSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.site.site.vo.SiteAddressInfoVo;
import com.niu.core.service.site.site.vo.SiteAddressListVo;

import java.util.List;

/**
 * 商家地址库服务接口
 */
public interface ISiteAddressService {


    /**
     * 商家地址库列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopAddressListVo>
     */
    PageResult<SiteAddressListVo> page(PageParam pageParam, SiteAddressSearchParam searchParam);

    /**
     * 商家地址库列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopAddressListVo>
     */
    List<SiteAddressListVo> list(SiteAddressSearchParam searchParam);

    /**
     * 商家地址库详情
     *
     * @param id 主键ID
     * @return ShopAddressInfoVo
     */
    SiteAddressInfoVo info(Integer id);

    /**
     * 获取默认发货地址
     *
     * @return ShopAddressInfoVo
     */
    SiteAddressInfoVo defaultDelivery();

    /**
     * 商家地址库添加
     *
     * @param addParam 添加参数
     */
    void add(SiteAddressParam addParam);

    /**
     * 商家地址库编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteAddressParam editParam);

    /**
     * 商家地址库删除
     *
     * @param id 主键ID
     */
    void del(Integer id);


    /**
     * 退款地址列表
     *
     * @return
     */
    List<SiteAddressListVo> refundAddress();

}
