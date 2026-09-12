package com.niu.mall.upgrade.v122;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.upgrade.v120.V120UpgradeHelper;

/**
 * 商城 v1.2.2 数据迁移（合并 PHP v1.2.1 + v1.2.2）。
 */
@UpgradeProvider(addon = "mall", version = "1.2.2")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        handleSeparAccountData();
        handleDiyData();
    }

    /**
     * order_trade 分账记录从 mall_order.order_from 回填 trade_scene。
     */
    public void handleSeparAccountData() {
        String separTable = V120UpgradeHelper.table("separ_account");
        String orderTable = V120UpgradeHelper.table("mall_order");
        String sql = "UPDATE `" + separTable + "` a "
                + "LEFT JOIN `" + orderTable + "` o ON a.trade_id = o.order_id "
                + "SET a.trade_scene = o.order_from "
                + "WHERE a.trade_type = 'order_trade' AND o.order_id IS NOT NULL";
        V120UpgradeHelper.execute(sql);
    }

    /**
     * 初始化默认店铺海报。
     */
    public void handleDiyData() {
        ICorePosterService corePosterService = SpringContext.bean(ICorePosterService.class);
        corePosterService.initDefaultPoster(0, "mall_shop", "mall");
    }
}
