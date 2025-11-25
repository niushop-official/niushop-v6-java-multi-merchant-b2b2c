package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import com.niu.core.service.core.shop.ICoreShopMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CoreShopMemberServiceImpl implements ICoreShopMemberService {

    @Resource
    SiteShopMemberMapper siteShopMemberMapper;

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;


    @Override
    public Boolean setConsum(Integer siteId, Integer memberId, Integer comsumNum, BigDecimal comsumMoney) {
        SiteShopMember shopMember = findShopMemberCreateIfcNotExist(siteId, memberId);
        Long nowTime = System.currentTimeMillis()/1000;

        shopMember.setLastConsumTime(nowTime);
        if (ObjectUtil.isNotEmpty(shopMember.getFirstConsumTime())){
            shopMember.setFirstConsumTime(nowTime);
        }
        int oldComsumNum = ObjectUtil.isNotEmpty(shopMember.getComsumNum()) ? shopMember.getComsumNum() : 0;
        shopMember.setComsumNum(oldComsumNum + (ObjectUtil.isNotEmpty(comsumNum) ? comsumNum : 1));
        BigDecimal oldComsumMoney = ObjectUtil.isNotEmpty(shopMember.getComsumMoney()) ? shopMember.getComsumMoney() : BigDecimal.ZERO;
        shopMember.setComsumMoney(oldComsumMoney.add(ObjectUtil.isNotEmpty(comsumMoney) ? comsumMoney : BigDecimal.ZERO));
        siteShopMemberMapper.updateById(shopMember);
        return true;
    }

    @Override
    public SiteShopMember findShopMemberCreateIfcNotExist(Integer siteId, Integer memberId) {
        SiteShopMember shopMember = siteShopMemberMapper.selectOne(
                new QueryWrapper<SiteShopMember>()
                        .eq("site_id", siteId)
                        .eq("member_id", memberId).last("limit 1")
        );
        if(ObjectUtil.isEmpty(shopMember)){
            shopMember = new SiteShopMember();
            shopMember.setMemberId(memberId);
            shopMember.setSiteId(siteId);
            siteShopMemberMapper.insert(shopMember);
        }
        return shopMember;
    }
}
