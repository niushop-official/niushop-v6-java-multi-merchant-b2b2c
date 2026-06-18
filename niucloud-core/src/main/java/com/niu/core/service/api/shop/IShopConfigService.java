package com.niu.core.service.api.shop;

import com.niu.core.service.core.shop.param.ShopApplyConfigParam;
import com.niu.core.service.core.shop.param.ShopCashOutConfigParam;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;

/**
 * 店铺提现设置
 */
public interface IShopConfigService {

    /**
     * 获取提现设置
     *
     * @return
     */
    ShopCashOutConfigVo getCashOutConfig();

    /**
     * 提现设置
     *
     * @param shopCashOutConfigParam
     */
    void setCashOutConfig(ShopCashOutConfigParam shopCashOutConfigParam);

    /**
     * 获取入驻设置
     *
     * @return
     */
    ShopApplyConfigInfoVo getShopApplyConfig();

    /**
     * 设置入驻(表单)设置
     *
     * @param param
     */
    void setShopApplyConfig(ShopApplyConfigParam param);
}
