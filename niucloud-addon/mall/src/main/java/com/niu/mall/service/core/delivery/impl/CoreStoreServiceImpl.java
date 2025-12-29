package com.niu.mall.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.mall.service.core.delivery.ICoreStoreService;
import com.niu.core.service.core.delivery.param.LatlngParam;
import com.niu.core.service.core.delivery.vo.StoreListVo;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class CoreStoreServiceImpl implements ICoreStoreService {

    @Resource
    DeliveryStoreMapper deliveryStoreMapper;

    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeStore())) {
            vo.getError().add("请选择要自提的门店");
        }

        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = item.getGoods();
            if (goods.getGoodsType().equals(GoodsTypeEnum.REAL.getType())) {
                JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
                if (!deliveryType.contains(OrderDeliveryTypeEnum.STORE.getType())) {
                    item.setNotSupportDelivery(1);
                    vo.getError().add("该商品不支持门店自提");
                }
            }
        }
    }

    /**
     * 获取自提点列表
     *
     * @param siteId
     * @param latlng
     * @return
     */
    @Override
    public List<StoreListVo> getStoreList(Integer siteId, LatlngParam latlng) {
        List<StoreListVo> storeList = CollectUtils.convert(deliveryStoreMapper.selectList(new QueryWrapper<DeliveryStore>()
                .eq("site_id", siteId)), StoreListVo.class);
        if (ObjectUtil.isNotEmpty(latlng.getLat()) && latlng.getLat().compareTo(BigDecimal.ZERO) == 1 && ObjectUtil.isNotEmpty(latlng.getLng()) && latlng.getLng().compareTo(BigDecimal.ZERO) == 1) {
            for (StoreListVo item : storeList) {
                item.setDistance(DistanceCalculateUtils.calculateDistance(
                        latlng.getLat().doubleValue(),
                        latlng.getLng().doubleValue(),
                        Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())
                ));
            }
            storeList.sort(Comparator.comparing(StoreListVo::getDistance));
        }
        return storeList;
    }
}
