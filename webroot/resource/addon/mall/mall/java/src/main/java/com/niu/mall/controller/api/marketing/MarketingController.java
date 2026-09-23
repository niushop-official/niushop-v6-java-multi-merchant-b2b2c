package com.niu.mall.controller.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.marketing.IFullGiftService;
import com.niu.mall.service.api.marketing.IFullReductionService;
import com.niu.mall.service.api.marketing.IMarketingService;
import com.niu.mall.service.api.marketing.param.MarketingDetailParam;
import com.niu.mall.service.api.marketing.param.MarketingGoodsParam;
import com.niu.mall.service.api.marketing.param.MarketingListParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 营销活动控制器
 * <p>
 * 提供营销活动的查询功能，包括满减、满赠活动的列表、详情、商品查询等
 * 用于前端用户查看营销活动信息
 */
@RestController
@RequestMapping("api/mall/marketing")
public class MarketingController {

    @Resource
    private IFullGiftService fullGiftService;

    @Resource
    private IFullReductionService fullReductionService;

    @Resource
    private IMarketingService marketingService;

    /**
     * 获取营销活动列表
     * <p>
     * 返回满减和满赠活动的列表，根据商品数据匹配适用的活动
     *
     * @param params 包含goods_data的参数
     * @return Result<Map < String, Object>> 营销活动列表
     */
    @PostMapping("/list")
    public Result<?> list(@Validated @RequestBody MarketingListParam params) {
        var result = marketingService.getList(params);
        return Result.success(result);
    }

    /**
     * 满减 / 满赠 / 限时折扣等活动详情（含按当前金额或件数计算的阶梯展示字段）。
     * <p>
     * 查询参数与参考接口一致：{@code price}（默认 0）、{@code num}（默认 0）、{@code market_ids}（活动类型 → 活动 id，
     * 如 {@code market_ids[full_reduction]=1}，与 uni-app GET 下 qs.stringify 一致）。
     *
     * @param param 由查询字符串绑定的参数对象
     * @return 各活动类型对应的详情 Map
     */
    @GetMapping("/detail")
    public Result<?> detail(@ModelAttribute MarketingDetailParam param) {
        return Result.success(marketingService.detail(param));
    }

    /**
     * 获取营销活动商品列表
     * <p>
     * 根据活动类型和活动ID获取参与活动的商品列表
     *
     * @param param     活动ID
     * @param pageParam 分页参数
     * @return Result<PageResult < Map < String, Object>>> 营销活动商品列表
     */
    @GetMapping("/{type}/{id}/goods")
    public Result<?> goodsPages(@Validated MarketingGoodsParam param, @Validated PageParam pageParam) {
        return Result.success(marketingService.goodsPage(pageParam, param));
    }

}