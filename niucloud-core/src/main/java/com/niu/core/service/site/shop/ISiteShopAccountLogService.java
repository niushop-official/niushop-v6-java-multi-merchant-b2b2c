package com.niu.core.service.site.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogParam;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogListVo;
import com.niu.core.service.site.shop.vo.SiteShopAccountLogVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家结算账单记录服务接口
 */
public interface ISiteShopAccountLogService {

    /**
     * 商家结算账单记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopAccountLogListVo>
     */
    PageResult<SiteShopAccountLogListVo> page(PageParam pageParam, SiteShopAccountLogSearchParam searchParam);

    /**
     * 商家结算账单记录详情
     * @param id 主键ID
     * @return  SiteShopAccountLogInfoVo
     */
    SiteShopAccountLogInfoVo info(Integer id);

    /**
     * 店铺资产概况
     * @param type 类型
     * @return BigDecimal 近30天累计收入金额
     */
    BigDecimal getIncomeByDateRange(String type);

}
