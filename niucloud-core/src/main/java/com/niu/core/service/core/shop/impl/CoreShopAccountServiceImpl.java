package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CoreShopAccountServiceImpl implements ICoreShopAccountService {

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;

    @Override
    @Transactional
    public void addLog(Integer siteId, BigDecimal money, String fromType, String memo, String relatedId) {
        if(money.compareTo(BigDecimal.ZERO) == 0) return;
        if(siteId.equals(RequestUtils.defaultSiteId())) return;
        Site site = siteMapper.selectById(siteId);
        if(ObjectUtil.isEmpty(site)) return;
        BigDecimal siteMoney = site.getMoney() != null ? site.getMoney() : BigDecimal.ZERO;
        BigDecimal siteMoneyGet = site.getMoneyGet() != null ? site.getMoneyGet() : BigDecimal.ZERO;
        if(money.compareTo(BigDecimal.ZERO) >  0) {
            site.setMoney(siteMoney.add(money));
            site.setMoneyGet(siteMoneyGet.add(money));
        }else{
            site.setMoney(siteMoney.subtract(money));
            site.setMoneyGet(siteMoneyGet.subtract(money));
        }
        siteMapper.updateById(site);
        SiteShopAccountLog accountLog = new SiteShopAccountLog();
        accountLog.setSiteId(siteId);
        accountLog.setMoney(money);
        accountLog.setFromType(fromType);
        accountLog.setMemo(memo);
        accountLog.setRelatedId(relatedId);
        accountLog.setMoneySum(site.getMoney());
        accountLog.setCreateTime(System.currentTimeMillis() / 1000);
        siteShopAccountLogMapper.insert(accountLog);

    }
}
