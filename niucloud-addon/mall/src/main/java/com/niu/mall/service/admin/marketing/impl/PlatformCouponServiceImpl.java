package com.niu.mall.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.admin.goods.IGoodsCategoryService;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.SiteShopSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.admin.shop.vo.SiteShopListVo;
import com.niu.mall.entity.coupon.MallCoupon;
import com.niu.mall.entity.coupon.MallCouponGoods;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.enums.coupon.CouponMemberStatusEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.mapper.coupon.MallCouponGoodsMapper;
import com.niu.mall.mapper.coupon.MallCouponMapper;
import com.niu.mall.mapper.coupon.MallCouponMemberMapper;
import com.niu.mall.service.admin.marketing.IMallCouponGoodsService;
import com.niu.mall.service.admin.marketing.IPlatformCouponService;
import com.niu.mall.service.admin.marketing.param.MallCouponParam;
import com.niu.mall.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.site.marketing.param.MallCouponSendParam;
import com.niu.mall.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.admin.marketing.vo.MallCouponListVo;
import com.niu.mall.service.admin.marketing.vo.MallCouponMemberListVo;
import com.niu.mall.service.admin.marketing.vo.MallCouponSelectVo;
import com.niu.mall.service.core.marketing.ICoreActiveGoodsService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class
PlatformCouponServiceImpl implements IPlatformCouponService {

    @Resource
    private IGoodsCategoryService goodsCategoryService;

    @Resource
    private ISiteShopCategoryService shopCategoryService;

    @Resource
    MallCouponMapper mallCouponMapper;

    @Resource
    MallCouponGoodsMapper mallCouponGoodsMapper;

    @Resource
    MallCouponMemberMapper mallCouponMemberMapper;

    @Resource
    IMallCouponGoodsService shopCouponGoodsService;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    ISiteShopService siteShopService;
    @Resource
    ICoreActiveGoodsService coreActiveGoodsService;
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
    public JSONObject getInitSiteList() {
        // 查询店铺分类
        List<SiteShopCategoryListVo> shopCategory = shopCategoryService.all();
        JSONArray siteTree = new JSONArray();
        for (SiteShopCategoryListVo category : shopCategory) {
            JSONArray children = new JSONArray();
            SiteShopSearchParam siteShopSearchParam = new SiteShopSearchParam();
            siteShopSearchParam.setCategoryId(category.getCategoryId());
            List<SiteShopListVo> shopList = siteShopService.list(siteShopSearchParam);
            if (!shopList.isEmpty()) {
                for (SiteShopListVo shop : shopList) {
                    JSONObject child = new JSONObject();
                    child.put("value", shop.getSiteId());
                    child.put("label", shop.getSiteName());
                    children.add(child);
                }
                JSONObject categoryNode = new JSONObject();
                categoryNode.put("value", category.getCategoryId());
                categoryNode.put("label", category.getCategoryName());
                categoryNode.put("children", children);
                siteTree.add(categoryNode);
            }
        }
        JSONObject res = new JSONObject();
        res.put("site_tree", siteTree);
        return res;
    }

    @Override
    public PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
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
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
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
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        if (!couponIds.equals("")) queryWrapper.in("id", couponIds.split(","));

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
                        .eq("site_id", RequestUtils.defaultSiteId())
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

        Integer siteId = RequestUtils.defaultSiteId();
        MallCoupon model = new MallCoupon();
        model.setSiteId(siteId);

        if (addParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(addParam.getMinConditionMoney());
        }
        if (ObjectUtil.isNotEmpty(addParam.getIsAllSiteJoin())) {
            model.setIsAllSiteJoin(addParam.getIsAllSiteJoin());
            if (ObjectUtil.isNotEmpty(addParam.getJoinSiteIds())) {
                model.setJoinSiteIds(JSONUtil.parseArray(addParam.getJoinSiteIds()).toString());
            }
        } else {
            model.setIsAllSiteJoin(0);
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


        if (ObjectUtil.isNotEmpty(addParam.getBrandIds()) || ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {


            if (ObjectUtil.isNotEmpty(addParam.getBrandIds())) {
                List<MallCouponGoods> list = new ArrayList<>();
                for (Integer brandId : addParam.getBrandIds()) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    mallCouponGoods.setBrandId(brandId);
                    list.add(mallCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
                //同步优惠券商品
                coreActiveGoodsService.couponGoodsCache();
            }
            if (ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = addParam.getGoodsCategoryIds();
                List<MallCouponGoods> list = new ArrayList<>();
                if (!isTwoDimensionalArray(goodsCategoryIds)) {
                    for (Object goodsCategoryId : goodsCategoryIds) {
                        MallCouponGoods mallCouponGoods = new MallCouponGoods();
                        mallCouponGoods.setSiteId(siteId);
                        mallCouponGoods.setCouponId(model.getId());
                        JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                        mallCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));
                        list.add(mallCouponGoods);
                    }
                } else {
                    // 二维数组：每个元素是一个路径数组，只保留叶子节点（最后一个），并去重（出现次数为1才保留）
                    Map<Integer, Integer> countMap = new HashMap<>();
                    for (Object item : goodsCategoryIds) {
                        JSONArray path = (JSONArray) item;
                        if (path.isEmpty()) continue;
                        Integer leafId = path.getInt(path.size() - 1);
                        countMap.put(leafId, countMap.getOrDefault(leafId, 0) + 1);
                    }
                    for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
                        if (entry.getValue() == 1) {  // 仅出现一次的保留
                            MallCouponGoods relation = new MallCouponGoods();
                            relation.setSiteId(siteId);
                            relation.setCouponId(model.getId());
                            relation.setCategoryId(entry.getKey());
                            list.add(relation);
                        }
                    }
                }
                shopCouponGoodsService.insertAll(list);
                //同步优惠券商品
                coreActiveGoodsService.couponGoodsCache();
            }

        } else {
            //实现关联表中添加数据
            MallCouponGoods mallCouponGoods = new MallCouponGoods();
            mallCouponGoods.setSiteId(siteId);//店铺ID 全部店铺为0
            mallCouponGoods.setCouponId(model.getId());//优惠券ID
            mallCouponGoodsMapper.insert(mallCouponGoods);
            //同步优惠券商品
            coreActiveGoodsService.couponGoodsCache();
        }
    }

    @Override
    public void edit(Integer couponId, MallCouponParam mallCouponParam) {
        MallCoupon coupon = mallCouponMapper.selectById(mallCouponParam.getId());
/*        if (!coupon.getStatus().equals(CouponStatusEnum.WAIT_START.getStatus())) {
            throw new RuntimeException("只能编辑未开始状态的优惠券");
        }*/


        Integer siteId = RequestUtils.defaultSiteId();
        MallCoupon model = new MallCoupon();
        model.setSiteId(siteId);
        model.setId(mallCouponParam.getId());
        if (mallCouponParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(mallCouponParam.getMinConditionMoney());
        }
        if (ObjectUtil.isNotEmpty(mallCouponParam.getIsAllSiteJoin())) {
            model.setIsAllSiteJoin(mallCouponParam.getIsAllSiteJoin());
            if (ObjectUtil.isNotEmpty(mallCouponParam.getJoinSiteIds())) {
                model.setJoinSiteIds(JSONUtil.parseArray(mallCouponParam.getJoinSiteIds()).toString());
            }
        } else {
            model.setIsAllSiteJoin(0);
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

        if (ObjectUtil.isNotEmpty(mallCouponParam.getBrandIds()) || ObjectUtil.isNotEmpty(mallCouponParam.getGoodsCategoryIds())) {
            if (ObjectUtil.isNotEmpty(mallCouponParam.getBrandIds())) {
                Integer[] brandIds = mallCouponParam.getBrandIds();
                List<MallCouponGoods> list = new ArrayList<>();
                for (Integer brandId : brandIds) {
                    MallCouponGoods mallCouponGoods = new MallCouponGoods();
                    mallCouponGoods.setSiteId(siteId);
                    mallCouponGoods.setCouponId(model.getId());
                    mallCouponGoods.setBrandId(brandId);
                    list.add(mallCouponGoods);
                }

                shopCouponGoodsService.insertAll(list);
                //同步优惠券商品
                coreActiveGoodsService.couponGoodsCache();
            }
            if (ObjectUtil.isNotEmpty(mallCouponParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = mallCouponParam.getGoodsCategoryIds();
                List<MallCouponGoods> list = new ArrayList<>();
                if (!isTwoDimensionalArray(goodsCategoryIds)) {
                    for (Object goodsCategoryId : goodsCategoryIds) {
                        MallCouponGoods mallCouponGoods = new MallCouponGoods();
                        mallCouponGoods.setSiteId(siteId);
                        mallCouponGoods.setCouponId(model.getId());
                        JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                        mallCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));

                        list.add(mallCouponGoods);
                    }
                } else {
                    // 二维数组：每个元素是一个路径数组，只保留叶子节点（最后一个），并去重（出现次数为1才保留）
                    Map<Integer, Integer> countMap = new HashMap<>();
                    for (Object item : goodsCategoryIds) {
                        JSONArray path = (JSONArray) item;
                        if (path.isEmpty()) continue;
                        Integer leafId = path.getInt(path.size() - 1);
                        countMap.put(leafId, countMap.getOrDefault(leafId, 0) + 1);
                    }
                    for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
                        if (entry.getValue() == 1) {  // 仅出现一次的保留
                            MallCouponGoods relation = new MallCouponGoods();
                            relation.setSiteId(siteId);
                            relation.setCouponId(model.getId());
                            relation.setCategoryId(entry.getKey());
                            list.add(relation);
                        }
                    }
                }


                shopCouponGoodsService.insertAll(list);
                //同步优惠券商品
                coreActiveGoodsService.couponGoodsCache();
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
        queryWrapper.eq("c.site_id", RequestUtils.defaultSiteId());
        queryWrapper.eq("c.coupon_id", id);

        if (!keywords.isEmpty()) {
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
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>().eq("id", couponId).eq("site_id", RequestUtils.defaultSiteId()));
    }

    @Override
    public void couponInvalid(Integer couponId) {

        MallCoupon mallCoupon = new MallCoupon();
        mallCoupon.setStatus(CouponStatusEnum.INVALID.getStatus());
        mallCouponMapper.update(mallCoupon, new QueryWrapper<MallCoupon>().eq("id", couponId).eq("site_id", RequestUtils.defaultSiteId()));
    }

    @Override
    public Boolean sendCoupon(List<MallCouponSendParam.CouponData> couponData, List<Integer> memberIds) {
        if (couponData == null || couponData.isEmpty() || memberIds == null || memberIds.isEmpty()) {
            throw new AdminException("MALL_COUPON_SEND_PARAM_ERROR");
        }
        Integer siteId = RequestUtils.defaultSiteId();
        for (Integer memberId : memberIds) {
            for (MallCouponSendParam.CouponData coupon : couponData) {
                Integer couponId = coupon.getCouponId();
                Integer num = coupon.getNum();
                try {
                    coreCouponMemberService.sendCoupon(siteId, memberId, couponId, num, "平台发券");
                } catch (Exception e) {
                    // 记录日志但不中断发放流程
                    System.out.println("平台优惠券发放失败，优惠券ID：" + couponId + "，用户ID：" + memberId + "，数量：" + num + "，错误信息：" + e.getMessage());
                }
            }
        }
        return true;
    }


    /**
     * 判断 JSONArray 是否为二维数组（至少有一个元素是 JSONArray）
     */
    private boolean isTwoDimensionalArray(JSONArray arr) {
        if (arr == null || arr.isEmpty()) {
            return false;
        }
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) instanceof JSONArray) {
                return true;
            }
        }
        return false;
    }
}
