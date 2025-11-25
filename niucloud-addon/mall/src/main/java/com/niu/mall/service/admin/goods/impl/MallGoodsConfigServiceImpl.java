package com.niu.mall.service.admin.goods.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.service.admin.goods.IMallGoodsConfigService;
import com.niu.mall.service.admin.goods.vo.MallGoodsConfigVo;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MallGoodsConfigServiceImpl implements IMallGoodsConfigService {

    @Resource
    ICoreGoodsConfigService coreConfigService;

    @Override
    public GoodsConfigVo getGoodsConfig() {
        return coreConfigService.getConfig();
    }

    @Override
    public void setGoodsConfig(JSONObject data) {
        coreConfigService.setConfig(data);
    }
}
