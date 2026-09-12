package com.niu.core.mapper.shop;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.shop.SiteShopApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户入驻申请表 Mapper
 */
@Mapper
/**
 * @deprecated 请使用 {@link com.niu.core.mapper.site.SiteApplyMapper}
 */
@Deprecated
public interface SiteShopApplyMapper extends MPJBaseMapper<SiteShopApply> {
}
