package com.niu.mall.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreSysConfigService;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    @Resource
    ICoreSysConfigService coreSysConfigService;

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
        if (ObjectUtil.isEmpty(searchParam.getOrder()) && (searchParam.getOrder().equals("create_time") || searchParam.getOrder().equals("price"))) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderByDesc("id");
        }

        IPage<MallCoupon> iPage = mallCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);

        String cacheName = "coupon_goods_list";
        String cacheTagName = cacheName;
        List<CouponGoodsVo> goodsVosList = (List<CouponGoodsVo>) cached.tag(cacheTagName).get(cacheName);

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
            if (ObjectUtil.isNotEmpty(goodsVosList)) {
                List<CouponGoodsVo> couponGoodsThis = goodsVosList.stream().filter(i -> i.getCouponId().equals(item.getId())).toList();
                if (!couponGoodsThis.isEmpty()) {
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
                if (couponMemberUsableCount > 0) {
                    vo.setNeedReceive(0);
                } else {
                    vo.setNeedReceive(1);
                }
                vo.setMemberReceiveCount(couponMemberCount);
            } else {
                vo.setIsReceive(0);
                vo.setMemberReceiveCount(0L);
            }

            //在H5领券中心，适用店铺，如果siteID等于0，则表示适用全部店铺
            if (vo.getSiteId().equals(RequestUtils.defaultSiteId())) {
                if (CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(item.getIsAllSiteJoin())) {
                    vo.setSiteName("全部店铺");
                } else {
                    List<Integer> siteIds = vo.getJoinSiteIds();
                    if (!siteIds.isEmpty()) {
                        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().in("site_id", siteIds));
                        if (!siteList.isEmpty()) {
                            vo.setSiteName(siteList.stream().map(Site::getSiteName).collect(Collectors.joining("、")));
                        }
                    }
                }
            } else {
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
            Long couponMemberUsableCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                    .eq("member_id", RequestUtils.memberId())
                    .eq("coupon_id", couponId)
                    .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
            Long couponMemberCount = mallCouponMemberMapper.selectCount(new QueryWrapper<MallCouponMember>()
                    .eq("member_id", RequestUtils.memberId())
                    .eq("coupon_id", couponId)
                    .eq("receive_type", "receive"));
            if (couponMemberCount > 0) {
                vo.setIsReceive(1);
            } else {
                vo.setIsReceive(0);
            }
            if (couponMemberUsableCount > 0) {
                vo.setNeedReceive(0);
            } else {
                vo.setNeedReceive(1);
            }
            vo.setMemberReceiveCount(couponMemberCount.intValue());
        } else {
            vo.setIsReceive(0);
            vo.setMemberReceiveCount(0);
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
        if (mallCoupon.getValidEndTime() > 0 && mallCoupon.getValidType().equals(2)) {
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
    public PageResult<MallCouponMemberListVo> getMemberPage(PageParam pageParam, Integer status, String type) {

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
        if (!couponIds.isEmpty()) {
            couponsList = mallCouponMapper.selectByIds(couponIds);
            List<Integer> siteIds = new ArrayList<>();
            if (!couponsList.isEmpty()) {
                for (MallCoupon item : couponsList) {
                    if (!CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(item.getIsAllSiteJoin())) {
                        JSONArray jsonArray = JSONUtil.parseArray(item.getJoinSiteIds());
                        for (Object siteId : jsonArray) {
                            siteIds.add(Integer.parseInt(siteId.toString()));
                        }
                    }
                    siteIds.add(item.getSiteId());
                }
            }
            if (!siteIds.isEmpty()) {
                siteList = siteMapper.selectList(new QueryWrapper<Site>().in("site_id", siteIds));
            }
        }
        List<MallCouponMemberListVo> list = new LinkedList<>();
        for (MallCouponMember item : iPage.getRecords()) {
            MallCouponMemberListVo vo = new MallCouponMemberListVo();
            BeanUtils.copyProperties(item, vo);
            if (RequestUtils.defaultSiteId().equals(item.getSiteId())) {
                List<MallCoupon> coupons = couponsList.stream().filter(coupon -> coupon.getId().equals(item.getCouponId())).toList();
                if (!coupons.isEmpty()) {
                    MallCoupon coupon = coupons.get(0);
                    if (CouponIsAllSiteEnum.ALL_SITE_JOIN.getType().equals(coupon.getIsAllSiteJoin())) {
                        vo.setSiteName("全部店铺");
                    } else {
                        List<Integer> siteIds = new ArrayList<>();
                        JSONArray jsonArray = JSONUtil.parseArray(coupon.getJoinSiteIds());
                        for (Object siteId : jsonArray) {
                            siteIds.add(Integer.parseInt(siteId.toString()));
                        }
                        if (!siteIds.isEmpty()) {
                            List<Site> siteListThis = siteList.stream().filter(site -> siteIds.contains(site.getSiteId())).toList();
                            if (!siteListThis.isEmpty()) {
                                vo.setSiteName(siteListThis.stream().map(Site::getSiteName).collect(Collectors.joining("、")));
                            }
                        }
                    }
                } else {
                    vo.setSiteName("无");
                }
            } else {
                //则显示该站点名称
                List<Site> siteListThis = siteList.stream().filter(site -> site.getSiteId().equals(vo.getSiteId())).toList();
                if (!siteListThis.isEmpty()) {
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
        if (ObjectUtil.isNotEmpty(siteId)) {
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
        MallCoupon coupon = mallCouponMapper.selectOne(new QueryWrapper<MallCoupon>()
                .eq("id", couponId).last("limit 1"));
        if (ObjectUtil.isEmpty(coupon)) {
            throw new ApiException("优惠券不存在");
        }
        Map<String, Object> data = new HashMap<>();
        Integer memberId = RequestUtils.memberId();
        if (ObjectUtil.isNotEmpty(memberId)) {
            data.put("mid", memberId);
        }
        data.put("coupon_id", couponId);
        return QrcodeUtils.qrcodeToFile(
                coupon.getSiteId(), "weapp",
                coreSysConfigService.getSceneDomain().getWapUrl(),
                "addon/mall/pages/coupon/detail",
                data,
                "upload/" + coupon.getSiteId() + "/shop_coupon_qrcode"
        );
    }

    @Override
    public List<Map<String, Object>> getGoodsDetailCouponLists(Integer goodsId) {
        // 1. 查询商品信息
        MallGoods goods = mallGoodsMapper.selectById(goodsId);
        if (goods == null) {
            return Collections.emptyList();
        }
        Integer siteId = goods.getSiteId();

        // 2. 查询与商品相关的优惠券ID（商品券、分类券、平台分类券、品牌券）
        Set<Integer> couponIds = new HashSet<>();

        // 商品券
        List<MallCouponGoods> goodsCoupons = mallCouponGoodsMapper.selectList(
                new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getGoodsId, goodsId));
        if (!goodsCoupons.isEmpty()) {
            couponIds.addAll(goodsCoupons.stream().map(MallCouponGoods::getCouponId).collect(Collectors.toList()));
        } else {
            couponIds.add(-1); // 占位，保证后面查询不出结果
        }

        // 分类券（商品分类）
        if (StringUtils.hasText(goods.getGoodsCategory())) {
            List<Integer> categoryIds = JSONUtil.toList(goods.getGoodsCategory(), Integer.class);
            if (!categoryIds.isEmpty()) {
                List<MallCouponGoods> categoryCoupons = mallCouponGoodsMapper.selectList(
                        new LambdaQueryWrapper<MallCouponGoods>().in(MallCouponGoods::getCategoryId, categoryIds));
                if (!categoryCoupons.isEmpty()) {
                    couponIds.addAll(categoryCoupons.stream().map(MallCouponGoods::getCouponId).collect(Collectors.toList()));
                } else {
                    couponIds.add(-1);
                }
            }
        }

        // 平台分类券
        if (goods.getGoodsMallCategory() != null) {
            List<MallCouponGoods> mallCategoryCoupons = mallCouponGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getMallCategoryId, goods.getGoodsMallCategory()));
            if (!mallCategoryCoupons.isEmpty()) {
                couponIds.addAll(mallCategoryCoupons.stream().map(MallCouponGoods::getCouponId).collect(Collectors.toList()));
            } else {
                couponIds.add(-1);
            }
        }

        // 品牌券
        if (goods.getBrandId() != null && goods.getBrandId() > 0) {
            List<MallCouponGoods> brandCoupons = mallCouponGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallCouponGoods>().eq(MallCouponGoods::getBrandId, goods.getBrandId()));
            if (!brandCoupons.isEmpty()) {
                couponIds.addAll(brandCoupons.stream().map(MallCouponGoods::getCouponId).collect(Collectors.toList()));
            } else {
                couponIds.add(-1);
            }
        }

        // 3. 构建查询条件
        // 基础条件
        LambdaQueryWrapper<MallCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallCoupon::getReceiveType, CouponReceiveTypeEnum.USER.getType())
                .eq(MallCoupon::getStatus, CouponStatusEnum.NORMAL.getStatus())
                .in(MallCoupon::getSiteId, Arrays.asList(siteId, 0L))
                // 组合条件：通用券 OR 各类关联券
                .and(w -> {
                    w.eq(MallCoupon::getType, CouponTypeEnum.ALL.getType());
                    if (!couponIds.isEmpty() && !couponIds.contains(-1)) {
                        w.or().in(MallCoupon::getId, couponIds);
                    }
                });
        // 时间条件（当前时间在有效期内）
        long now = System.currentTimeMillis() / 1000;
        wrapper.le(MallCoupon::getStartTime, now)
                .ge(MallCoupon::getEndTime, now);
        // 排序
        wrapper.orderByDesc(MallCoupon::getId);
        // 指定查询字段（对应 PHP 的 $field）
        wrapper.select(MallCoupon::getId, MallCoupon::getSiteId, MallCoupon::getTitle,
                MallCoupon::getStartTime, MallCoupon::getEndTime, MallCoupon::getRemainCount,
                MallCoupon::getReceiveCount, MallCoupon::getLimitCount, MallCoupon::getStatus,
                MallCoupon::getPrice, MallCoupon::getMinConditionMoney, MallCoupon::getType,
                MallCoupon::getReceiveType, MallCoupon::getValidType, MallCoupon::getLength,
                MallCoupon::getValidStartTime, MallCoupon::getValidEndTime, MallCoupon::getSort,
                MallCoupon::getJoinSiteIds, MallCoupon::getIsAllSiteJoin);

        List<MallCoupon> couponList = mallCouponMapper.selectList(wrapper);

        // 4. 查询会员已领取的优惠券信息（用于计算 is_receive, need_receive 等）
        Map<Integer, Integer> memberReceiveCountMap = new HashMap<>();
        Map<Integer, Integer> memberUsableCountMap = new HashMap<>();

        Integer memberId = RequestUtils.memberId();
        if (memberId != null) {
            LambdaQueryWrapper<MallCouponMember> memberWrapper = new LambdaQueryWrapper<MallCouponMember>()
                    .eq(MallCouponMember::getMemberId, memberId);
            if (couponList != null && !couponList.isEmpty())
                memberWrapper.in(MallCouponMember::getCouponId, couponList.stream().map(MallCoupon::getId).collect(Collectors.toList()));
            List<MallCouponMember> memberCoupons = mallCouponMemberMapper.selectList(memberWrapper);
            for (MallCouponMember cm : memberCoupons) {
                memberReceiveCountMap.merge(cm.getCouponId(), 1, Integer::sum);
                if (cm.getStatus().equals(CouponMemberStatusEnum.WAIT_USE.getStatus())) {
                    memberUsableCountMap.merge(cm.getCouponId(), 1, Integer::sum);
                }
            }
        }

        // 5. 组装返回数据，添加附加字段
        List<Map<String, Object>> result = new ArrayList<>();
        if (couponList != null) {
            for (MallCoupon coupon : couponList) {
                Map<String, Object> item = new HashMap<>();
                BeanUtils.copyProperties(coupon, item); // 复制基本字段

                // 处理 sum_count
                if (coupon.getRemainCount() != -1) {
                    item.put("sum_count", coupon.getRemainCount() + coupon.getReceiveCount());
                } else {
                    item.put("sum_count", -1);
                }

                // 会员相关字段
                int receiveCount = memberReceiveCountMap.getOrDefault(coupon.getId(), 0);
                int usableCount = memberUsableCountMap.getOrDefault(coupon.getId(), 0);
                item.put("member_receive_count", receiveCount);
                item.put("coupon_member_usable_count", usableCount);
                item.put("is_receive", receiveCount > 0 ? 1 : 0);
                item.put("need_receive", usableCount > 0 ? 0 : 1);

                // 站点范围校验（is_all_site_join == 0 且当前站点不在 join_site_ids 中则跳过）
                if (coupon.getIsAllSiteJoin().equals(1)) {
                    List<Long> joinSiteIds = parseJoinSiteIds(coupon.getJoinSiteIds());
                    if (!joinSiteIds.contains(siteId)) {
                        continue; // 不满足条件的优惠券不加入结果
                    }
                }

                // 附加虚拟字段（需根据字典转换）
                item.put("coupon_price", coupon.getPrice());       // 优惠券面额（带货币符号？）
                item.put("coupon_min_price", coupon.getMinConditionMoney());
                item.put("receive_type_name", CouponReceiveTypeEnum.getNameByType(coupon.getReceiveType()));
                item.put("type_name", CouponTypeEnum.getNameByType(coupon.getType()));

                result.add(item);
            }
        }
        return result;
    }

    @Override
    /**
     * 计算会员店铺和平台分别可享受的最佳优惠券优惠金额
     * 1. site_id=0 为平台优惠券，否则为店铺优惠券
     * 2. limit_count：单个会员限制领取数量
     * 3. remain_count：优惠券剩余数量，-1 表示无限量
     * 4. member_receive_count：当前会员已领取数量
     * 5. coupon_member_usable_count：当前会员可使用优惠券数量
     * @param couponList 优惠券列表（每个元素为 Map，包含上述字段）
     * @param goodsMoney 商品金额
     * @param memberId   当前会员ID（可为 null）
     * @return 包含 site_coupon_price 和 admin_coupon_price 的 Map
     */
    public Map<String, BigDecimal> calcMemberCouponPromotion(List<Map<String, Object>> couponList,
                                                             BigDecimal goodsMoney,
                                                             Integer memberId) {
        // 初始化返回结果
        BigDecimal siteCouponPrice = BigDecimal.ZERO;
        BigDecimal adminCouponPrice = BigDecimal.ZERO;

        // 如果没有优惠券列表，直接返回 0
        if (couponList == null || couponList.isEmpty()) {
            return Map.of("site_coupon_price", siteCouponPrice,
                    "admin_coupon_price", adminCouponPrice);
        }

        for (Map<String, Object> coupon : couponList) {
            // 获取必要字段（注意类型转换）
            Integer couponMemberUsableCount = getInt(coupon.get("coupon_member_usable_count"));
            Integer memberReceiveCount = getInt(coupon.get("member_receive_count"));
            Integer limitCount = getInt(coupon.get("limit_count"));
            Integer remainCount = getInt(coupon.get("remain_count"));
            BigDecimal minConditionMoney = getBigDecimal(coupon.get("min_condition_money"));
            Integer siteId = getInt(coupon.get("site_id"));
            BigDecimal couponPrice = getBigDecimal(coupon.get("coupon_price"));

            // 过滤已达领取使用上限的优惠券
            if (memberId != null && couponMemberUsableCount == 0 && memberReceiveCount >= limitCount) {
                continue;
            }

            // 过滤剩余数量为 0 的优惠券（remain_count == 0 跳过，-1 表示无限量，不跳过）
            if (remainCount != null && remainCount == 0) {
                continue;
            }

            // 过滤未达使用门槛的优惠券
            if (minConditionMoney != null && minConditionMoney.compareTo(BigDecimal.ZERO) > 0) {
                // bcsub($goods_money, $min_condition_money, 2) < 0 等价于 goods_money < min_condition_money
                if (goodsMoney.compareTo(minConditionMoney) < 0) {
                    continue;
                }
            }

            // 区分店铺优惠券和平台优惠券，取最大值
            if (siteId != null && siteId == 0) {
                // 平台优惠券
                if (couponPrice != null && couponPrice.compareTo(adminCouponPrice) > 0) {
                    adminCouponPrice = couponPrice;
                }
            } else {
                // 店铺优惠券（site_id 不为 0 或为 null 时按店铺处理）
                if (couponPrice != null && couponPrice.compareTo(siteCouponPrice) > 0) {
                    siteCouponPrice = couponPrice;
                }
            }
        }

        // 优惠金额不能超过商品金额（先限制店铺优惠）
        if (siteCouponPrice.compareTo(goodsMoney) > 0) {
            siteCouponPrice = goodsMoney;
        }
        // 剩余可优惠金额 = 商品金额 - 店铺优惠
        BigDecimal remaining = goodsMoney.subtract(siteCouponPrice);
        if (adminCouponPrice.compareTo(remaining) > 0) {
            adminCouponPrice = remaining;
        }

        // 返回结果
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("site_coupon_price", siteCouponPrice);
        result.put("admin_coupon_price", adminCouponPrice);
        return result;
    }

    // 辅助方法：安全获取 Integer 值
    private Integer getInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 辅助方法：安全获取 BigDecimal 值
    private BigDecimal getBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(((Number) value).toString());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private List<Long> parseJoinSiteIds(String joinSiteIdsJson) {
        if (joinSiteIdsJson.isBlank()) return Collections.emptyList();
        try {
            return JSON.parseObject(joinSiteIdsJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
