package com.niu.mall.service.site.supply_goods.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.event.goods.MallSupplyGoodsDataInfoEventDefiner;
import com.niu.mall.service.site.supply_goods.IMallSupplyGoodsService;
import com.niu.mall.service.site.supply_goods.vo.MallSupplyGoodsInfoVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商品分类实现
 */
@Service
public class MallSupplyGoodsServiceImpl implements IMallSupplyGoodsService {

    @Override
    public MallSupplyGoodsInfoVo getInfo(Integer id) {
        MallSupplyGoodsInfoVo result = new MallSupplyGoodsInfoVo();
        MallSupplyGoodsDataInfoEventDefiner.MallSupplyGoodsDataInfoEvent event = new MallSupplyGoodsDataInfoEventDefiner.MallSupplyGoodsDataInfoEvent();
        event.setGoodsId(id);
        event.addAppSign("supply");
        List<MallSupplyGoodsDataInfoEventDefiner.MallSupplyGoodsDataResult> goodsList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if(!goodsList.isEmpty()){
            for (MallSupplyGoodsDataInfoEventDefiner.MallSupplyGoodsDataResult item : goodsList){
                if(item.getResult() != null){
                    result = item.getResult();
                }
            }
        }
        return result;
    }
}
