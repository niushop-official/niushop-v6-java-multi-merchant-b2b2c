package com.niu.core.upgrade.v003;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.mapper.goods.GoodsBrandMapper;

@UpgradeProvider(addon = "core", version = "0.0.3")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        handleBrandData();
    }

    //处理品牌数据
    public  void handleBrandData(){
        GoodsBrandMapper goodsBrandMapper = SpringContext.bean(GoodsBrandMapper.class);
        goodsBrandMapper.update(new GoodsBrand(),new LambdaUpdateWrapper<GoodsBrand>()
                .gt(GoodsBrand::getBrandId,0)
                .set(GoodsBrand::getAuditStatus,2)
                .set(GoodsBrand::getAuditTime,System.currentTimeMillis()/1000)
        );
    }

}
