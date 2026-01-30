package com.niu.core.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.core.goods.ICoreGoodsCategoryService;
import com.niu.core.service.core.goods.ICoreGoodsConfigService;
import com.niu.core.service.core.goods.param.GoodsConfigParam;
import com.niu.core.service.core.goods.vo.CoreGoodsConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoreGoodsConfigServiceImpl implements ICoreGoodsConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public void setConfig(GoodsConfigParam param) {
        JSONObject json = new JSONObject();
        json.set("is_verify_brand", param.getIsVerifyBrand());
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "GOODS_CONFIG", json);
    }

    @Override
    public CoreGoodsConfigVo getConfig() {
        CoreGoodsConfigVo configVo = new CoreGoodsConfigVo();
        CoreSysConfigVo config = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "GOODS_CONFIG");
        if (ObjectUtil.isNotNull(config.getValueJson())){
            configVo.setIsVerifyBrand(config.getValueJson().getInt("is_verify_brand",0));
        }else{
            configVo.setIsVerifyBrand(0);
        }
        return configVo;
    }
}
