package com.niu.core.service.api.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.service.api.shop.vo.*;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.web.IWebConfigService;
import com.niu.core.service.admin.web.vo.WebConfigVo;
import com.niu.core.service.api.shop.ISiteShopService;
import com.niu.core.service.api.shop.param.SiteShopSearchParam;
import com.niu.core.service.api.shop.param.SiteShopSelectParam;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 店铺扩展实现
 */
@Service
public class SiteShopServiceImpl implements ISiteShopService {
        
    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    SiteShopMemberMapper siteShopMemberMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    IWebConfigService webConfigService;

    @Resource
    ICoreSysConfigService coreSysConfigService;
    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    @Override
    public PageResult<SiteShopListVo> page(PageParam pageParam, SiteShopSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
        queryWrapper.eq("nss.group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
        queryWrapper.eq("nss.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
        queryWrapper.eq("ns.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsSelf())) {
        queryWrapper.eq("nss.is_self", searchParam.getIsSelf());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j-> j.like("ns.keywords", searchParam.getKeyword()))
            );
        }
        queryWrapper.orderByDesc("ns.create_time");
        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo item : iPage.getRecords()) {

            SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
            BeanUtils.copyProperties(item, shop);
            item.setShop(shop);
        }
        return PageResult.build(iPage);
    }

    @Override
    public List<SiteShopListVo> list(SiteShopSearchParam searchParam) {

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("ns.group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("nss.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("ns.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsSelf())) {
            queryWrapper.eq("ns.is_self", searchParam.getIsSelf());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j-> j.like("ns.keywords", searchParam.getKeyword()))
            );
        }
        queryWrapper.orderByDesc("ns.create_time");
        List<SiteShopListVo> list = siteMapper.selectJoinList( SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo item : list) {
            SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
            BeanUtils.copyProperties(item, shop);
            item.setShop(shop);
        }
        return list;

    }

    @Override
    public SiteShopSelectPage getSelectPage(PageParam pageParam, SiteShopSelectParam siteShopSelectParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();
        List<Integer> siteIds = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getVerifySiteIds())) {
            QueryWrapper<Site> siteQueryWrapper = new QueryWrapper<>();
            siteQueryWrapper.in("site_id", siteShopSelectParam.getVerifySiteIds());
            siteQueryWrapper.eq("status", SiteStatusEnum.ON.getCode());
            List<Site> siteList = siteMapper.selectList(siteQueryWrapper);
            siteIds = siteList.stream().map(Site::getSiteId).collect(Collectors.toList());
        }
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.ne("ns.app_type", "admin");
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", siteShopSelectParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getExpireTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.expire_time", siteShopSelectParam.getExpireTime());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getGroupId())) {
            queryWrapper.eq("ns.group_id", siteShopSelectParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getCategoryId())) {
            queryWrapper.eq("ns.category_id", siteShopSelectParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getStatus())) {
            queryWrapper.eq("ns.status", siteShopSelectParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getIsSelf())) {
            queryWrapper.eq("ns.is_self", siteShopSelectParam.getIsSelf());
        }
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", siteShopSelectParam.getKeyword()))
                    .or(j-> j.like("ns.keywords", siteShopSelectParam.getKeyword()))
            );
        }
        queryWrapper.orderByDesc("ns.create_time");
        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo item : iPage.getRecords()) {
            SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
            BeanUtils.copyProperties(item, shop);
            item.setShop(shop);
        }
        PageResult<SiteShopListVo> pageRes = PageResult.build(iPage);
        SiteShopSelectPage vo = new SiteShopSelectPage();
        BeanUtils.copyProperties(pageRes, vo);
        vo.setVerifySiteIds(siteIds);
        return vo;
    }

    @Override
    public void editFollowNumber(Integer siteId, Integer isFollow) {
        SiteShop siteShop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        if (siteShop == null) {
            throw new CommonException("店铺扩展不存在");
        }
        if (isFollow.equals(1)) {
            siteShopMapper.update(null, new UpdateWrapper<SiteShop>().set("follow_number", siteShop.getFollowNumber() + 1).eq("site_id", siteId));
        } else {
            siteShopMapper.update(null, new UpdateWrapper<SiteShop>().set("follow_number", siteShop.getFollowNumber() - 1).eq("site_id", siteId));
        }
        //关注列表
        SiteShopMember siteShopMember = siteShopMemberMapper.selectOne(
                new LambdaQueryWrapper<SiteShopMember>()
                        .eq(SiteShopMember::getSiteId, siteId)
                        .eq(SiteShopMember::getMemberId, RequestUtils.memberId())
        );
        if (siteShopMember == null) {
            siteShopMember = new SiteShopMember();
            siteShopMember.setSiteId(siteId);
            siteShopMember.setMemberId(RequestUtils.memberId());
        }
        siteShopMember.setIsFollow(Objects.equals(isFollow, 1) ? 1 : 0);
        siteShopMemberMapper.insertOrUpdate(siteShopMember);
    }


    @Override
    public SiteShopInfoVo info(Object id,Map<String, String> allParams) {
        if (ObjectUtil.isNotEmpty( id)) {
            if ("undefined".equals( id.toString())){
                return  new SiteShopInfoVo();
            }
        }
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude," +
                        " ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours," +
                        " ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app," +
                        " ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend," +
                        " nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, " +
                        "nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, " +
                        "nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no," +
                        " nss.wechat_payment_code, nss.business_license,nss.quality_score,nss.service_score,nss.logistics_score,nss.contact_name, nss.background_h5")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("ns.site_id", id);
        //添加距离
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        if (ObjectUtil.isNotEmpty(allParams) && allParams.containsKey("position[latitude]") && allParams.containsKey("position[longitude]")){

            if(deliveryConfig.getIsShow() == 1){
                String latitude = allParams.get("position[latitude]");
                String longitude = allParams.get("position[longitude]");
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( "+
                        "cos(radians("+latitude+")) "+
                        "        * cos(radians(ns.latitude)) "+
                        "        * cos(radians(ns.longitude) - radians("+longitude+")) "+
                        "        + sin(radians("+latitude+")) "+
                        "        * sin(radians(ns.latitude)) "+
                        "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }


        SiteShopInfoVo model = siteMapper.selectJoinOne(SiteShopInfoVo.class, queryWrapper);
        Assert.notNull(model, "店铺扩展不存在");
        SiteShopInfoVo.SiteShop shop = new SiteShopInfoVo.SiteShop();
        BeanUtils.copyProperties(model, shop);
        model.setShop(shop);
        //会员基础信息
        SiteShopInfoVo.MemberInfo memberInfo = new SiteShopInfoVo.MemberInfo();
        memberInfo.setIsFollow(0);
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            SiteShopMember member = siteShopMemberMapper.selectOne(new QueryWrapper<SiteShopMember>().eq("member_id", RequestUtils.memberId()).eq("site_id", id).last("limit 1"));
            if (ObjectUtil.isNotEmpty(member)) {
                memberInfo.setIsFollow(member.getIsFollow());
            }
        }
        model.setMemberInfo(memberInfo);
        return model;
    }

    @Override
    public String shopQrcode(Integer id) {
        Map<String, Object> urlParam = new HashMap<>();
        urlParam.put("key", "site_id");
        urlParam.put("value", id);
        // 生成二维码
        String url = QrcodeUtils.qrcodeToFile(
                id, "weapp",
                coreSysConfigService.getSceneDomain().getWapUrl(),
                "addon/mall/pages/index",
                urlParam,
                "upload/qrcode/" + id + "/shop"
        );
        return url;
    }


    @Override
    public WebShopListVo selectShopList() {
        WebShopListVo vo = new WebShopListVo();

        WebConfigVo webConfigVo = webConfigService.getWebConfig();
        List<WebShopListVo.SiteWithShopInfo> list = new ArrayList<>();
        if(webConfigVo.getIsShowShop() != 0){
            List<Integer> siteIds = new ArrayList<>();
            try {
                if (ObjectUtil.isNotEmpty(webConfigVo.getSite_ids()) && !webConfigVo.getSite_ids().equals("[]")) {
                    JSONArray jsonArray = JSONUtil.parseArray(webConfigVo.getSite_ids());
                    siteIds = jsonArray.toList(Integer.class);
                }
            } catch (Exception e) {
            }
            if (!siteIds.isEmpty()) {
                MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
                queryWrapper.select("ns.site_id,ns.site_name,ns.front_end_name,ns.front_end_logo,ns.icon")
                    .setAlias("ns")
                    .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                    .ne("ns.app_type", "admin")
                    .eq("ns.status", SiteStatusEnum.ON.getCode())
                    .eq("nss.business_status", SiteStatusEnum.ON.getCode())
                    .gt("nss.mall_up_num", 0)
                    .in("ns.site_id", siteIds)
                    .orderByDesc("nss.mall_up_num", "nss.follow_number");
                List<Site> siteList = siteMapper.selectJoinList(Site.class, queryWrapper);

            for (Site site : siteList) {
                WebShopListVo.SiteWithShopInfo siteWithShopInfo = new WebShopListVo.SiteWithShopInfo();
                BeanUtils.copyProperties(site, siteWithShopInfo);

                WebShopListVo.SiteWithShopInfo.ShopInfo shopInfo = new WebShopListVo.SiteWithShopInfo.ShopInfo();
                shopInfo.setIsSelf(1);
                siteWithShopInfo.setShop(shopInfo);
                MallShopGoodsListEventDefiner.MallShopGoodsListEvent event = new MallShopGoodsListEventDefiner.MallShopGoodsListEvent();
                event.setSiteId(site.getSiteId());
                event.setNum(2);
                List<MallShopGoodsListEventDefiner.MallShopGoodsListEventResult> results = EventAndSubscribeOfPublisher.publishAndCallback(event);

                List<MallGoodsListVo> goodsList = new ArrayList<>();
                if (results != null && !results.isEmpty()) {
                    MallShopGoodsListEventDefiner.MallShopGoodsListEventResult result = results.get(0);
                    if (result != null && result.getGoodsList() != null) {
                        goodsList = result.getGoodsList();
                    }
                }
                siteWithShopInfo.setGoodsList(goodsList);
                list.add(siteWithShopInfo);
            }
        }
        }

        vo.setList(list);
        WebShopListVo.WebConfig webConfig = new WebShopListVo.WebConfig();
        BeanUtils.copyProperties(webConfigVo, webConfig);
        vo.setWebConfigVo(webConfig);
        return vo;
    }

}
