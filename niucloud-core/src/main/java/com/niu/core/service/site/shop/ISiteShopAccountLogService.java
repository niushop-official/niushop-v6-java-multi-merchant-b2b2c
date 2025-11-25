package com.niu.core.service.site.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogParam;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogListVo;

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

}
