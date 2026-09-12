package com.niu.core.service.core.shop;

import com.niu.core.service.core.shop.param.SiteSupplyStatParam;

/**
 * 供货商扩展表 site_supply 累计统计（对齐 PHP CoreSupplyStatService）
 */
public interface ICoreSiteSupplyStatService {

    void addStat(SiteSupplyStatParam param);

    void setStat(SiteSupplyStatParam param);
}
