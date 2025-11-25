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
    private Cached cached;

    @Override
    public SiteShopInfoVo getShopInfo(Integer siteId) {
        SiteShopInfoVo siteShopInfoVo = new SiteShopInfoVo();
        Site siteInfo = siteMapper.selectById(siteId);
        BeanUtils.copyProperties(siteInfo, siteShopInfoVo);
        SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        if(model != null){
            SiteShopInfoVo.shopInfo shopInfo = new SiteShopInfoVo.shopInfo();
            shopInfo.setSupplyFollowNum(model.getSupplyFollowNum());
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
        businessStatus = ObjectUtil.isNotEmpty(businessStatus) ? businessStatus : 0;
        int num = siteShopMapper.update(new SiteShop(), new UpdateWrapper<SiteShop>().eq("site_id", siteId).set("business_status", businessStatus));
        if(num>0) cached.tag("site_cache_"+siteId).clear();
    }

    @Override
    public boolean check() {
        Integer siteId = RequestUtils.siteId();
        SiteShop shopInfo = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));

        if (ObjectUtil.isEmpty(shopInfo)) {
            return false;
        } else {
            if (shopInfo.getBankType() == 1) {
                // 判断银行卡类型
                if (ObjectUtil.isEmpty(shopInfo.getBankAccountName()) ||
                        ObjectUtil.isEmpty(shopInfo.getBankAccountNo()) ||
                        ObjectUtil.isEmpty(shopInfo.getBankName()) ||
                        ObjectUtil.isEmpty(shopInfo.getBankAddress())) {
                    return false;
                }
            }
            if (shopInfo.getBankType() == 2) {
                // 判断支付宝类型
                if (ObjectUtil.isEmpty(shopInfo.getAlipayName()) ||
                        ObjectUtil.isEmpty(shopInfo.getAlipayAccountNo())) {
                    return false;
                }
            }
        }
        return true;
    }
}
