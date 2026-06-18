package com.niu.mall.service.core.goods;

import java.util.List;
import java.util.Map;

public interface ICoreGoodsPartyService {

    Map<String, Object> importGoods(List<Map<String, Object>> params, String goodsType, Integer siteId);

    public Map<String, Object> collect(List<Map<String, Object>> params, Integer siteId);
}
