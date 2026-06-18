package com.niu.mall.service.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.marketing.param.MarketingListParam;

import java.util.List;
import java.util.Map;

/**
 * 满减服务接口
 */
public interface IFullReductionService {

    /**
     * 根据商品列表获取可参与的满减活动信息
     *
     * @param params      参数，包含 goods_data 和 member_id（可选）
     * @param isCheckRule 是否校验规则（默认 true）
     * @return 每个匹配商品的活动信息列表
     */
    List<Map<String, Object>> getFullReductionList(MarketingListParam params, boolean isCheckRule);

    /**
     * 满减活动详情
     *
     * @param id 活动ID
     * @return 详情 Map
     */
    Map<String, Object> detail(Integer id);

    /**
     * 获取活动关联的商品分页
     *
     * @param id 活动ID
     * @return 分页结果（数据为商品列表）
     */
    PageResult<Map<String, Object>> getGoodsPage(PageParam pageParam, Integer id);
}
