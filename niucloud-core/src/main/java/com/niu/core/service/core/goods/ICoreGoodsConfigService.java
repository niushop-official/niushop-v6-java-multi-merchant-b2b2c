package com.niu.core.service.core.goods;

import com.niu.core.service.core.goods.param.GoodsConfigParam;
import com.niu.core.service.core.goods.vo.CoreGoodsConfigVo;

import java.util.List;

/**
 * 商品配置服务接口
 */
public interface ICoreGoodsConfigService {

    /**
     * 获取商品配置
     * @return
     */
    void setConfig(GoodsConfigParam param);

    /**
     * 设置商品配置
     * @return
     */
    CoreGoodsConfigVo getConfig();

}
