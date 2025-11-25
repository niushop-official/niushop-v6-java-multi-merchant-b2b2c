package com.niu.core.service.site.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCategory;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.SiteShopAccountInfoParam;
import com.niu.core.service.admin.shop.param.SiteShopBasicInfoParam;
import com.niu.core.service.admin.shop.vo.SiteShopInShopVo;
import com.niu.core.service.admin.shop.vo.SiteShopInfoVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.site.shop.ISiteShopSettingService;
import com.niu.core.service.site.shop.vo.ShopSiteInfoVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteShopSettingServiceImpl implements ISiteShopSettingService {

    @Resource
    private ISiteShopService siteShopService;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    private SiteShopCategoryMapper siteShopCategoryMapper;

    @Resource
    private IAddonService addonService;

    @Override
    public SiteShopInfoVo getBasicInfo() {
        return siteShopService.info(RequestUtils.siteId());
    }

    @Override
    public void setBasicInfo(SiteShopBasicInfoParam siteShopBasicInfoParam) {
        siteShopService.editBasicInfo(RequestUtils.siteId(), siteShopBasicInfoParam);
    }

    @Override
    public ShopSiteInfoVo getShopInfo() {

        Site siteInfo = siteMapper.selectById(RequestUtils.siteId());
        SiteShop shopInfo = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", RequestUtils.siteId()));
        ShopSiteInfoVo shopSiteInfoVo = new ShopSiteInfoVo();
        if(ObjectUtil.isNotEmpty(siteInfo))
        {
            shopSiteInfoVo.setSiteName(siteInfo.getSiteName());
            List<String> appKeys = JSONUtil.toList(JSONUtil.parseArray(siteInfo.getApp()), String.class);
            List<Addon> apps = addonService.getAddonListByKeys(appKeys, AddonActionEnum.APP.getCode());
            shopSiteInfoVo.setAppName(apps.stream().map(Addon::getTitle).collect(Collectors.toList()));
            shopSiteInfoVo.setCreateTime(siteInfo.getCreateTime());
            shopSiteInfoVo.setExpireTime(siteInfo.getExpireTime() == 0 ? "" : DateUtils.timestampToString(siteInfo.getExpireTime()));
        }
        if(ObjectUtil.isNotEmpty(shopInfo))
        {

            shopSiteInfoVo.setIsSelf(shopInfo.getIsSelf());
            SiteShopCategory siteShopCategory = siteShopCategoryMapper.selectById(shopInfo.getCategoryId());
            if(ObjectUtil.isNotEmpty(siteShopCategory))
            {
                shopSiteInfoVo.setCategoryName(siteShopCategory.getCategoryName());
            }
        }
        return shopSiteInfoVo;
    }

    @Override
    public SiteShopInShopVo getShopAccountInfo() {
        SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isEmpty(model)) throw new AdminException("店铺信息不存在");
        SiteShopInShopVo vo = new SiteShopInShopVo();
        BeanUtils.copyProperties(model, vo);
        vo.setMoney(model.getMoney().setScale(2, RoundingMode.HALF_UP).toString());
        vo.setMoneyGet(model.getMoneyGet().setScale(2, RoundingMode.HALF_UP).toString());
        vo.setMoneyCashOuting(model.getMoneyCashOuting().setScale(2, RoundingMode.HALF_UP).toString());
        vo.setShopDeposit(model.getShopDeposit().setScale(2, RoundingMode.HALF_UP).toString());
        vo.setWithdrawalMoney((model.getMoneyGet().subtract(model.getMoney()).subtract(model.getMoneyCashOuting())).setScale(2, RoundingMode.HALF_UP).toString());
        vo.setSupplyServiceRate(model.getSupplyServiceRate().toString());
        return vo;
    }

    @Override
    public void editShopAccount(SiteShopAccountInfoParam siteShopAccountInfoParam) {
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>()
                .eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isEmpty(shop)) {
            throw new AdminException("店铺信息不存在");
        }
        BeanUtils.copyProperties(siteShopAccountInfoParam, shop);
        siteShopMapper.update(shop, new QueryWrapper<SiteShop>()
                .eq("site_id", RequestUtils.siteId()));
    }

}
