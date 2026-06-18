package com.niu.mall.service.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.marketing.param.MarketingDetailParam;
import com.niu.mall.service.api.marketing.param.MarketingGoodsParam;
import com.niu.mall.service.api.marketing.param.MarketingListParam;

import java.util.List;

public interface IMarketingService {

    List<?> getList(MarketingListParam params);

    Object detail(MarketingDetailParam param);

    PageResult<?> goodsPage(PageParam pageParam, MarketingGoodsParam param);
}
