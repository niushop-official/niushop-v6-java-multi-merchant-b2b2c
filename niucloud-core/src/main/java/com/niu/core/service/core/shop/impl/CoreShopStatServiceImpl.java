package com.niu.core.service.core.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class CoreShopStatServiceImpl implements ICoreShopStatService {

    @Resource
    SiteShopMapper siteShopMapper;

    @Override
    public void addStat(ShopStatParam shopStatParam) {
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", shopStatParam.getSiteId()));

        if(ObjectUtil.isNotEmpty(shop))
        {
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplySaleNum())){
                shop.setSupplySaleNum(shop.getSupplySaleNum() + shopStatParam.getSupplySaleNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyUpNum())){
                shop.setSupplyUpNum(shop.getSupplyUpNum() + shopStatParam.getSupplyUpNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyFollowNum())){
                shop.setSupplyFollowNum(shop.getSupplyFollowNum() + shopStatParam.getSupplyFollowNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyGoodsNum())){
                shop.setSupplyGoodsNum(shop.getSupplyGoodsNum() + shopStatParam.getSupplyGoodsNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getMallSaleNum())){
                shop.setMallSaleNum(shop.getMallSaleNum() + shopStatParam.getMallSaleNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getMallUpNum())){
                shop.setMallUpNum(shop.getMallUpNum() + shopStatParam.getMallUpNum());
            }
            siteShopMapper.updateById(shop);
        }
    }

    @Override
    public void setStat(ShopStatParam shopStatParam) {
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", shopStatParam.getSiteId()));

        if(ObjectUtil.isNotEmpty(shop))
        {
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplySaleNum())){
                shop.setSupplySaleNum(shopStatParam.getSupplySaleNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyUpNum())){
                shop.setSupplyUpNum(shopStatParam.getSupplyUpNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyFollowNum())){
                shop.setSupplyFollowNum(shopStatParam.getSupplyFollowNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getSupplyGoodsNum())){
                shop.setSupplyGoodsNum(shopStatParam.getSupplyGoodsNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getMallSaleNum())){
                shop.setMallSaleNum(shopStatParam.getMallSaleNum());
            }
            if(ObjectUtil.isNotEmpty(shopStatParam.getMallUpNum())){
                shop.setMallUpNum(shopStatParam.getMallUpNum());
            }
            siteShopMapper.updateById(shop);
        }
    }
}
