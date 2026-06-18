package com.niu.mall.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.exception.CommonException;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponInfoVo;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoreCouponMemberServiceImpl extends ServiceImpl<MallCouponMemberMapper, MallCouponMember> implements ICoreCouponMemberService {

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Override
    public List<MallMemberCouponListVo> getUseCouponListByMemberId(Integer memberId, Integer siteId) {
        List<MallCouponMember> couponMembers = mallCouponMemberMapper.selectList(new QueryWrapper<MallCouponMember>()
                .eq("member_id", memberId)
                .eq("site_id", siteId)
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .gt("expire_time", System.currentTimeMillis() / 1000)
                .orderByAsc("expire_time"));
        List<MallMemberCouponListVo> list = new ArrayList<>();
        for (MallCouponMember memberCoupon : couponMembers) {
            MallMemberCouponListVo vo = new MallMemberCouponListVo();
            BeanUtils.copyProperties(memberCoupon, vo);
            vo.setGoods(mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>().eq("coupon_id", memberCoupon.getCouponId())));
            vo.setCoupon(mallCouponMapper.selectById(memberCoupon.getCouponId()));
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<MallMemberCouponListVo> getUsePlatformCouponListByMemberId(Integer memberId) {
        List<MallCouponMember> couponMembers = mallCouponMemberMapper.selectList(new QueryWrapper<MallCouponMember>()
                .eq("member_id", memberId)
                .eq("site_id", 0)
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .gt("expire_time", System.currentTimeMillis() / 1000)
                .orderByAsc("expire_time"));
        List<MallMemberCouponListVo> list = new ArrayList<>();
        for (MallCouponMember memberCoupon : couponMembers) {
            MallMemberCouponListVo vo = new MallMemberCouponListVo();
            BeanUtils.copyProperties(memberCoupon, vo);
            vo.setGoods(mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>().eq("coupon_id", memberCoupon.getCouponId())));
            vo.setCoupon(mallCouponMapper.selectById(memberCoupon.getCouponId()));
            list.add(vo);
        }
        return list;
    }

    @Override
    public MallMemberCouponInfoVo getUsePlatformCouponInfoById(Integer Id) {

        MallCouponMember couponMember = mallCouponMemberMapper.selectOne(new QueryWrapper<MallCouponMember>()
                .eq("id", Id)
                .eq("site_id", 0)
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
        if (ObjectUtil.isEmpty(couponMember)) {
            throw new CommonException("优惠券不存在或已使用");
        }
        MallMemberCouponInfoVo vo = new MallMemberCouponInfoVo();
        BeanUtils.copyProperties(couponMember, vo);
        vo.setGoods(mallCouponGoodsMapper.selectList(new QueryWrapper<MallCouponGoods>().eq("coupon_id", couponMember.getCouponId())));
        vo.setCoupon(mallCouponMapper.selectById(couponMember.getCouponId()));
        return vo;
    }

    @Override
    public void use(Integer id, Integer tradeId) {
        MallCouponMember memberCoupon = mallCouponMemberMapper.selectOne(new QueryWrapper<MallCouponMember>()
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .eq("id", id));
        if (memberCoupon == null) throw new CommonException("优惠券已使用或已过期");
        if (memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == -1)
            throw new CommonException("当前优惠券已过期");

        memberCoupon.setTradeId(tradeId);
        memberCoupon.setStatus(CouponMemberStatusEnum.USED.getStatus());
        memberCoupon.setUseTime(System.currentTimeMillis() / 1000);
        mallCouponMemberMapper.updateById(memberCoupon);
    }

    /**
     * 恢复已使用的优惠券
     *
     * @param id
     */
    @Override
    public void recover(Integer id) {
        MallCouponMember memberCoupon = mallCouponMemberMapper.selectOne(new QueryWrapper<MallCouponMember>()
                .eq("status", CouponMemberStatusEnum.USED.getStatus())
                .eq("id", id));
        if (memberCoupon != null) {
            memberCoupon.setTradeId(0);
            memberCoupon.setStatus(memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == 1 ? CouponMemberStatusEnum.WAIT_USE.getStatus() : CouponMemberStatusEnum.EXPIRE.getStatus());
            memberCoupon.setUseTime(Long.valueOf(0));
            mallCouponMemberMapper.updateById(memberCoupon);
        }
    }

    @Override
    public void invalid(List<Integer> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            // 如果传入的ids列表为空，直接返回，避免后续不必要的操作
            return;
        }
        QueryWrapper<MallCouponMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus());
        MallCouponMember updateMember = new MallCouponMember();
        updateMember.setStatus(CouponMemberStatusEnum.INVALID.getStatus());
        mallCouponMemberMapper.update(updateMember, queryWrapper);
    }

    /**
     * 优惠券过期
     *
     */
    @Override
    public void expire(List<Integer> ids) {
        MallCouponMember memberCoupon = new MallCouponMember();
        memberCoupon.setStatus(CouponMemberStatusEnum.EXPIRE.getStatus());
        mallCouponMemberMapper.update(memberCoupon, new QueryWrapper<MallCouponMember>()
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .in("id", ids)
        );
    }

    /**
     * 发放优惠券（这里只进行发放，直接领取数量控制在直接领取方法，累计发放数量）
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num      发放数量
     */
    @Override
    public void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num) {
        sendCoupon(siteId, memberId, couponId, num, null);
    }

    /**
     * 发放优惠券（这里只进行发放，直接领取数量控制在直接领取方法，累计发放数量）
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num      发放数量
     * @param num      备注
     */
    @Override
    public void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num, String memo) {
        // 根据优惠券ID和站点ID查询优惠券信息
        QueryWrapper<MallCoupon> couponQueryWrapper = new QueryWrapper<>();
        couponQueryWrapper.eq("id", couponId).eq("site_id", siteId);
        MallCoupon coupon = mallCouponMapper.selectOne(couponQueryWrapper);
        if (coupon == null) {
            throw new CommonException("COUPON_NOT_EXIST");
        }

        // 判断领取时间是否符合要求
        long currentTime = System.currentTimeMillis() / 1000;

        long expireTime;
        if (coupon.getValidType() == 1) {
            expireTime = 86400 * coupon.getLength() + currentTime;
        } else {
            expireTime = coupon.getValidEndTime();
        }

        List<MallCouponMember> memberCouponData = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            MallCouponMember memberCoupon = new MallCouponMember();
            memberCoupon.setSiteId(siteId);
            memberCoupon.setCouponId(couponId);
            memberCoupon.setMemberId(memberId);
            memberCoupon.setCreateTime(currentTime);
            memberCoupon.setExpireTime(expireTime);
            memberCoupon.setReceiveType("send");
            memberCoupon.setType(coupon.getType().toString());
            memberCoupon.setTitle(coupon.getTitle());
            memberCoupon.setPrice(coupon.getPrice());
            memberCoupon.setStatus(CouponMemberStatusEnum.WAIT_USE.getStatus());
            memberCoupon.setMinConditionMoney(coupon.getMinConditionMoney());
            if (memo != null && !memo.isEmpty()) {
                memberCoupon.setMemo(memo);
            }

            memberCouponData.add(memberCoupon);
        }

        this.saveBatch(memberCouponData);

        coupon.setReceiveCount(coupon.getReceiveCount() + num);
        //直接发放不能统计，需要继续优化,防止现有数量为0导致异常，先统计领用数量
/*        if (coupon.getRemainCount()!= -1) {
            coupon.setRemainCount(coupon.getRemainCount() - num);
        }*/
        mallCouponMapper.updateById(coupon);
    }


}
