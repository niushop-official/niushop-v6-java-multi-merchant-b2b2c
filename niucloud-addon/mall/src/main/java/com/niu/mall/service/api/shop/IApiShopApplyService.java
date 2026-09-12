package com.niu.mall.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.site.apply.vo.AccountCheckVo;
import com.niu.mall.service.api.shop.param.ShopApplyAddParam;
import com.niu.mall.service.api.shop.param.ShopApplyListParam;
import com.niu.mall.service.api.shop.vo.ShopApplyInfoVo;
import com.niu.mall.service.api.shop.vo.ShopApplyPageVo;
import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;

import java.util.List;
import java.util.Map;

public interface IApiShopApplyService {
    PageResult<ShopApplyPageVo> getPage(PageParam pageParam, ShopApplyListParam param);

    ShopApplyInfoVo getInfo(Integer id);

    AccountCheckVo accountCheck(String account);

    Integer add(ShopApplyAddParam param);

    void edit(Integer id, ShopApplyAddParam param);

    List<Map<String, String>> getStatusList();

    ShopApplyConfigVo getShopApplyConfig();
}