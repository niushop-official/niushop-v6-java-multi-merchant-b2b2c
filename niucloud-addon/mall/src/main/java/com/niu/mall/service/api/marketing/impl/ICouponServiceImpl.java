package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.coupon.*;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.api.marketing.ICouponService;
import com.niu.mall.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.mall.service.api.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.api.marketing.vo.MallCouponListVo;
import com.niu.mall.service.api.marketing.vo.MallCouponMemberListVo;
import com.niu.mall.service.core.goods.ICoreGoodsCouponService;
import com.niu.mall.service.core.goods.vo.CouponGoodsVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ICouponServiceImpl implements ICouponService {

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    ICoreGoodsCouponService coreGoodsCouponService;

    @Resource
    ISiteService siteService;
    @Resource
    SiteMapper siteMapper;
    @Resource
    private Cached cached;
    @Override
    public PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        Integer siteId = ObjectUtil.isNotEmpty(searchParam.getSiteId()) ? searchParam.getSiteId() : RequestUtils.siteId();

        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        queryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());

        //根据时间查询
        Long nowTime = System.currentTimeMillis() / 1000;
        queryWrapper.and(i -> i.and(j -> j.le("start_time", nowTime).ge("end_time", nowTime))
                .or(j -> j.eq("start_time", 0).eq("end_time", 0)));

        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        //排序
        if (ObjectUtil.isEmpty(searchParam.getOrder()) && (searchParam.getOrder().equals("create_time") || searchParam.getOrder().equals("price"))){
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        }else{
            queryWrapper.orderByDesc("id");
        }

        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);

        String cacheName = "coupon_goods_list";
        String cacheTagName = cacheName;
        List<CouponGoodsVo> goodsVosList = (List<CouponGoodsVo>)cached.tag(cacheTagName).get(cacheName);

        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon item : iPage.getRecords()) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(item, vo);
            
            // 手动设置时间字段
            vo.setStartTime(item.getStartTime());
            vo.setEndTime(item.getEndTime());
            
            // 转换 joinSiteIds 从字符串到 List<Integer>
            if (ObjectUtil.isNotEmpty(item.getJoinSiteIds())) {
                try {
                    JSONArray jsonArray = JSONUtil.parseArray(item.getJoinSiteIds());
                    List<Integer> siteIds = new ArrayList<>();
                    for (Object siteObjId : jsonArray) {
                        siteIds.add(Integer.parseInt(siteObjId.toString()));
                    }
                    vo.setJoinSiteIds(siteIds);
                } catch (Exception e) {
                    vo.setJoinSiteIds(new ArrayList<>());
                }
            } else {
                vo.setJoinSiteIds(new ArrayList<>());
            }
            
            // 设置 sumCount
            if (vo.getRemainCount() != null && vo.getRemainCount() == -1) {
                vo.setSumCount("-1");
            } else if (vo.getRemainCount() != null && vo.getReceiveCount() != null) {
                vo.setSumCount(String.valueOf(vo.getRemainCount() + vo.getReceiveCount()));
            } else {
                vo.setSumCount("0");
            }
            
            // 获取优惠券关联的商品信息
            // 1. 先从缓存获取
            MallCouponListVo.CouponGoods couponGoods = null;
            if (ObjectUtil.isNotEmpty(goodsVosList)){
                List<CouponGoodsVo> couponGoodsThis = goodsVosList.stream().filter(i -> i.getCouponId().equals(item.getId())).toList();
                if (!couponGoodsThis.isEmpty()){
                    couponGoods = new MallCouponListVo.CouponGoods();
                    BeanUtils.copyProperties(couponGoodsThis.get(0), couponGoods);
                }
            }
            
            // 2. 如果缓存中没有，实时查询数据库（与PHP的getCouponGoodsOne逻辑一致）
            if (couponGoods == null) {
                List<CouponGoodsVo> couponGoodsList = coreGoodsCouponService.getCouponGoodsOne(item.getId());
                if (!couponGoodsList.isEmpty()) {
                    couponGoods = new MallCouponListVo.CouponGoods();
                    BeanUtils.copyProperties(couponGoodsList.get(0), couponGoods);
                }
            }
            
            vo.setGoods(couponGoods);

            if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
                Long couponMemberUsableCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                        .eq("member_id", RequestUtils.memberId())
                        .eq("coupon_id", item.getId())
                        .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
                Long couponMemberCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                        .eq("member_id", RequestUtils.memberId())
                        .eq("coupon_id", item.getId())
                        .eq("receive_type", "receive"));
                if (couponMemberCount > 0) {
                    vo.setIsReceive(1);
                } else {
                    vo.setIsReceive(0);
                }
                if(couponMemberUsableCount>0){
                    vo.setNeedReceive(0);
                }else{
                    vo.setNeedReceive(1);
                }
                vo.setMemberReceiveCount(couponMemberCount);
            } else {
                vo.setIsReceive(0);
                vo.setMemberReceiveCount(0L);
            }

            //在H5领券中心，适用店铺，如果siteID等于0，则表示适用全部店铺
            if(vo.getSiteId().equals(RequestUtils.defaultSiteId())){
                if(CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(item.getIsAllSiteJoin())){
                    vo.setSiteName("全部店铺");
                }else{
                    List<Integer> siteIds = vo.getJoinSiteIds();
                    if(!siteIds.isEmpty()){
                        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().in("site_id", siteIds));
                        if(!siteList.isEmpty()){
                            vo.setSiteName(siteList.stream().map(Site::getSiteName).collect(Collectors.joining("、")));
                        }
                    }
                }
            }else{
                //则显示该站点名称
                SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
                vo.setSiteName(siteInfoVo.getSiteName());
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
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
    public MallCouponInfoVo getDetail(Integer couponId) {
        MallCoupon model = mallCouponMapper.selectOne(
                new QueryWrapper<MallCoupon>()
                        .eq("id", couponId)
                        .last("limit 1"));
        MallCouponInfoVo vo = new MallCouponInfoVo();
        if (ObjectUtil.isEmpty(model)) return null;
        BeanUtils.copyProperties(model, vo);

        if (vo.getRemainCount() == -1) {
            vo.setSumCount("-1");
        } else {
            vo.setSumCount(Integer.toString(vo.getRemainCount() + vo.getReceiveCount()));
        }

        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            Long couponMemberCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                    .eq("member_id", RequestUtils.memberId()).eq("coupon_id", couponId));

            if (couponMemberCount > 0) {
                vo.setIsReceive(1);
                vo.setMemberReceiveCount(couponMemberCount.intValue());
            }
        }

        return vo;
    }

    @Override
    @Transactional
    public void receive(MallCouponReceiveParam mallCouponReceiveParam) {

        Integer memberId = RequestUtils.memberId();
        List<String> receiveType = this.getReceiveType();
        if (!receiveType.contains(mallCouponReceiveParam.getType())) {
            throw new ApiException("优惠券领取方式不存在");
        }

        //判断是否领取过
        Long count = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                .eq("coupon_id", mallCouponReceiveParam.getCouponId())
                .eq("member_id", memberId)
                .eq("receive_type", mallCouponReceiveParam.getType()));

        //判断优惠券是否充足
        MallCoupon mallCoupon = mallCouponMapper.selectOne(new QueryWrapper<MallCoupon>().eq("id", mallCouponReceiveParam.getCouponId()));

        if (ObjectUtil.isEmpty(mallCoupon)) throw new ApiException("优惠券已领完");
        if (mallCoupon.getStatus() == -1) throw new ApiException("优惠券已失效");
        if (count.intValue() >= mallCoupon.getLimitCount()) throw new ApiException("领用次数已达到上限");
        if (mallCoupon.getRemainCount() == 0) throw new ApiException("优惠券已领完");
        Long time = System.currentTimeMillis() / 1000;
        if (mallCoupon.getStartTime() > 0) {
            if (mallCoupon.getStartTime() > time) throw new ApiException("优惠券未开始");
            if (mallCoupon.getEndTime() < time) throw new ApiException("优惠券已过期");
        }
        if (mallCoupon.getValidEndTime() > 0) {
            if (mallCoupon.getValidStartTime() > time) throw new ApiException("优惠券未开始");
            if (mallCoupon.getValidEndTime() < time) throw new ApiException("优惠券已过期");
        }
        MallCoupon updateCoupon = new MallCoupon();

        if (mallCoupon.getRemainCount() != -1) {
            updateCoupon.setRemainCount(mallCoupon.getRemainCount() - mallCouponReceiveParam.getNumber());
            updateCoupon.setReceiveCount(mallCoupon.getReceiveCount() + mallCouponReceiveParam.getNumber());
        } else {
            updateCoupon.setReceiveCount(mallCoupon.getReceiveCount() + mallCouponReceiveParam.getNumber());
        }
        mallCouponMapper.update(updateCoupon, new QueryWrapper<MallCoupon>().eq("id", mallCoupon.getId()));

        Long expireTime;
        if (mallCoupon.getValidType() == 1) {
            expireTime = time + Long.valueOf(86400 * mallCoupon.getLength());
        } else {
            expireTime = mallCoupon.getValidEndTime();
        }
        MallCouponMember mallCouponMember = new MallCouponMember();
        mallCouponMember.setCouponId(mallCoupon.getId());
        mallCouponMember.setSiteId((mallCoupon.getSiteId()));
        mallCouponMember.setMemberId(memberId);
        mallCouponMember.setCreateTime(time);
        mallCouponMember.setExpireTime(expireTime);
        mallCouponMember.setReceiveType(mallCouponReceiveParam.getType());
        mallCouponMember.setType(mallCoupon.getType().toString());
        mallCouponMember.setTitle(mallCoupon.getTitle());
        mallCouponMember.setPrice(mallCoupon.getPrice());
        mallCouponMember.setStatus(CouponMemberStatusEnum.WAIT_USE.getStatus());
        mallCouponMember.setMinConditionMoney(mallCoupon.getMinConditionMoney());
        mallCouponMemberMapper.insert(mallCouponMember);
    }

    @Override
    public PageResult<MallCouponMemberListVo> getMemberPage(PageParam pageParam, Integer status,String type ) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCouponMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        List<String> typeList = Arrays.asList(CouponTypeEnum.ALL.getType().toString(), CouponTypeEnum.CATEGORY.getType().toString(), CouponTypeEnum.GOODS.getType().toString(), CouponTypeEnum.BRAND.getType().toString(), CouponTypeEnum.MALL_CATEGORY.getType().toString());
        if (type != null && !type.equals("all") && typeList.contains(type)) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderByDesc("id");

        IPage<MallCouponMember> iPage = mallCouponMemberMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<MallCoupon> couponsList = new ArrayList<>();
        List<Site> siteList = new ArrayList<>();
        List<Integer> couponIds = iPage.getRecords().stream().map(MallCouponMember::getCouponId).toList();
        if (!couponIds.isEmpty()){
            couponsList = mallCouponMapper.selectByIds(couponIds);
            List<Integer> siteIds = new ArrayList<>();
            if (!couponsList.isEmpty()){
                for (MallCoupon item : couponsList){
                    if(!CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(item.getIsAllSiteJoin())){
                        JSONArray jsonArray = JSONUtil.parseArray(item.getJoinSiteIds());
                        for (Object siteId : jsonArray) {
                            siteIds.add(Integer.parseInt(siteId.toString()));
                        }
                    }
                    siteIds.add(item.getSiteId());
                }
            }
            if (!siteIds.isEmpty()){
                siteList = siteMapper.selectList(new QueryWrapper<Site>().in("site_id", siteIds));
            }
        }
        List<MallCouponMemberListVo> list = new LinkedList<>();
        for (MallCouponMember item : iPage.getRecords()) {
            MallCouponMemberListVo vo = new MallCouponMemberListVo();
            BeanUtils.copyProperties(item, vo);
            if(RequestUtils.defaultSiteId().equals(item.getSiteId())){
                List<MallCoupon> coupons = couponsList.stream().filter(coupon -> coupon.getId().equals(item.getCouponId())).toList();
                if(!coupons.isEmpty()){
                    MallCoupon coupon = coupons.get(0);
                    if(CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(coupon.getIsAllSiteJoin())){
                        vo.setSiteName("全部店铺");
                    }else{
                        List<Integer> siteIds = new ArrayList<>();
                        JSONArray jsonArray = JSONUtil.parseArray(coupon.getJoinSiteIds());
                        for (Object siteId : jsonArray) {
                            siteIds.add(Integer.parseInt(siteId.toString()));
                        }
                        if(!siteIds.isEmpty()){
                            List<Site> siteListThis = siteList.stream().filter(site -> siteIds.contains(site.getSiteId())).toList();
                            if(!siteListThis.isEmpty()){
                                vo.setSiteName(siteListThis.stream().map(Site::getSiteName).collect(Collectors.joining("、")));
                            }
                        }
                    }
                }else{
                    vo.setSiteName("无");
                }
            }else {
                //则显示该站点名称
                List<Site> siteListThis = siteList.stream().filter(site -> site.getSiteId().equals(vo.getSiteId())).toList();
                if(!siteListThis.isEmpty()){
                    vo.setSiteName(siteListThis.get(0).getSiteName());
                }

            }
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<JSONObject> getCouponCountByStatus() {

        List<JSONObject> list = new ArrayList<>();

        Map<Integer, String> statusMap = CouponMemberStatusEnum.getMap();

        for (Map.Entry<Integer, String> entry : statusMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            Long count = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                    .eq("member_id", RequestUtils.memberId())
                    .eq("status", entry.getKey()));
            if (count == null) {
                count = 0L;
            }
            jsonObject.put("status", entry.getKey());
            jsonObject.put("status_name", entry.getValue());
            jsonObject.put("count", count);
            list.add(jsonObject);
        }

        return list;
    }

    @Override
    public Long getMemberCount() {

        return mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                .eq("member_id", RequestUtils.memberId()));
    }

    @Override
    public Long getMemberCount(Integer status) {

        return mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                .eq("member_id", RequestUtils.memberId())
                .eq("status", status));
    }

    @Override
    public List<String> getReceiveType() {
        JSONArray jsonArray = CouponMemberReceiveTypeEnum.getReceiveType();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getJSONObject(i).getStr("name"));
        }
        return list;

    }

    @Override
    public JSONArray getCouponType() {

        Map<Integer, String> couponTypeMap = CouponTypeEnum.getMap();
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, String> entry : couponTypeMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label", entry.getValue());
            jsonObject.put("value", entry.getKey());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public List<MallCouponListVo> getCouponComponents(String siteId, String couponIds, Integer num) {

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        queryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());
        if (!couponIds.equals("")) {
            queryWrapper.in("coupon_id", Arrays.asList(couponIds.split(",")));
        }
        if(ObjectUtil.isNotEmpty(siteId))
        {
            queryWrapper.eq("site_id", siteId);
        }

        //根据时间查询
        Long nowTime = System.currentTimeMillis() / 1000;
        queryWrapper.and(i -> i.and(j -> j.le("start_time", nowTime).ge("end_time", nowTime))
                .or(j -> j.eq("start_time", 0).eq("end_time", 0)));
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit " + num);
        List<MallCoupon> mallCouponList = mallCouponMapper.selectList(queryWrapper);
        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon mallCoupon : mallCouponList) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(mallCoupon, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<MallCouponListVo> getPlatformCouponComponents(String couponIds, Integer num) {
        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        queryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        if (!couponIds.equals("")) {
            queryWrapper.in("coupon_id", Arrays.asList(couponIds.split(",")));
        }

        //根据时间查询
        Long nowTime = System.currentTimeMillis() / 1000;
        queryWrapper.and(i -> i.and(j -> j.le("start_time", nowTime).ge("end_time", nowTime))
                .or(j -> j.eq("start_time", 0).eq("end_time", 0)));
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit " + num);
        List<MallCoupon> mallCouponList = mallCouponMapper.selectList(queryWrapper);
        List<MallCouponListVo> list = new LinkedList<>();
        for (MallCoupon mallCoupon : mallCouponList) {
            MallCouponListVo vo = new MallCouponListVo();
            BeanUtils.copyProperties(mallCoupon, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public String getQrcode(Integer couponId) {
        return null;
    }


}
