package com.niu.core.service.api.shop.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.shop.ShopMemberIsFollowEnum;
import com.niu.core.event.shop.AfterFollowEvent;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.service.api.shop.IShopMemberService;
import com.niu.core.service.api.shop.vo.SiteShopFollowVo;
import com.niu.core.service.core.shop.ICoreShopMemberService;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopMemberServiceImpl implements IShopMemberService {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private ICoreShopMemberService coreShopMemberService;

    @Resource
    private SiteShopMemberMapper siteShopMemberMapper;

    @Override
    public PageResult<SiteShopFollowVo> followList(PageParam pageParam, String addon) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ssm.id,ssm.site_id,site.logo,site.icon,site.site_name,site.front_end_logo,ssm.member_id")
                .setAlias("ssm")
                .leftJoin("?_site site ON site.site_id = ssm.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("ssm.is_follow", 1);
        queryWrapper.eq("ssm.member_id", RequestUtils.memberId());
        if (StrUtil.isNotBlank(addon)) {
            queryWrapper.eq("ssm.addon", addon);
        }
        queryWrapper.orderByDesc("site.create_time");
        IPage<SiteShopFollowVo> iPage = siteShopMemberMapper.selectJoinPage(new Page<>(page, limit), SiteShopFollowVo.class, queryWrapper);
        List<SiteShopFollowVo> records = iPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            for (SiteShopFollowVo record : records) {
                // null避免图片裂开
                if ("".equals(record.getLogo())) record.setLogo(null);
                if ("".equals(record.getIcon())) record.setIcon(null);
                if ("".equals(record.getFrontEndLogo())) record.setFrontEndLogo(null);

                Site site = new Site();
                site.setSiteId(record.getSiteId());
                site.setSiteName(record.getSiteName());
                site.setLogo(record.getLogo());
                site.setIcon(record.getIcon());
                // 如果front_end_logo为空，使用logo作为默认值
                String frontEndLogo = record.getFrontEndLogo();
                if (ObjectUtil.isEmpty(frontEndLogo)) {
                    frontEndLogo = record.getLogo();
                }
                site.setFrontEndLogo(frontEndLogo);
                record.setSite(site);
            }
        }
        return PageResult.build(iPage);
    }


    @Override
    public void follow(Integer siteId, Integer isFollow, String addon) {
        // 1. 验证状态合法性
        if (ShopMemberIsFollowEnum.getNameByStatus(isFollow).isEmpty()) {
            throw new CommonException("FOLLOW_STATUS_ERROR");
        }


        Integer memberId = RequestUtils.memberId();
        if (memberId == null) {
            throw new CommonException("请先登录");
        }

        // 2. 获取或创建店铺会员记录（复用核心方法）
        SiteShopMember shopMember = coreShopMemberService.findShopMemberCreateIfcNotExist(siteId, memberId, addon);
        if (shopMember == null) {
            throw new CommonException("店铺会员记录创建失败");
        }

        // 3. 检查状态是否已相同
        if (shopMember.getIsFollow() != null && shopMember.getIsFollow().equals(isFollow)) {
            throw new CommonException("FOLLOW_STATUS_ERROR");
        }

        // 4. 更新关注状态
        shopMember.setIsFollow(isFollow);
        siteShopMemberMapper.updateById(shopMember);

        // 5. 统计该 addon 下的关注数量
        Long followNumber = siteShopMemberMapper.selectCount(
                new LambdaQueryWrapper<SiteShopMember>()
                        .eq(SiteShopMember::getSiteId, siteId)
                        .eq(SiteShopMember::getAddon, addon)
                        .eq(SiteShopMember::getIsFollow, 1)
        );

        // 6. 发布事件（用于后续处理，如更新站点关注数、消息通知等）
        eventPublisher.publishEvent(new AfterFollowEvent(this, siteId, addon, followNumber.intValue()));
    }

    @Override
    public boolean getFollowBySiteAndAddon(Integer siteId, String addon) {
        Integer memberId = RequestUtils.memberId();
        if (memberId == null || memberId <= 0 || siteId == null || StrUtil.isBlank(addon)) {
            return false;
        }
        SiteShopMember follow = siteShopMemberMapper.selectOne(new LambdaQueryWrapper<SiteShopMember>()
                .eq(SiteShopMember::getSiteId, siteId)
                .eq(SiteShopMember::getAddon, addon)
                .eq(SiteShopMember::getMemberId, memberId)
                .last("limit 1"));
        if (follow == null) {
            return false;
        }
        return ObjectUtil.equal(follow.getIsFollow(), 1);
    }
}
