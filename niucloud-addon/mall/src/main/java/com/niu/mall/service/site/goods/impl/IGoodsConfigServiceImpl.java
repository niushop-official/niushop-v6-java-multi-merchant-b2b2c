package com.niu.mall.service.site.goods.impl;

import cn.hutool.json.JSONObject;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.site.goods.IGoodsConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class IGoodsConfigServiceImpl implements IGoodsConfigService {

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

    @Override
    public GoodsConfigVo getConfig() {
        return coreGoodsConfigService.getConfig();
    }

    @Override
    public void setGoodsConfig(JSONObject data) {

    }
}
