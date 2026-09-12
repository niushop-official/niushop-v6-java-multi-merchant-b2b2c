package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.shop.SiteSupply;
import com.niu.core.mapper.shop.SiteSupplyMapper;
import com.niu.core.service.core.shop.ICoreSiteSupplyStatService;
import com.niu.core.service.core.shop.param.SiteSupplyStatParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreSiteSupplyStatServiceImpl implements ICoreSiteSupplyStatService {

    @Resource
    SiteSupplyMapper siteSupplyMapper;

    @Override
    public void addStat(SiteSupplyStatParam param) {
        if (param == null || param.getSiteId() == null) {
            return;
        }
        SiteSupply supply = siteSupplyMapper.selectOne(new QueryWrapper<SiteSupply>().eq("site_id", param.getSiteId()));
        if (ObjectUtil.isEmpty(supply)) {
            return;
        }
        if (ObjectUtil.isNotEmpty(param.getSupplySaleNum())) {
            supply.setSupplySaleNum(nullSafe(supply.getSupplySaleNum()) + param.getSupplySaleNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyUpNum())) {
            supply.setSupplyUpNum(nullSafe(supply.getSupplyUpNum()) + param.getSupplyUpNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyFollowNum())) {
            supply.setSupplyFollowNum(nullSafe(supply.getSupplyFollowNum()) + param.getSupplyFollowNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyGoodsNum())) {
            supply.setSupplyGoodsNum(nullSafe(supply.getSupplyGoodsNum()) + param.getSupplyGoodsNum());
        }
        siteSupplyMapper.updateById(supply);
    }

    @Override
    public void setStat(SiteSupplyStatParam param) {
        if (param == null || param.getSiteId() == null) {
            return;
        }
        SiteSupply supply = siteSupplyMapper.selectOne(new QueryWrapper<SiteSupply>().eq("site_id", param.getSiteId()));
        if (ObjectUtil.isEmpty(supply)) {
            return;
        }
        if (ObjectUtil.isNotEmpty(param.getSupplySaleNum())) {
            supply.setSupplySaleNum(param.getSupplySaleNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyUpNum())) {
            supply.setSupplyUpNum(param.getSupplyUpNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyFollowNum())) {
            supply.setSupplyFollowNum(param.getSupplyFollowNum());
        }
        if (ObjectUtil.isNotEmpty(param.getSupplyGoodsNum())) {
            supply.setSupplyGoodsNum(param.getSupplyGoodsNum());
        }
        siteSupplyMapper.updateById(supply);
    }

    private int nullSafe(Integer value) {
        return value != null ? value : 0;
    }
}
