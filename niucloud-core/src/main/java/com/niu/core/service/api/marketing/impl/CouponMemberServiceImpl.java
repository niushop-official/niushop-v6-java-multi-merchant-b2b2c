package com.niu.core.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.core.entity.coupon.MallCouponMember;
import com.niu.core.enums.coupon.*;
import com.niu.core.mapper.coupon.MallCouponGoodsMapper;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.core.mapper.coupon.MallCouponMemberMapper;
import com.niu.core.service.api.marketing.ICouponMemberService;
import com.niu.core.service.api.marketing.ICouponService;
import com.niu.core.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.core.service.api.marketing.param.MallCouponSearchParam;
import com.niu.core.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.api.marketing.vo.MallCouponListVo;
import com.niu.core.service.api.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponMemberServiceImpl implements ICouponMemberService {

    @Resource
    private MallCouponMemberMapper mallCouponMemberMapper;

    @Override
    public PageResult<MallCouponMemberListVo> getPage(PageParam pageParam,Integer memberId) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MallCouponMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("member_id", memberId)
                .select("id", "site_id", "coupon_id", "member_id", "title", "price", "min_condition_money", "create_time", "expire_time", "use_time", "status", "type", "receive_type", "trade_id")
                .orderByDesc("id");

        IPage<MallCouponMember> result = mallCouponMemberMapper.selectPage(new Page<>(page, limit),queryWrapper);

        List<MallCouponMemberListVo> voList = result.getRecords().stream().map(member -> {
            MallCouponMemberListVo vo = new MallCouponMemberListVo();
            BeanUtils.copyProperties(member, vo);

            if (vo.getPrice() == null) {
                vo.setPrice(BigDecimal.ZERO);
            }
            if (vo.getMinConditionMoney() == null) {
                vo.setMinConditionMoney(BigDecimal.ZERO);
            }
            if (vo.getStatus() == null) {
                vo.setStatus(0);
            }
            if (vo.getType() == null) {
                vo.setType("1");
            }
            if (vo.getReceiveType() == null) {
                vo.setReceiveType("1");
            }
            
            return vo;
        }).collect(Collectors.toList());

        return PageResult.build( page,limit, result.getTotal(), voList);
    }
}
