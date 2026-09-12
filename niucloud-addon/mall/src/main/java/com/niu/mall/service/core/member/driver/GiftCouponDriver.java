package com.niu.mall.service.core.member.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.enums.coupon.CouponTypeEnum;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class GiftCouponDriver {

    public MallCouponMapper shopCouponMapper() {
        return SpringContext.bean(MallCouponMapper.class);
    }

    public ICoreCouponMemberService coreCouponMemberService() {
        return SpringContext.bean(ICoreCouponMemberService.class);
    }


    /**
     * 获取礼包内容
     * @param siteId
     * @param config
     * @param scene
     * @return
     */
    public Object content(Integer siteId, JSONObject config, String scene) {
        List<MallCoupon> couponList = shopCouponMapper().selectList(new QueryWrapper<MallCoupon>().in("id", config.getJSONArray("coupon_id")));
        if (ObjectUtil.isEmpty(couponList)) return "";

        switch (scene) {
            case "admin":
                List<String> content = new LinkedList<>();
                for (MallCoupon item : couponList) {
                    content.add(item.getTitle() + "*" + config.getByPath("coupon_list.id_" + item.getId(), String.class));
                }
                return String.join(",", content);
            case "member_level":
                JSONArray levelContent = new JSONArray();
                for (MallCoupon item : couponList) {
                    JSONObject levelContentItem = new JSONObject();
                    levelContentItem.set("text", Math.round(item.getPrice().doubleValue()) + "元优惠券");
                    levelContentItem.set("background", "/addon/mall/gift/gift_coupon_bg.png");
                    levelContentItem.set("icon", "/addon/mall/coupon/coupon_icon.png");
                    levelContent.add(levelContentItem);
                }
                return levelContent;
            case "member_sign":
                JSONArray signContent = new JSONArray();
                for (MallCoupon item : couponList) {
                    JSONObject contentItem = new JSONObject();
                    // 与PHP保持一致：使用round处理价格，路径为/addon/mall/sign/coupon.png
                    contentItem.set("text", Math.round(item.getPrice().doubleValue()) + "元优惠券");
                    contentItem.set("icon", "/addon/mall/sign/coupon.png");
                    signContent.add(contentItem);
                }
                return signContent;
            case "member_sign_continue":
                JSONArray signContinueContent = new JSONArray();
                for (MallCoupon item : couponList) {
                    JSONObject contentItem = new JSONObject();
                    // 与PHP保持一致：使用round处理价格，路径为/addon/mall/sign/coupon01.png
                    contentItem.set("text", Math.round(item.getPrice().doubleValue()) + "元优惠券");
                    contentItem.set("icon", "/addon/mall/sign/coupon01.png");
                    signContinueContent.add(contentItem);
                }
                return signContinueContent;
            case "wap":
                return buildWapCouponContent(siteId, config);
            default:
                return "";
        }
    }

    private JSONArray buildWapCouponContent(Integer siteId, JSONObject config) {
        JSONArray couponIds = config.getJSONArray("coupon_id");
        if (ObjectUtil.isEmpty(couponIds)) {
            return new JSONArray();
        }
        List<MallCoupon> couponList = shopCouponMapper().selectList(
                new QueryWrapper<MallCoupon>()
                        .in("id", couponIds)
                        .select("id", "site_id", "title", "price", "min_condition_money", "type", "valid_type", "length", "valid_start_time", "valid_end_time")
        );
        JSONArray wapContent = new JSONArray();
        for (MallCoupon coupon : couponList) {
            JSONObject item = new JSONObject();
            item.set("id", coupon.getId());
            item.set("title", coupon.getTitle());
            item.set("price", coupon.getPrice());
            item.set("min_condition_money", formatMoney(coupon.getMinConditionMoney()));
            item.set("coupon_min_price", coupon.getMinConditionMoney());
            item.set("type", coupon.getType());
            item.set("valid_type", coupon.getValidType());
            item.set("length", coupon.getLength());
            item.set("valid_start_time", coupon.getValidStartTime());
            item.set("valid_end_time", coupon.getValidEndTime());
            item.set("type_name", CouponTypeEnum.getNameByType(coupon.getType()));
            wapContent.add(item);
        }
        return wapContent;
    }

    private String formatMoney(BigDecimal money) {
        if (money == null) {
            return "0.00";
        }
        return money.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 发放礼包
     */
    public void grant(Integer siteId, Integer memberId, JSONObject config, Map<String, Object> vars) {
        if (config == null) {
            return;
        }
        JSONArray couponIds = config.getJSONArray("coupon_id");
        if (ObjectUtil.isEmpty(couponIds)) {
            return;
        }
        String memo = vars != null && vars.get("memo") != null ? String.valueOf(vars.get("memo")) : "";
        for (int i = 0; i < couponIds.size(); i++) {
            Integer couponId = couponIds.getInt(i);
            Integer num = config.getByPath("coupon_list.id_" + couponId, Integer.class);
            if (couponId == null || num == null || num <= 0) {
                continue;
            }
            try {
                // 不可用券（已过期/已失效/有效期已过）在 sendCoupon 内拒绝，跳过继续发其它券
                coreCouponMemberService().sendCoupon(siteId, memberId, couponId, num, memo);
            } catch (Exception e) {
                System.out.println("优惠券发放失败 couponId=" + couponId + ", reason=" + e.getMessage());
            }
        }
    }
}
