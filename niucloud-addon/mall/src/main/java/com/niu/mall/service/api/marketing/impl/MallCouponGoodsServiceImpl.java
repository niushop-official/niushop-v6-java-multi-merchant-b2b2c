package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.enums.coupon.CouponIsAllSiteEnum;
import com.niu.mall.enums.coupon.CouponReceiveTypeEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.service.api.goods.vo.MallGoodsCouponVo;
import com.niu.mall.service.api.marketing.IMallCouponGoodsService;
import com.niu.mall.service.api.marketing.param.MallGoodsCouponSearchParam;
import com.niu.mall.service.api.marketing.vo.MallCouponListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 优惠券商品或品类关联实现
 */
@Service
public class MallCouponGoodsServiceImpl extends ServiceImpl<MallCouponGoodsMapper, MallCouponGoods> implements IMallCouponGoodsService {

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    IMemberService memberService;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Override
    public void insertAll(List<MallCouponGoods> list) {
        super.saveOrUpdateBatch(list);
    }

    /**
     * 获取优惠券适用商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<MallGoodsCouponVo> getCouponGoodsPageList(PageParam pageParam, MallGoodsCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        // 初始化商品优惠券ID数组
        List<Integer> goodsCouponIdList = new ArrayList<>();
        if (searchParam.getGoodsId() != null && searchParam.getGoodsId() > 0) {
            QueryWrapper<MallCouponGoods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("goods_id", searchParam.getGoodsId());
            List<MallCouponGoods> goodsCouponList = mallCouponGoodsMapper.selectList(goodsQueryWrapper);

            if (!goodsCouponList.isEmpty()) {
                for (MallCouponGoods couponGoods : goodsCouponList) {
                    goodsCouponIdList.add(couponGoods.getCouponId());
                }
            } else {
                goodsCouponIdList.add(-1);
            }
        }

        // 初始化分类优惠券ID数组
        List<Integer> categoryCouponIdList = new ArrayList<>();
        if (searchParam.getCategoryId() != null && searchParam.getCategoryId() != 0) {
            QueryWrapper<MallCouponGoods> categoryQueryWrapper = new QueryWrapper<>();
            categoryQueryWrapper.in("category_id", searchParam.getCategoryId());
            List<MallCouponGoods> categoryCouponList = mallCouponGoodsMapper.selectList(categoryQueryWrapper);

            if (!categoryCouponList.isEmpty()) {
                for (MallCouponGoods couponGoods : categoryCouponList) {
                    categoryCouponIdList.add(couponGoods.getCouponId());
                }
            } else {
                categoryCouponIdList.add(-1);
            }
        }

        // 初始化商场分类优惠券ID数组
        List<Integer> mallCategoryCouponIdList = new ArrayList<>();
        if (searchParam.getMallCategoryId() != null && searchParam.getMallCategoryId() != 0) {
            QueryWrapper<MallCouponGoods> mallCategoryQueryWrapper = new QueryWrapper<>();
            mallCategoryQueryWrapper.in("mall_category_id", searchParam.getMallCategoryId());
            List<MallCouponGoods> mallCategoryCouponList = mallCouponGoodsMapper.selectList(mallCategoryQueryWrapper);

            if (!mallCategoryCouponList.isEmpty()) {
                for (MallCouponGoods couponGoods : mallCategoryCouponList) {
                    mallCategoryCouponIdList.add(couponGoods.getCouponId());
                }
            } else {
                mallCategoryCouponIdList.add(-1);
            }
        }

        // 初始化品牌优惠券ID数组
        List<Integer> brandCouponIdList = new ArrayList<>();
        if (searchParam.getBrandId() != null && searchParam.getBrandId() != 0) {
            QueryWrapper<MallCouponGoods> brandQueryWrapper = new QueryWrapper<>();
            brandQueryWrapper.in("brand_id", searchParam.getBrandId());
            List<MallCouponGoods> brandCouponList = mallCouponGoodsMapper.selectList(brandQueryWrapper);

            if (!brandCouponList.isEmpty()) {
                for (MallCouponGoods couponGoods : brandCouponList) {
                    brandCouponIdList.add(couponGoods.getCouponId());
                }
            } else {
                brandCouponIdList.add(-1);
            }
        }

        // 构建最终查询条件
        QueryWrapper<MallCoupon> finalQueryWrapper = new QueryWrapper<>();

        finalQueryWrapper.select("id,site_id,title,start_time,end_time,remain_count,receive_count,limit_count,status,price,min_condition_money,type,receive_type,valid_type,length,valid_start_time,valid_end_time,sort,join_site_ids,is_all_site_join");
        finalQueryWrapper.orderByDesc("id");

        finalQueryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());
        finalQueryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        finalQueryWrapper.in("site_id", Arrays.asList(searchParam.getSiteId(), 0));



        finalQueryWrapper.and(qw -> {
            // 全品类优惠券
            qw.or().eq("type", CouponTypeEnum.ALL.getType());

            // 商品优惠券
            if (!goodsCouponIdList.isEmpty()) {
                qw.or(w -> w.in("id", goodsCouponIdList).eq("type",  CouponTypeEnum.GOODS.getType()));
            }

            // 分类优惠券
            if (!categoryCouponIdList.isEmpty()) {
                qw.or(w -> w.in("id", categoryCouponIdList).eq("type",  CouponTypeEnum.CATEGORY.getType()));
            }

            // 商城分类优惠券
            if (!mallCategoryCouponIdList.isEmpty()) {
                qw.or(w -> w.in("id", mallCategoryCouponIdList).eq("type", CouponTypeEnum.MALL_CATEGORY.getType()));
            }

            // 品牌优惠券
            if (!brandCouponIdList.isEmpty()) {
                qw.or(w -> w.in("id", brandCouponIdList).eq("type", CouponTypeEnum.BRAND.getType()));
            }
        });

        // 执行最终查询
        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), finalQueryWrapper);
        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon item : iPage.getRecords()) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(item, vo);

            if (ObjectUtil.isNotEmpty(item.getRemainCount()) &&  item.getRemainCount() == -1){
                vo.setSumCount("-1");
            }else{
                vo.setSumCount(String.valueOf(item.getRemainCount() + item.getReceiveCount()));
            }

            if ( RequestUtils.memberId() != null) {
                Long coupon_member_usable_count = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                        .eq("member_id", RequestUtils.memberId())
                        .eq("coupon_id", vo.getId())
                        .eq("status", CouponStatusEnum.NORMAL.getStatus()));
                Long coupon_member_count = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                        .eq("member_id", RequestUtils.memberId())
                        .eq("coupon_id", vo.getId()));
                if (coupon_member_count > 0) {
                    vo.setIsReceive(1);
                } else {
                    vo.setIsReceive(0);
                }
                if (coupon_member_usable_count > 0) {
                    vo.setNeedReceive(0);
                } else {
                    vo.setNeedReceive(1);
                }
                vo.setMemberReceiveCount(coupon_member_count);
            } else {
                vo.setMemberReceiveCount(0L);
                vo.setIsReceive(0);
            }

            if (vo.getIsAllSiteJoin() == CouponIsAllSiteEnum.NOT_ALL_SITE_JOIN.getType()) {
                if (ObjectUtil.isNotEmpty(item.getJoinSiteIds())) {
                    vo.setJoinSiteIds(JSONUtil.parseArray(item.getJoinSiteIds()).toList(Integer.class));
                }
                if (vo.getJoinSiteIds().contains(String.valueOf(searchParam.getSiteId()))) {
                    list.add(vo);
                }
            }else{
                list.add(vo);
            }
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }
}
