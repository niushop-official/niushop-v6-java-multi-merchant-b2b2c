package com.niu.core.service.admin.shop;


import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.SitePaymentAEParam;
import com.niu.core.service.admin.shop.param.SitePaymentSearchParam;
import com.niu.core.service.core.site_payment.vo.SitePaymentVo;

import java.time.LocalDateTime;
import java.util.Map;

public interface ISitePaymentService {
    /**
     * 获取站点支付方式列表
     *
     * @return 支付方式列表
     */
    JSONArray getList();

    PageResult<?> getPage(PageParam pageParam, SitePaymentSearchParam param, LocalDateTime[] allParam);

    SitePaymentVo getDetail(Integer id);

    /**
     * 主动绑定用户
     *
     * @param param
     * @return
     */
    Map<String, Object> addByHandle(SitePaymentAEParam param);

    Map<String, Object> editByHandle(SitePaymentAEParam param);
}
