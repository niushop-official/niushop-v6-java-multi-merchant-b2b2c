package com.niu.core.service.core.shop.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.shop.ICoreShopMemberConfigService;
import com.niu.core.service.core.shop.ICoreShopMemberService;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CoreShopMemberServiceImpl implements ICoreShopMemberService {

    @Resource
    SiteShopMemberMapper siteShopMemberMapper;

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;

    @Resource
    ICoreShopMemberConfigService shopMemberConfigService;

    @Resource
    SiteMapper siteMapper;


    @Override
    public Boolean setConsum(Integer siteId, Integer memberId, String addon, Integer comsumNum, BigDecimal comsumMoney) {
        SiteShopMember shopMember = findShopMemberCreateIfcNotExist(siteId, memberId, addon);
        Long nowTime = System.currentTimeMillis() / 1000;

        shopMember.setLastConsumTime(nowTime);
        if (ObjectUtil.isEmpty(shopMember.getFirstConsumTime())) {
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
    public SiteShopMember findShopMemberCreateIfcNotExist(Integer siteId, Integer memberId, String addon) {
        SiteShopMember shopMember = siteShopMemberMapper.selectOne(
                new LambdaQueryWrapper<SiteShopMember>()
                        .eq(SiteShopMember::getSiteId, siteId)
                        .eq(SiteShopMember::getMemberId, memberId)
                        .eq(SiteShopMember::getAddon, addon)
                        .last("limit 1")
        );
        if (ObjectUtil.isEmpty(shopMember)) {
            shopMember = new SiteShopMember();
            shopMember.setMemberId(memberId);
            shopMember.setSiteId(siteId);
            shopMember.setAddon(addon);
            siteShopMemberMapper.insert(shopMember);
        }
        return shopMember;
    }

    // ========== 新增缺失方法 ==========

    @Override
    @Transactional
    public void batchFindShopMemberCreateIfcNotExist(Integer siteId, Integer memberId) {
        Site site = siteMapper.selectById(siteId);
        if (site == null) {
            throw new CommonException("站点不存在");
        }
        // 获取站点 app 列表（假设 site.getApp() 是 JSON 数组或逗号分隔）
        List<String> appList = parseAppList(site.getApp());
        for (String app : appList) {
            if ("supply".equals(app)) continue; // 供货商不创建
            findShopMemberCreateIfcNotExist(siteId, memberId, app);
        }
    }

    @Override
    @Transactional
    public void modify(Integer siteId, Integer memberId, String addon, String field, Object value) {
        String fieldName = mapFieldName(field);
        LambdaQueryWrapper<SiteShopMember> wrapper = new LambdaQueryWrapper<SiteShopMember>()
                .eq(SiteShopMember::getSiteId, siteId)
                .eq(SiteShopMember::getMemberId, memberId)
                .eq(SiteShopMember::getAddon, addon);
        SiteShopMember shopMember = siteShopMemberMapper.selectOne(wrapper);
        if (shopMember == null) {
            throw new CommonException("店铺会员记录不存在");
        }
        // 根据字段名动态设置值（这里仅支持 shop_member_label，可扩展）
        if ("shop_member_label".equals(fieldName)) {
            shopMember.setShopMemberLabel(String.valueOf(value));
        } else {
            throw new CommonException("不支持的字段: " + field);
        }
        siteShopMemberMapper.updateById(shopMember);
    }

    @Override
    @Transactional
    public void batchModify(Integer siteId, List<Integer> memberIds, String addon, String field, Object value) {
        if (CollUtil.isEmpty(memberIds)) return;
        String fieldName = mapFieldName(field);
        // 批量更新
        LambdaQueryWrapper<SiteShopMember> wrapper = new LambdaQueryWrapper<SiteShopMember>()
                .eq(SiteShopMember::getSiteId, siteId)
                .in(SiteShopMember::getMemberId, memberIds)
                .eq(SiteShopMember::getAddon, addon);
        SiteShopMember updateData = new SiteShopMember();
        if ("shop_member_label".equals(fieldName)) {
            updateData.setShopMemberLabel(String.valueOf(value));
        } else {
            throw new CommonException("不支持的字段: " + field);
        }
        siteShopMemberMapper.update(updateData, wrapper);
    }

    @Override
    @Transactional
    public void checkOldShopMember(Integer memberId) {
        // 查询该会员的所有店铺会员记录（可能跨多个站点）
        List<SiteShopMember> members = siteShopMemberMapper.selectList(
                new LambdaQueryWrapper<SiteShopMember>()
                        .eq(SiteShopMember::getMemberId, memberId)
        );
        if (CollUtil.isEmpty(members)) {
            throw new CommonException("MEMBER_NOT_EXIST");
        }
        // 对每个站点单独检查
        for (SiteShopMember shopMember : members) {
            Integer siteId = shopMember.getSiteId();
            // 获取该站点的老会员配置
            ShopMemberConfigVo config = shopMemberConfigService.getMemberConfig(siteId);
            if (config == null || config.getOldMemberLength() == null) {
                continue;
            }
            Long createTime = shopMember.getCreateTime();
            long now = System.currentTimeMillis() / 1000;
            long diff = now - createTime;
            int oldMemberLength = config.getOldMemberLength();
            if (diff > 3600L * 24 * oldMemberLength) {
                // 超过老会员天数，设置为老会员
                shopMember.setIsOld(1);
                siteShopMemberMapper.updateById(shopMember);
            }
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 将 PHP 中的 match 逻辑映射到 Java
     */
    private String mapFieldName(String field) {
        // 目前只有 shop_member_label 一种
        if ("shop_member_label".equals(field)) {
            return "shop_member_label";
        }
        // 可扩展其他字段
        throw new CommonException("不支持的字段: " + field);
    }

    /**
     * 解析站点 app 字段（假设存储为 JSON 数组字符串，如 ["mall", "o2o"]）
     */
    private List<String> parseAppList(String appJson) {
        if (StrUtil.isBlank(appJson)) {
            return java.util.Collections.emptyList();
        }
        // 使用 Hutool 解析 JSON 数组
        return cn.hutool.json.JSONUtil.toList(appJson, String.class);
    }
}
