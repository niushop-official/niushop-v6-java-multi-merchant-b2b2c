package com.niu.core.service.api.marketing;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.core.service.api.marketing.param.MallCouponSearchParam;
import com.niu.core.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.api.marketing.vo.MallCouponListVo;
import com.niu.core.service.api.marketing.vo.MallCouponMemberListVo;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponMemberService {

    /**
     * 获取会员持有优惠券列表
     * @param memberId 会员ID
     * @return 分页结果
     */
    PageResult<MallCouponMemberListVo> getPage(PageParam pageParam,Integer memberId);
}
