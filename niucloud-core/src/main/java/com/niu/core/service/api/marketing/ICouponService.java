package com.niu.core.service.api.marketing;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.marketing.param.MallCouponComponentsParam;
import com.niu.core.service.api.marketing.param.MallCouponMemberSearchParam;
import com.niu.core.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.core.service.api.marketing.param.MallCouponSearchParam;
import com.niu.core.service.api.marketing.param.MallGoodsCouponSearchParam;
import com.niu.core.service.api.marketing.vo.MallCouponExpiringVo;
import com.niu.core.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.api.marketing.vo.MallCouponListVo;
import com.niu.core.service.api.marketing.vo.MallCouponMemberListVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠券服务接口
 * 对应 PHP: app\service\api\marketing\CouponService
 */
public interface ICouponService {

    PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam);

    List<MallCouponListVo> getSelectedList(String couponIds);

    MallCouponInfoVo getDetail(Integer couponId);

    void receive(MallCouponReceiveParam mallCouponReceiveParam);

    PageResult<MallCouponMemberListVo> getMemberPage(PageParam pageParam, MallCouponMemberSearchParam searchParam);

    /**
     * 兼容旧调用
     */
    default PageResult<MallCouponMemberListVo> getMemberPage(PageParam pageParam, Integer status, String type) {
        MallCouponMemberSearchParam param = new MallCouponMemberSearchParam();
        param.setStatus(status);
        param.setType(type);
        return getMemberPage(pageParam, param);
    }

    List<JSONObject> getCouponCountByStatus();

    Long getMemberCount(MallCouponMemberSearchParam searchParam);

    default Long getMemberCount() {
        return getMemberCount(new MallCouponMemberSearchParam());
    }

    default Long getMemberCount(Integer status) {
        MallCouponMemberSearchParam param = new MallCouponMemberSearchParam();
        param.setStatus(status);
        return getMemberCount(param);
    }

    MallCouponExpiringVo memberCouponExpiringSoon(MallCouponMemberSearchParam searchParam);

    List<String> getReceiveType();

    JSONArray getCouponType();

    List<MallCouponListVo> getCouponComponents(MallCouponComponentsParam param);

    List<MallCouponListVo> getPlatformCouponComponents(MallCouponComponentsParam param);

    default List<MallCouponListVo> getPlatformCouponComponents(String couponIds, Integer num) {
        MallCouponComponentsParam param = new MallCouponComponentsParam();
        param.setCouponIds(couponIds);
        param.setNum(num);
        return getPlatformCouponComponents(param);
    }

    String getQrcode(Integer couponId);

    List<Map<String, Object>> getGoodsDetailCouponLists(MallGoodsCouponSearchParam searchParam);

    default List<Map<String, Object>> getGoodsDetailCouponLists(Integer goodsId) {
        MallGoodsCouponSearchParam param = new MallGoodsCouponSearchParam();
        param.setGoodsId(goodsId);
        return getGoodsDetailCouponLists(param);
    }

    Map<String, BigDecimal> calcMemberCouponPromotion(List<Map<String, Object>> couponList, BigDecimal goodsMoney, Integer memberId);
}
