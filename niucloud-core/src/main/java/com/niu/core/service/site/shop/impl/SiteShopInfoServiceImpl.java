package com.niu.core.service.site.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.ISiteSettAccountService;
import com.niu.core.service.admin.site.param.SiteSettAccountSearchParam;
import com.niu.core.service.admin.site.vo.SiteSettAccountVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.site.shop.ISiteShopInfoService;
import com.niu.core.service.site.shop.vo.SiteShopInfoVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteShopInfoServiceImpl implements ISiteShopInfoService {

    @Resource
    private GoodsServiceMapper goodsServiceMapper;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    private ISiteSettAccountService siteSettAccountService;

    @Resource
    private Cached cached;

    @Resource
    private ICoreSiteService coreSiteService;

    @Override
    public SiteShopInfoVo getShopInfo(Integer siteId) {
        SiteShopInfoVo siteShopInfoVo = new SiteShopInfoVo();
        Site siteInfo = siteMapper.selectById(siteId);
        BeanUtils.copyProperties(siteInfo, siteShopInfoVo);
        SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        if(model != null){
            SiteShopInfoVo.shopInfo shopInfo = new SiteShopInfoVo.shopInfo();
            shopInfo.setSupplyFollowNum(model.getFollowNumber());
            siteShopInfoVo.setShop(shopInfo);
        }
        List<GoodsService> goodsServiceList = goodsServiceMapper.selectList(new QueryWrapper<GoodsService>().eq("site_id", RequestUtils.defaultSiteId()).orderByDesc("create_time"));
        List<SiteShopInfoVo.serviceInfo> serviceInfoList = new ArrayList<>();
        for (GoodsService goodsService : goodsServiceList) {
            SiteShopInfoVo.serviceInfo serviceInfo = new SiteShopInfoVo.serviceInfo();
            BeanUtils.copyProperties(goodsService, serviceInfo);
            serviceInfoList.add(serviceInfo);
        }
        siteShopInfoVo.setServiceList(serviceInfoList);
        return siteShopInfoVo;
    }

    @Override
    @Transactional
    public void editBusinessStatus(Integer businessStatus) {
        Integer siteId = RequestUtils.siteId();
        coreSiteService.editSiteBusinessStatus(siteId, ObjectUtil.isNotEmpty(businessStatus) ? businessStatus : 0);
        cached.tag("site_cache_" + siteId).clear();
    }

    @Override
    public boolean check() {
        List<SiteSettAccountVo> accounts = siteSettAccountService.getList(new SiteSettAccountSearchParam());
        if (ObjectUtil.isEmpty(accounts)) {
            return false;
        }
        for (SiteSettAccountVo account : accounts) {
            if (account.getType() == 1) {
                if (ObjectUtil.isEmpty(account.getName()) ||
                        ObjectUtil.isEmpty(account.getAccountNo()) ||
                        ObjectUtil.isEmpty(account.getBankName())) {
                    return false;
                }
            } else if (account.getType() == 2) {
                if (ObjectUtil.isEmpty(account.getName()) ||
                        ObjectUtil.isEmpty(account.getAccountNo())) {
                    return false;
                }
            }
        }
        return true;
    }
}
