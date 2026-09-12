package com.niu.mall.service.api.marketing.impl;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.enums.fullactive.FullGiftRuleTypeEnum;
import com.niu.core.enums.fullactive.FullReductionPromotionTypeEnum;
import com.niu.core.enums.fullactive.FullReductionRuleTypeEnum;
import com.niu.core.enums.fullactive.FullReductionTypeEnum;
import com.niu.mall.service.api.marketing.IFullGiftService;
import com.niu.mall.service.api.marketing.IFullReductionService;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.IMarketingService;
import com.niu.mall.service.api.marketing.param.MarketingDetailParam;
import com.niu.mall.service.api.marketing.param.MarketingGoodsParam;
import com.niu.mall.service.api.marketing.param.MarketingListParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 满减服务实现
 */
@Service
public class MarketingServiceImpl implements IMarketingService {

    @Resource
    private IFullGiftService fullGiftService;
    @Resource
    private IFullReductionService fullReductionService;
    @Resource
    private IMallDiscountService discountService;


    /**
     * 获取营销活动列表（按类型分组）
     *
     * @param params 查询参数（如 site_id, 分页等）
     * @return 活动列表分组
     */
    @Override
    public List<?> getList(MarketingListParam params) {
        // 获取满赠活动列表（不分页）
        List<Map<String, Object>> giftList = fullGiftService.getFullGiftList(params, false);
        // 获取满减活动列表（不分页）
        List<Map<String, Object>> reductionList = fullReductionService.getFullReductionList(params, false);

        List<Map<String, Object>> result = new ArrayList<>();
        if (giftList != null && !giftList.isEmpty()) {
            Map<String, Object> giftGroup = new HashMap<>();
            giftGroup.put("title", CommonActiveEnum.FULL_GIFT.getName());
            giftGroup.put("type", CommonActiveEnum.FULL_GIFT.getType());
            giftGroup.put("active_list", giftList);
            result.add(giftGroup);
        }
        if (reductionList != null && !reductionList.isEmpty()) {
            Map<String, Object> reductionGroup = new HashMap<>();
            reductionGroup.put("title", CommonActiveEnum.FULL_REDUCTION.getName());
            reductionGroup.put("type", CommonActiveEnum.FULL_REDUCTION.getType());
            reductionGroup.put("active_list", reductionList);
            result.add(reductionGroup);
        }
        return result;
    }

    /**
     * 获取多个营销活动的详情（用于购物车/下单前计算）
     *
     * @param param 包含 market_ids (Map<类型, 活动ID>), price, num
     * @return 每个活动详情（含动态计算字段）
     */
    @Override
    public Object detail(MarketingDetailParam param) {
        // 是否需要进行阶梯计算（当商品总价或总数量为0时，不需要计算优惠金额）
        boolean needCalculate = true;
        BigDecimal price = param.getPrice() != null ? param.getPrice() : BigDecimal.ZERO;
        BigDecimal num = param.getNum() != null ? new BigDecimal(param.getNum()) : BigDecimal.ZERO;
        if (price.compareTo(BigDecimal.ZERO) == 0 && num.compareTo(BigDecimal.ZERO) == 0) {
            needCalculate = false;
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> marketIds = param.getMarket_ids();
        if (marketIds == null || marketIds.isEmpty()) {
            return result;
        }

        for (Map.Entry<String, Integer> entry : marketIds.entrySet()) {
            String marketType = entry.getKey();
            Integer marketId = entry.getValue();
            Map<String, Object> detail = null;

            switch (marketType) {
                case "full_reduction" -> {
                    detail = fullReductionService.detail(marketId);
                    if (detail != null) {
                        // 格式化结束时间
                        Object endTimeObj = detail.get("end_time");
                        if (endTimeObj != null) {
                            String formatted = formatTimestamp(endTimeObj);
                            detail.put("end_time", formatted);
                        }
                        if (needCalculate) {
                            // 获取规则JSON（按level排序）
                            List<Map<String, Object>> ruleJson = (List<Map<String, Object>>) detail.get("rule_json");
                            if (ruleJson != null && !ruleJson.isEmpty()) {
                                // 按 level 排序
                                ruleJson.sort(Comparator.comparingInt(r -> (Integer) r.get("level")));
                                String type = (String) detail.get("type");
                                String ruleType = (String) detail.get("rule_type");
                                String promotionType = (String) detail.get("promotion_type");

                                BigDecimal ruleUnit;
                                if (FullReductionTypeEnum.NUM.getType().equals(type)) {
                                    ruleUnit = num;
                                } else {
                                    ruleUnit = price.multiply(num);
                                }

                                if (FullReductionRuleTypeEnum.CYCLE.getType().equals(ruleType)) {
                                    // 循环档位（只取第一档）
                                    Map<String, Object> first = ruleJson.get(0);
                                    first.put("is_show", 1);
                                    BigDecimal amount = new BigDecimal(first.get("amount").toString());
                                    first.put("is_enough", amount.compareTo(ruleUnit) <= 0 ? 1 : 0);
                                    BigDecimal reduce = new BigDecimal(first.get("reduce").toString());
                                    // 计算抵扣金额：floor(总金额/门槛) × 减免额
                                    long cycles = ruleUnit.divide(amount, 0, RoundingMode.DOWN).longValue();
                                    BigDecimal deductPrice = BigDecimal.valueOf(cycles).multiply(reduce)
                                            .setScale(2, RoundingMode.DOWN);
                                    first.put("deduct_price", deductPrice);
                                } else {
                                    // 累进档位
                                    for (int i = 0; i < ruleJson.size(); i++) {
                                        Map<String, Object> rule = ruleJson.get(i);
                                        rule.put("is_show", 0);
                                        rule.put("is_enough", 0);
                                        rule.put("deduct_price", BigDecimal.ZERO);
                                        BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                                        if (i == 0) {
                                            rule.put("is_show", 1);
                                        }
                                        if (ruleUnit.compareTo(amount) >= 0) {
                                            rule.put("is_show", 1);
                                            if (i > 0) {
                                                ruleJson.get(i - 1).put("is_show", 0);
                                            }
                                            rule.put("is_enough", 1);
                                            if (i > 0) {
                                                ruleJson.get(i - 1).put("is_enough", 0);
                                            }
                                        }
                                        // 如果当前档位满足，计算抵扣金额
                                        if ((int) rule.get("is_enough") == 1) {
                                            BigDecimal reduce = new BigDecimal(rule.get("reduce").toString());
                                            if (FullReductionPromotionTypeEnum.REDUCTION.getType().equals(promotionType)) {
                                                // 直接减金额
                                                rule.put("deduct_price", reduce);
                                            } else {
                                                // 折扣（reduce 为折扣比例，如 8 表示8折）
                                                BigDecimal discountRate = reduce.divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
                                                BigDecimal total = price.multiply(num);
                                                BigDecimal deduct = total.multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, RoundingMode.HALF_UP);
                                                rule.put("deduct_price", deduct);
                                            }
                                        }
                                        ruleJson.set(i, rule);
                                    }
                                }
                                detail.put("rule_json", ruleJson);
                            }
                        }
                    }
                }
                case "full_gift" -> {
                    detail = fullGiftService.detail(marketId);
                    if (detail != null) {
                        Object endTimeObj = detail.get("end_time");
                        if (endTimeObj != null) {
                            detail.put("end_time", formatTimestamp(endTimeObj));
                        }
                        if (needCalculate) {
                            List<Map<String, Object>> ruleJson = (List<Map<String, Object>>) detail.get("rule_json");
                            if (ruleJson != null && !ruleJson.isEmpty()) {
                                ruleJson.sort(Comparator.comparingInt(r -> (Integer) r.get("level")));
                                String ruleType = (String) detail.get("rule_type");
                                BigDecimal ruleUnit = price.multiply(num);
                                if (FullGiftRuleTypeEnum.CYCLE.getType().equals(ruleType)) {
                                    Map<String, Object> first = ruleJson.get(0);
                                    first.put("is_show", 1);
                                    BigDecimal amount = new BigDecimal(first.get("amount").toString());
                                    first.put("is_enough", amount.compareTo(ruleUnit) <= 0 ? 1 : 0);
                                } else {
                                    for (int i = 0; i < ruleJson.size(); i++) {
                                        Map<String, Object> rule = ruleJson.get(i);
                                        rule.put("is_show", 0);
                                        rule.put("is_enough", 0);
                                        BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                                        if (i == 0) {
                                            rule.put("is_show", 1);
                                        }
                                        if (ruleUnit.compareTo(amount) >= 0) {
                                            rule.put("is_show", 1);
                                            if (i > 0) {
                                                ruleJson.get(i - 1).put("is_show", 0);
                                            }
                                            rule.put("is_enough", 1);
                                            if (i > 0) {
                                                ruleJson.get(i - 1).put("is_enough", 0);
                                            }
                                        }
                                        ruleJson.set(i, rule);
                                    }
                                }
                                detail.put("rule_json", ruleJson);
                            }
                        }
                    }
                }
                case "discount" -> {
                    detail = discountService.detail(marketId);
                    if (detail != null) {
                        // 对齐 PHP：model timestamp 输出后，再把 end_time 格式化为 Y-m-d H:i；start_time 同步格式化供有效期展示/倒计时
                        Object startTimeObj = detail.get("start_time");
                        if (startTimeObj != null) {
                            detail.put("start_time", formatTimestamp(startTimeObj));
                        }
                        Object endTimeObj = detail.get("end_time");
                        if (endTimeObj != null) {
                            detail.put("end_time", formatTimestamp(endTimeObj));
                        }
                    }
                }
                default -> detail = new HashMap<>();
            }
            result.put(marketType, detail);
        }
        return result;
    }

    /**
     * 获取营销活动关联的商品分页
     *
     * @param pageParam 分页
     * @param param     包含 type (活动类型) 和 id (活动ID)
     * @return 分页数据
     */
    @Override
    public PageResult<?> goodsPage(PageParam pageParam, MarketingGoodsParam param) {
        String type = param.getType();
        Integer id = param.getId();
        if (id == null) {
            return null;
        }
        return switch (type) {
            case "full_reduction" -> fullReductionService.getGoodsPage(pageParam, id);
            case "full_gift" -> fullGiftService.getGoodsPage(pageParam, id);
            case "discount" -> discountService.getGoodsPage(pageParam, id);
            default -> null;
        };
    }

    /**
     * 格式化时间戳（支持 Integer/Long 或 String）
     */
    private String formatTimestamp(Object timeObj) {
        if (timeObj == null) return "";
        long timestamp;
        if (timeObj instanceof Number) {
            timestamp = ((Number) timeObj).longValue();
        } else {
            try {
                timestamp = Long.parseLong(timeObj.toString());
            } catch (NumberFormatException e) {
                return timeObj.toString();
            }
        }
        // 假设 timestamp 为秒级时间戳
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp, 0, java.time.ZoneOffset.ofHours(8));
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
