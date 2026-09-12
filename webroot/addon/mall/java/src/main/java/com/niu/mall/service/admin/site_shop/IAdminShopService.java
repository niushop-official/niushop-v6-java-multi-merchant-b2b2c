package com.niu.mall.service.admin.site_shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopListQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopSelectQueryParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopListVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopPageVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopSelectVo;

import java.util.List;

public interface IAdminShopService {
    PageResult<AdminShopPageVo> getPage(PageParam pageParam, AdminShopPageQueryParam queryParam);

    List<AdminShopListVo> getList(AdminShopListQueryParam queryParam);

    AdminShopInfoVo getInfo(Integer siteId);

    Integer add(SiteAddParam param);

    void edit(Integer siteId, SiteEditParam param);

    PageResult<AdminShopSelectVo> getSelectPage(PageParam pageParam, AdminShopSelectQueryParam queryParam);
}