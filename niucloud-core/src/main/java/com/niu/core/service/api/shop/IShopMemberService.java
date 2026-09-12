package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.vo.SiteShopFollowVo;

public interface IShopMemberService {

    PageResult<SiteShopFollowVo> followList(PageParam pageParam, String addon);

    /**
     * 更新关注店铺
     *
     * @param siteId
     * @param isFollow
     * @param addon
     */
    void follow(Integer siteId, Integer isFollow, String addon);

    /**
     * 当前会员是否关注指定站点（对齐 PHP getFollowBySiteAndAddon）
     */
    boolean getFollowBySiteAndAddon(Integer siteId, String addon);
}
