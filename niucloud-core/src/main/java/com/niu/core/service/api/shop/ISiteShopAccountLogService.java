package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteShopAccountLogParam;
import com.niu.core.service.api.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.api.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopAccountLogListVo;

/**
 * 商家结算账单记录服务接口
 */
public interface ISiteShopAccountLogService {

    /**
     * 商家结算账单记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopAccountLogListVo>
     */
    PageResult<SiteShopAccountLogListVo> page(PageParam pageParam, SiteShopAccountLogSearchParam searchParam);

    /**
     * 商家结算账单记录详情
     * @param id 主键ID
     * @return  SiteShopAccountLogInfoVo
     */
    SiteShopAccountLogInfoVo info(Integer id);

    /**
     * 商家结算账单记录添加
     * @param addParam 添加参数
     */
    void add(SiteShopAccountLogParam addParam);

    /**
     * 商家结算账单记录编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopAccountLogParam editParam);

    /**
     * 商家结算账单记录删除
     * @param id 主键ID
     */
    void del(Integer id);

}
