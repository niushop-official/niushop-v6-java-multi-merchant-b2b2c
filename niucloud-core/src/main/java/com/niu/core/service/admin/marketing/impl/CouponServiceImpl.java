package com.niu.core.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.entity.coupon.MallCoupon;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.core.entity.coupon.MallCouponMember;
import com.niu.core.enums.coupon.CouponMemberStatusEnum;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.mapper.coupon.MallCouponGoodsMapper;
import com.niu.core.mapper.coupon.MallCouponMapper;
import com.niu.core.mapper.coupon.MallCouponMemberMapper;
import com.niu.core.service.admin.goods.IGoodsCategoryService;
import com.niu.core.service.admin.marketing.ICouponService;
import com.niu.core.service.admin.marketing.IMallCouponGoodsService;
import com.niu.core.service.admin.marketing.param.MallCouponParam;
import com.niu.core.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.core.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.core.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.admin.marketing.vo.MallCouponListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponSelectVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class
CouponServiceImpl implements ICouponService {

    @Resource
    private IGoodsCategoryService goodsCategoryService;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    IMallCouponGoodsService shopCouponGoodsService;

    @Override
    public PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("site_id", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus()))) queryWrapper.eq("status", searchParam.getStatus());
        queryWrapper.orderByDesc("id");

        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon item : iPage.getRecords()) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setReceiveUseCount(mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>().eq("coupon_id", item.getId()).gt("use_time", 1)));
            vo.setStartTime(item.getStartTime() == null || item.getStartTime() == 0 ? "0" : DateUtils.timestampToString(item.getStartTime()));
            vo.setEndTime(item.getEndTime() == null || item.getEndTime() == 0 ? "0" : DateUtils.timestampToString(item.getEndTime()));
            // 对齐 PHP with site
            MallCouponListVo.SiteInfo siteInfo = new MallCouponListVo.SiteInfo();
            if (item.getSiteId() != null && item.getSiteId() > 0) {
                Site site = siteMapper.selectOne(new QueryWrapper<Site>()
                        .eq("site_id", item.getSiteId())
                        .select("site_id", "site_name", "logo")
                        .last("limit 1"));
                if (site != null) {
                    BeanUtils.copyProperties(site, siteInfo);
                }
            }
            vo.setSite(siteInfo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public MallCouponSelectVo getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        //去除重复
        MallCouponSelectVo voRes = new MallCouponSelectVo();
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyCouponIds())) {
            List<Integer> couponIds = new ArrayList<>();
            List<MallCoupon> couponList = mallCouponMapper.selectList(queryWrapper);
            for (MallCoupon item : couponList) {
                if (searchParam.getVerifyCouponIds().contains(item.getId())) {
                    couponIds.add(item.getId());
                }
            }
            voRes.setVerifyCouponIds(couponIds);

        } else {
            voRes.setVerifyCouponIds(new ArrayList<>());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifyCouponIds()))
            queryWrapper.in("id", searchParam.getVerifyCouponIds());
        queryWrapper.orderByDesc("id");

        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallCouponSelectVo.ShopCouponListVo> list = new LinkedList<>();
        for (MallCoupon item : iPage.getRecords()) {
            MallCouponSelectVo.ShopCouponListVo vo = new MallCouponSelectVo.ShopCouponListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setReceiveUseCount(mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>().eq("coupon_id", item.getId()).gt("use_time", 1)));
            list.add(vo);
        }

        voRes.setPerPage(limit);
        voRes.setTotal(iPage.getTotal());
        voRes.setData(list);
        voRes.setCurrentPage(page);

        return voRes;

    }

    @Override
    public List<MallCouponListVo> getSelectedList(String couponIds) {

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        if (!couponIds.equals("")) queryWrapper.in("id", couponIds);

        queryWrapper.orderByDesc("id");
        List<MallCoupon> list = mallCouponMapper.selectList(queryWrapper);
        List<MallCouponListVo> voList = new LinkedList<>();
        for (MallCoupon item : list) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }
        return voList;

    }

    @Override
    public MallCouponInfoVo getInfo(Integer couponId) {
        MallCoupon model = mallCouponMapper.selectOne(
                new QueryWrapper<MallCoupon>()
                        .eq("id", couponId)
                        .last("limit 1"));
        MallCouponInfoVo vo = new MallCouponInfoVo();
        if (ObjectUtil.isEmpty(model)) return null;
        BeanUtils.copyProperties(model, vo);
        vo.setStartTime(model.getStartTime() == null || model.getStartTime() == 0 ? "0" : DateUtils.timestampToString(model.getStartTime()));
        vo.setEndTime(model.getEndTime() == null || model.getEndTime() == 0 ? "0" : DateUtils.timestampToString(model.getEndTime()));

        if (vo.getRemainCount() == -1) {
            vo.setLimit(2);
        } else {
            vo.setLimit(1);
        }

        if (vo.getMinConditionMoney().compareTo(new BigDecimal("0.00")) == 0) {
            vo.setThreshold(2);
        } else {
            vo.setThreshold(1);
        }

        if (model.getLimitCount() == 0) {
            vo.setLimitCount("");
        } else {
            vo.setLimitCount(model.getLimitCount().toString());
        }

        if (vo.getType() == 2) {
            List<MallCouponGoods> list = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>().select("category_id").eq("coupon_id", couponId));
            List<Integer> newList = list.stream().map(MallCouponGoods::getCategoryId).collect(Collectors.toList());
            vo.setGoodsCategoryIds(newList);
        } else {
            vo.setGoodsCategoryIds(new ArrayList<>());
        }

        if (vo.getType() == 3) {
            List<MallCouponGoods> list = mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>().select("goods_id").eq("coupon_id", couponId));
            List<Integer> newList = list.stream().map(MallCouponGoods::getGoodsId).collect(Collectors.toList());
            vo.setGoodsIds(newList);
        } else {
            vo.setGoodsIds(new ArrayList<>());
        }

        if (vo.getRemainCount() == -1) {
            vo.setRemainCount(1000);
            vo.setSumCount("不限量");
        } else {
            vo.setSumCount(Integer.toString(vo.getRemainCount() + vo.getReceiveCount()));
        }

        Long receiveCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>().eq("coupon_id", couponId).gt("use_time", 1));
        if (ObjectUtil.isNotEmpty(receiveCount)) {
            vo.setReceiveUseCount(receiveCount);
        } else {
            vo.setReceiveUseCount(0L);
        }
        Long expireCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>().eq("coupon_id", couponId).eq("use_time", 0).le("expire_time", System.currentTimeMillis() / 1000));
        if (ObjectUtil.isNotEmpty(expireCount)) {
            vo.setReceiveExpireCount(expireCount);
        } else {
            vo.setReceiveExpireCount(0L);
        }

        return vo;
    }

    @Override
    public PageResult<MallCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallCouponMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("c")
                .innerJoin("?_member m ON c.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("c.*,m.username,m.mobile,m.nickname,m.member_no");
        queryWrapper.eq("c.coupon_id", id);

        if (!keywords.isEmpty()) {
            //查询逻辑应该是在领取该优惠券的用户中模糊查询，不是在所有用户中模糊查询
            queryWrapper.and((qw)->qw.like("m.username", keywords).or()
                    .like("m.nickname", keywords).or()
                    .like("m.mobile", keywords));
        }

        IPage<MallCouponMemberListVo> iPage = mallCouponMemberMapper.selectJoinPage(new Page<>(page, limit), MallCouponMemberListVo.class, queryWrapper);
        for (MallCouponMemberListVo vo : iPage.getRecords()) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
        }

        return PageResult.build(iPage);
    }

}
