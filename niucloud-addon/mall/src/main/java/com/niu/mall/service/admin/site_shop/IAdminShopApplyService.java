package com.niu.mall.service.admin.site_shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyPageQueryParam;
import com.niu.mall.service.admin.site_shop.param.AdminShopApplyVerifyParam;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyInfoVo;
import com.niu.mall.service.admin.site_shop.vo.AdminShopApplyPageVo;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;

import java.util.Map;

public interface IAdminShopApplyService {
    PageResult<AdminShopApplyPageVo> getPage(PageParam pageParam, AdminShopApplyPageQueryParam queryParam);

    AdminShopApplyInfoVo getInfo(Integer id);

    void verify(Integer id, AdminShopApplyVerifyParam param);

    void del(Integer id);

    Map<String, String> getStatusList();

    ShopApplyConfigVo getShopApplyConfig();

    void setShopApplyConfig(ShopApplyConfigVo param);

    void modifyOpenStatus(Integer applyId, Integer siteId);  // 供店铺添加时调用
}