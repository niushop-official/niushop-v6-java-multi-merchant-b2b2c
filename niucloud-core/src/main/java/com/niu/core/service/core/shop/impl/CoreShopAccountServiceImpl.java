package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
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
    SiteShopAccountLogMapper siteShopAccountLogMapper;

    @Override
    @Transactional
    public void addLog(Integer siteId, BigDecimal money, String fromType, String memo, String relatedId) {
        if(money.compareTo(BigDecimal.ZERO) == 0) return;
        if(siteId.equals(RequestUtils.defaultSiteId())) return;
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        if(ObjectUtil.isEmpty(shop)) return;
        if(money.compareTo(BigDecimal.ZERO) >  0) {
            shop.setMoney(shop.getMoney().add(money));
            shop.setMoneyGet(shop.getMoneyGet().add(money));
        }else{
            shop.setMoney(shop.getMoney().subtract(money));
            shop.setMoneyGet(shop.getMoneyGet().subtract(money));
        }
        siteShopMapper.updateById(shop);
        SiteShopAccountLog accountLog = new SiteShopAccountLog();
        accountLog.setSiteId(siteId);
        accountLog.setMoney(money);
        accountLog.setFromType(fromType);
        accountLog.setMemo(memo);
        accountLog.setRelatedId(relatedId);
        accountLog.setMoneySum(shop.getMoney());
        accountLog.setCreateTime(System.currentTimeMillis() / 1000);
        siteShopAccountLogMapper.insert(accountLog);

    }
}
