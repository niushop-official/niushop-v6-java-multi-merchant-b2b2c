package com.niu.core.service.admin.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.site_payment.param.SitePaymentApplyParam;
import com.niu.core.service.core.site_payment.vo.SitePaymentApplyVo;

public interface ISitePaymentApplyService {

    /**
     * 分页列表
     *
     * @param param@return 分页结果（已处理 site_name 和 user_type）
     */
    PageResult<SitePaymentApplyVo> getPage(PageParam pageParam, SitePaymentApplyParam param);

    /**
     * 详情
     *
     * @param id 申请记录ID
     * @return 详情数据（Map或自定义VO）
     */
    SitePaymentApplyVo getDetail(Integer id);
}
