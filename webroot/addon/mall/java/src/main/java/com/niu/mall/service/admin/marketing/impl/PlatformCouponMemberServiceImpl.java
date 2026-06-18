package com.niu.mall.service.admin.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.service.admin.marketing.IPlatformCouponMemberService;
import com.niu.mall.service.admin.marketing.param.MallCouponMemberSearchParam;
import com.niu.mall.service.admin.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PlatformCouponMemberServiceImpl implements IPlatformCouponMemberService {

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Override
    public PageResult<MallCouponMemberListVo> getPage(PageParam pageParam, MallCouponMemberSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();

        LambdaQueryWrapper<MallCouponMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallCouponMember::getSiteId, siteId)
                .eq(MallCouponMember::getMemberId, searchParam.getMemberId())
                .orderByDesc(MallCouponMember::getId);
        // 选择需要返回的字段
        wrapper.select(MallCouponMember::getId, MallCouponMember::getTitle, MallCouponMember::getPrice,
                MallCouponMember::getMinConditionMoney, MallCouponMember::getCreateTime,
                MallCouponMember::getExpireTime, MallCouponMember::getUseTime, MallCouponMember::getStatus);
        var iPage = mallCouponMemberMapper.selectPage(new Page<>(page, limit), wrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }
}
