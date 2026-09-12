package com.niu.core.service.admin.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.marketing.param.MallCouponMemberSearchParam;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;

/**
 * 优惠券服务接口
 */
public interface IPlatformCouponMemberService {

    /**
     * 优惠券列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopCouponListVo>
     */
    PageResult<MallCouponMemberListVo> getPage(PageParam pageParam, MallCouponMemberSearchParam searchParam);

}
