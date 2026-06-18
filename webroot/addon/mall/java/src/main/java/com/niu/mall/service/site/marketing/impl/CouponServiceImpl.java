package com.niu.mall.service.site.marketing.impl;

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
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.site.goods.IGoodsCategoryService;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.site.marketing.ICouponService;
import com.niu.mall.service.site.marketing.IMallCouponGoodsService;
import com.niu.mall.service.site.marketing.param.MallCouponParam;
import com.niu.mall.service.site.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.site.marketing.param.MallCouponSelectParam;
import com.niu.mall.service.site.marketing.param.MallCouponSendParam;
import com.niu.mall.service.site.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.site.marketing.vo.MallCouponListVo;
import com.niu.mall.service.site.marketing.vo.MallCouponMemberListVo;
import com.niu.mall.service.site.marketing.vo.MallCouponSelectVo;
import com.niu.mall.service.site.marketing.vo.MallCouponStatVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponServiceImpl implements ICouponService {

    @Resource
    private IGoodsCategoryService goodsCategoryService;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    IMallCouponGoodsService shopCouponGoodsService;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;


    @Override
    public JSONObject getInit() {
        JSONArray tree = goodsCategoryService.tree();
        JSONArray categoryList = new JSONArray();
        tree.forEach(json -> {
                    JSONObject vo = new JSONObject();
                    JSONObject jsonObj = (JSONObject) json;

                    vo.put("value", jsonObj.getInt("category_id"));
                    vo.put("label", jsonObj.getStr("category_name"));
                    if (jsonObj.getJSONArray("children") != null) {
                        JSONArray children = new JSONArray();
                        jsonObj.getJSONArray("children").forEach(child -> {

                            JSONObject childJsonObj = (JSONObject) child;
                            JSONObject childVo = new JSONObject();
                            childVo.put("value", childJsonObj.getInt("category_id"));
                            childVo.put("label", childJsonObj.getStr("category_name"));
                            children.add(childVo);
                        });
                        vo.put("children", children);
                    }
                    categoryList.add(vo);
                }
        );
        JSONObject result = new JSONObject();
        result.put("goods_category_tree", categoryList);
        return result;

    }

    @Override
    public PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        queryWrapper.orderByDesc("id");

        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon item : iPage.getRecords()) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setReceiveUseCount(mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>().eq("coupon_id", item.getId()).gt("use_time", 1)));
            vo.setStartTime(item.getStartTime() == null || item.getStartTime() == 0 ? "0" : DateUtils.timestampToString(item.getStartTime()));
            vo.setEndTime(item.getEndTime() == null || item.getEndTime() == 0 ? "0" : DateUtils.timestampToString(item.getEndTime()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public MallCouponSelectVo getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
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
        queryWrapper.eq("site_id", RequestUtils.siteId());
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
                        .eq("site_id", RequestUtils.siteId())
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

    @Transactional
    @Override
    public void add(MallCouponParam addParam) {

        Integer siteId = RequestUtils.siteId();
        MallCoupon model = new MallCoupon();
        model.setSiteId(siteId);

        if (addParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(addParam.getMinConditionMoney());
        }

        if (addParam.getReceiveType() != 2) {
            if (addParam.getReceiveTypeTime() == 1) {
                if (ObjectUtil.isNotEmpty(addParam.getReceiveTime())) {
                    String[] receiveTime = addParam.getReceiveTime();
                    Long startTime = (receiveTime[0] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[0]);
                    Long endTime = (receiveTime[1] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[1]);
                    model.setStartTime(startTime);
                    model.setEndTime(endTime);
                    Long nowTime = System.currentTimeMillis() / 1000;
                    if (nowTime < startTime) {
                        model.setStatus(0);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(addParam.getValidTime())) {
            Long validStartTime = System.currentTimeMillis() / 1000;
            Long validEndTime = DateUtils.StringToTimestamp(addParam.getValidTime());
            if (validStartTime > validEndTime) throw new AdminException("发放时间不能小于当前时间");
            model.setValidStartTime(validStartTime);
            model.setValidEndTime(validEndTime);
        }

        if (addParam.getLimit() == 2) {
            model.setRemainCount(-1);
        } else {
            model.setRemainCount(addParam.getRemainCount());
        }

        model.setTitle(addParam.getTitle());
        model.setLimitCount(addParam.getLimitCount());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setPrice(addParam.getPrice());
        model.setType(addParam.getType());
        model.setReceiveType(addParam.getReceiveType());
        model.setValidType(addParam.getValidType());
        model.setLength(addParam.getLength());
        mallCouponMapper.insert(model);
        if (ObjectUtil.isNotEmpty(addParam.getGoodsIds()) || ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {

            if (ObjectUtil.isNotEmpty(addParam.getGoodsIds())) {
                Integer[] goodsIds = addParam.getGoodsIds();
                List<MallCouponGoods> list = new ArrayList<>();
                for (Integer goodsId : goodsIds) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    mallCouponGoods.setGoodsId(goodsId);
                    list.add(mallCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }
            if (ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = addParam.getGoodsCategoryIds();
                List<MallCouponGoods> list = new ArrayList<>();
                for (Object goodsCategoryId : goodsCategoryIds) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                    mallCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));

                    list.add(mallCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }

        }
    }

    @Override
    public void edit(Integer couponId, MallCouponParam mallCouponParam) {
        MallCoupon coupon = mallCouponMapper.selectById(mallCouponParam.getId());
        if (!coupon.getStatus().equals(CouponStatusEnum.WAIT_START.getStatus())) {
            throw new RuntimeException("只能编辑未开始状态的优惠券");
        }

        Integer siteId = RequestUtils.siteId();
        MallCoupon model = new MallCoupon();
        model.setSiteId(siteId);
        model.setId(mallCouponParam.getId());
        if (mallCouponParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(mallCouponParam.getMinConditionMoney());
        }

        if (mallCouponParam.getReceiveType() != 2) {
            if (mallCouponParam.getReceiveTypeTime() == 1) {
                if (ObjectUtil.isNotEmpty(mallCouponParam.getReceiveTime())) {
                    String[] receiveTime = mallCouponParam.getReceiveTime();
                    Long startTime = (receiveTime[0] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[0]);
                    Long endTime = (receiveTime[1] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[1]);
                    model.setStartTime(startTime);
                    model.setEndTime(endTime);
                    Long nowTime = System.currentTimeMillis() / 1000;
                    if (nowTime < startTime) {
                        model.setStatus(0);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(mallCouponParam.getValidTime())) {
            Long validStartTime = System.currentTimeMillis() / 1000;
            Long validEndTime = DateUtils.StringToTimestamp(mallCouponParam.getValidTime());
            if (validStartTime > validEndTime) throw new AdminException("发放时间不能小于当前时间");
            model.setValidStartTime(validStartTime);
            model.setValidEndTime(validEndTime);
        }


        if (mallCouponParam.getLimit() == 2) {
            model.setRemainCount(-1);
        } else {
            model.setRemainCount(mallCouponParam.getRemainCount());
        }

        model.setTitle(mallCouponParam.getTitle());
        model.setLimitCount(mallCouponParam.getLimitCount());
        model.setPrice(mallCouponParam.getPrice());
        model.setType(mallCouponParam.getType());
        model.setReceiveType(mallCouponParam.getReceiveType());
        model.setValidType(mallCouponParam.getValidType());
        model.setLength(mallCouponParam.getLength());
        mallCouponGoodsMapper.delete(new QueryWrapper<MallCouponGoods>().eq("coupon_id", model.getId()));
        mallCouponMapper.updateById(model);

        if (ObjectUtil.isNotEmpty(mallCouponParam.getGoodsIds()) || ObjectUtil.isNotEmpty(mallCouponParam.getGoodsCategoryIds())) {
            if (ObjectUtil.isNotEmpty(mallCouponParam.getGoodsIds())) {
                Integer[] goodsIds = mallCouponParam.getGoodsIds();
                List<MallCouponGoods> list = new ArrayList<>();
                for (Integer goodsId : goodsIds) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    mallCouponGoods.setGoodsId(goodsId);
                    list.add(mallCouponGoods);
                }

                shopCouponGoodsService.insertAll(list);
            }
            if (ObjectUtil.isNotEmpty(mallCouponParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = mallCouponParam.getGoodsCategoryIds();
                List<MallCouponGoods> list = new ArrayList<>();
                for (Object goodsCategoryId : goodsCategoryIds) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                    mallCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));

                    list.add(mallCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }
        }
    }

    @Override
    public void delete(Integer couponId) {
        MallCoupon mallCoupon = mallCouponMapper.selectById(couponId);
        if (ObjectUtil.isEmpty(mallCoupon)) throw new AdminException("优惠券不存在");
        if (mallCoupon.getStatus() == CouponStatusEnum.NORMAL.getStatus()) {
            MallCouponMember mallCouponMember = mallCouponMemberMapper.selectOne(new QueryWrapper<MallCouponMember>().eq("coupon_id", couponId));
            if (ObjectUtil.isNotEmpty(mallCouponMember)) {
                throw new AdminException("优惠券已被领取，无法删除");
            }
        }
        mallCouponMapper.deleteById(couponId);
        mallCouponGoodsMapper.delete(new QueryWrapper<MallCouponGoods>().eq("coupon_id", couponId));
        mallCouponMemberMapper.delete(new QueryWrapper<MallCouponMember>().eq("coupon_id", couponId).eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
    }

    @Override
    public PageResult<MallCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallCouponMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("c")
                .innerJoin("?_member m ON c.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("c.*,m.username,m.mobile,m.nickname,m.member_no");
        queryWrapper.eq("c.site_id", RequestUtils.siteId());
        if (id != null) {
            queryWrapper.eq("c.coupon_id", id);
        }

        if (keywords != null && !keywords.isEmpty()) {
            //查询逻辑应该是在领取该优惠券的用户中模糊查询，不是在所有用户中模糊查询
            queryWrapper.and((qw) -> qw.like("m.username", keywords).or()
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

    @Override
    public void setStatus(Integer couponId, Integer status) {

        MallCoupon mallCoupon = new MallCoupon();
        mallCoupon.setReceiveStatus(status);
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>().eq("id", couponId).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void couponInvalid(Integer couponId) {

        MallCoupon mallCoupon = new MallCoupon();
        mallCoupon.setStatus(CouponStatusEnum.INVALID.getStatus());
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>().eq("id", couponId).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public Boolean sendCoupon(MallCouponSendParam param) {
        // 1. 获取参数
        List<MallCouponSendParam.CouponData> couponData = param.getCouponData();
        List<Integer> memberIds = param.getMemberIds();

        // 2. 参数校验
        if (CollectionUtils.isEmpty(couponData) || CollectionUtils.isEmpty(memberIds)) {
            throw new AdminException("优惠券发放参数错误");
        }

        // 3. 循环发放
        for (Integer memberId : memberIds) {
            for (MallCouponSendParam.CouponData coupon : couponData) {
                Integer couponId = coupon.getCouponId();
                Integer num = coupon.getNum();
                try {
                    coreCouponMemberService.sendCoupon(RequestUtils.siteId(), memberId, couponId, num, "店铺发券");
                } catch (Exception e) {
                    log.error("店铺优惠券发放失败，优惠券ID：{}，用户ID：{}，数量：{}，错误信息：{}",
                            couponId, memberId, num, e.getMessage(), e);
                }
            }
        }
        return true;
    }

    @Override
    public MallCouponStatVo getCouponStat(Integer id) {
        Integer siteId = RequestUtils.siteId();
        long now = System.currentTimeMillis() / 1000;

        QueryWrapper<MallCoupon> couponWrapper = new QueryWrapper<>();
        couponWrapper.select("IFNULL(SUM(receive_count), 0) AS receive_count");
        couponWrapper.eq("site_id", siteId);
        if (id != null && id > 0) {
            couponWrapper.eq("id", id);
        }
        Map<String, Object> sumRow = mallCouponMapper.selectMaps(couponWrapper).stream().findFirst().orElse(null);
        long receiveCount = 0L;
        if (sumRow != null && sumRow.get("receive_count") != null) {
            receiveCount = Long.parseLong(sumRow.get("receive_count").toString());
        }

        QueryWrapper<MallCouponMember> useWrapper = new QueryWrapper<>();
        useWrapper.eq("site_id", siteId).gt("use_time", 1);
        if (id != null && id > 0) {
            useWrapper.eq("coupon_id", id);
        }
        Long receiveUseCount = mallCouponMemberMapper.selectCount(useWrapper);

        QueryWrapper<MallCouponMember> expireWrapper = new QueryWrapper<>();
        expireWrapper.eq("site_id", siteId).eq("use_time", 0).lt("expire_time", now);
        if (id != null && id > 0) {
            expireWrapper.eq("coupon_id", id);
        }
        Long receiveExpireCount = mallCouponMemberMapper.selectCount(expireWrapper);

        MallCouponStatVo vo = new MallCouponStatVo();
        vo.setReceiveCount(receiveCount);
        vo.setReceiveUseCount(receiveUseCount != null ? receiveUseCount : 0L);
        vo.setReceiveExpireCount(receiveExpireCount != null ? receiveExpireCount : 0L);
        return vo;
    }
}
