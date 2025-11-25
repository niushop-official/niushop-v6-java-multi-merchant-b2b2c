package com.niu.core.service.core.shop;

import com.niu.core.service.core.shop.param.ShopCashOutConfigParam;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;

/**
 * 店铺提现设置
 */
public interface ICoreShopConfigService {

    /**
     * 获取提现设置
     * @return
     */
    ShopCashOutConfigVo getCashOutConfig();

    /**
     * 提现设置
     * @param shopCashOutConfigParam
     */
    void setCashOutConfig(ShopCashOutConfigParam shopCashOutConfigParam);
}
