package com.niu.core.service.site.shop;


import cn.hutool.json.JSONArray;
import com.niu.core.service.admin.shop.param.SitePaymentParam;
import com.niu.core.service.admin.site.vo.SitePaymentTypeVo;
import com.niu.core.service.site.shop.param.SiteShopPaymentParam;
import com.niu.core.service.site.shop.vo.SiteShopPaymentTypeVo;

import java.util.List;

public interface ISiteShopPaymentService {
    /**
     * 获取站点支付方式列表
     * @return 支付方式列表
     */
    JSONArray getList();
    

    Boolean editStatus(Integer id, SiteShopPaymentParam sitePaymentParam );
}
