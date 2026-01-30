package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteShopAccountParam;
import com.niu.core.service.api.shop.param.SiteShopParam;
import com.niu.core.service.api.shop.param.SiteShopSearchParam;
import com.niu.core.service.api.shop.param.SiteShopSelectParam;
import com.niu.core.service.api.shop.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 店铺扩展服务接口
 */
public interface ISiteShopService {

    /**
     * 店铺扩展列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopListVo>
     */
    PageResult<SiteShopListVo> page(PageParam pageParam, SiteShopSearchParam searchParam);

    List<SiteShopListVo> list(SiteShopSearchParam searchParam);

    /**
     * 店铺扩展详情
     * @param id 主键ID
     * @return  SiteShopInfoVo
     */
    SiteShopInfoVo info(Object id,Map<String, String> allParams);

    /**
     * 店铺二维码
     * @param id 主键ID
     * @return  SiteShopInfoVo
     */
    String shopQrcode(Integer id);

    /**
     * 获取店铺下拉列表
     * @param siteShopSelectParam
     * @return
     */
    SiteShopSelectPage getSelectPage(PageParam pageParam, SiteShopSelectParam siteShopSelectParam);

    /**
     * 更新关注店铺
     * @param siteId
     * @param isFollow
     */
    void editFollowNumber(Integer siteId, Integer isFollow);


    WebShopListVo selectShopList();
}
