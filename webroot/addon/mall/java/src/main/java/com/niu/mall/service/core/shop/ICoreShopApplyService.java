package com.niu.mall.service.core.shop;


import com.niu.mall.service.core.shop.vo.ShopApplyConfigVo;

public interface ICoreShopApplyService {

    /**
     * 获取商户入驻设置
     */
    ShopApplyConfigVo getShopApplyConfig();

    /**
     * 设置商户入驻配置
     *
     * @param data 配置数据 (apply_title, apply_banner, form_id)
     */
    void setShopApplyConfig(ShopApplyConfigVo data);
}
