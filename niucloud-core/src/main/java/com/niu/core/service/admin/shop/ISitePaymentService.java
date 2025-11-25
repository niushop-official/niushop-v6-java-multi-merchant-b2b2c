package com.niu.core.service.admin.shop;


import cn.hutool.json.JSONArray;
import com.niu.core.service.admin.shop.param.SitePaymentParam;
import com.niu.core.service.admin.site.vo.SitePaymentTypeVo;

import java.util.List;

public interface ISitePaymentService {
    /**
     * 获取站点支付方式列表
     * @return 支付方式列表
     */
    JSONArray getList();
}
