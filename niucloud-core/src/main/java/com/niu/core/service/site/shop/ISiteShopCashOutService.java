package com.niu.core.service.site.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopCashOutCancelVo;
import com.niu.core.service.site.shop.vo.SiteShopCashOutCodeVo;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import com.niu.core.service.api.shop.param.WithdrawApplyParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商家站点提现服务接口
 */
public interface ISiteShopCashOutService {

    /**
     * 商家站点提现列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopCashOutListVo>
     */
    PageResult<SiteShopCashOutListVo> page(PageParam pageParam, SiteShopCashOutSearchParam searchParam);

    /**
     * 商家站点提现详情
     * @param id 主键ID
     * @return  SiteShopCashOutInfoVo
     */
    SiteShopCashOutInfoVo info(String id);


    /**
     * 申请提现
     */
    Integer apply(WithdrawApplyParam withdrawParam);

    /**
     * 取消申请提现
     */
    Boolean cancel(Integer id);


    /**
     * 商家站点提现转账
     * @param id
     */
    SiteShopCashOutCodeVo transfer(Integer id);
}
