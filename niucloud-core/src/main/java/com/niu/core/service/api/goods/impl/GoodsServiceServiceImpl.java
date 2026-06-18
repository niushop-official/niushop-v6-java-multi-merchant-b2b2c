package com.niu.core.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCategory;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.goods.vo.GoodsServiceListVo;
import com.niu.core.service.api.shop.param.ShopSearchParam;
import com.niu.core.service.api.shop.param.ShopSearchPostParam;
import com.niu.core.service.api.shop.vo.MallGoodsListVo;
import com.niu.core.service.api.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.api.shop.vo.SiteShopListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreConfigService;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 商品服务实现
 */
@Service
public class GoodsServiceServiceImpl implements IGoodsServiceService {

    @Resource
    GoodsServiceMapper goodsServiceMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    SiteShopCategoryMapper siteShopCategoryMapper;
    @Resource
    ICoreConfigService coreConfigService;


    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    /**
     * 商品服务列表
     *
     * @return List<ShopGoodsServiceListVo>
     */
    @Override
    public List<GoodsServiceListVo> getAll() {
        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("create_time");
        List<GoodsService> record = goodsServiceMapper.selectList(queryWrapper);
        List<GoodsServiceListVo> list = new LinkedList<>();
        for (GoodsService item : record) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public PageResult<SiteShopListVo> shopList(ShopSearchPostParam searchParam) {
        Integer page = searchParam.getPage();
        Integer limit = searchParam.getLimit();
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, " +
                        "ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.ne("ns.app_type", "admin");
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        // 筛选有上架商品的店铺，与 PHP getShopPage / component() 口径一致
        queryWrapper.gt("nss.mall_up_num", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null) {

            if (deliveryConfig.getIsShow() == 1) {
                String latitude = searchParam.getPosition().getLatitude();
                String longitude = searchParam.getPosition().getLongitude();
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( " +
                        "cos(radians(" + latitude + ")) " +
                        "        * cos(radians(ns.latitude)) " +
                        "        * cos(radians(ns.longitude) - radians(" + longitude + ")) " +
                        "        + sin(radians(" + latitude + ")) " +
                        "        * sin(radians(ns.latitude)) " +
                        "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRegion())) {
            if (ObjectUtil.isNotEmpty(searchParam.getRegion().getProvinceId()) && searchParam.getRegion().getProvinceId() > 0) {
                queryWrapper.eq("ns.province_id", searchParam.getRegion().getProvinceId());
            }
            if (ObjectUtil.isNotEmpty(searchParam.getRegion().getCityId()) && searchParam.getRegion().getCityId() > 0) {
                queryWrapper.eq("ns.city_id", searchParam.getRegion().getCityId());
            }
            if (ObjectUtil.isNotEmpty(searchParam.getRegion().getDistrictId()) && searchParam.getRegion().getDistrictId() > 0) {
                queryWrapper.eq("ns.district_id", searchParam.getRegion().getDistrictId());
            }
        }


        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j -> j.like("ns.keywords", searchParam.getKeyword()))
            );
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            Integer[] categoryId = null;
            if (searchParam.getCategoryId() instanceof Integer) {
                categoryId = new Integer[]{(Integer) searchParam.getCategoryId()};
            } else if (searchParam.getCategoryId() instanceof ArrayList) {
                categoryId = ((ArrayList<?>) searchParam.getCategoryId()).toArray(new Integer[0]);
            }
            queryWrapper.in("nss.category_id", categoryId);
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("nss.group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsSelf())) {
            queryWrapper.eq("nss.is_self", searchParam.getIsSelf());
        }
        queryWrapper.eq("ns.status", 1);
        queryWrapper.eq("nss.business_status", 1);
        queryWrapper.like("ns.app", "mall");
        queryWrapper.ne("ns.app_type", "admin");

        if (ObjectUtil.isNotEmpty(searchParam.getDeliveryType())) {
            List<String> deliveryType = new ArrayList<>();
            if (searchParam.getDeliveryType() instanceof String) {
                deliveryType.add(searchParam.getDeliveryType().toString());
            } else if (searchParam.getDeliveryType() instanceof ArrayList) {
                deliveryType = (List<String>) searchParam.getDeliveryType();
            }
            if (deliveryType.contains(DeliveryTypeEnum.EXPRESS.getType())) {
                queryWrapper.eq("ns.is_express", "1");
            }
            if (deliveryType.contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                queryWrapper.eq("ns.is_local_delivery", "1");
            }
            if (deliveryType.contains(DeliveryTypeEnum.STORE.getType())) {
                queryWrapper.eq("ns.is_store", "1");
            }
        }

        //是否仅显示
        if (searchParam.getIsOnlyArea() > 0 && ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getProvinceId() != null && searchParam.getPosition().getCityId() != null) {
            queryWrapper.eq("ns.province_id", searchParam.getPosition().getProvinceId());
            queryWrapper.eq("ns.city_id", searchParam.getPosition().getCityId());
        }

        List<String> orders = Arrays.asList("mall_sale_num", "create_time", "distance");
        if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && orders.contains(searchParam.getOrder())) {
            switch (searchParam.getOrder()) {
                case "create_time":
                    queryWrapper.orderBy(true, "asc".equals(searchParam.getSort()), "ns.create_time");
                    break;
                case "mall_sale_num":
                    queryWrapper.orderBy(true, "asc".equals(searchParam.getSort()), "nss.mall_sale_num");
                    break;
                case "distance":
                    if (ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null && deliveryConfig.getIsShow() == 1) {
                        queryWrapper.orderByAsc("distance");
                        queryWrapper.orderByDesc("nss.mall_up_num");
                        queryWrapper.orderByDesc("nss.follow_number");
                    } else {
                        queryWrapper.orderByDesc("nss.mall_up_num");
                        queryWrapper.orderByDesc("nss.follow_number");
                    }
                    break;
            }
        } else {
            if (ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null && deliveryConfig.getIsShow() == 1) {
                queryWrapper.orderByAsc("distance");
            }
            queryWrapper.orderByDesc("nss.mall_up_num");
            queryWrapper.orderByDesc("nss.follow_number");
        }

        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);

        if (ObjectUtil.isNotEmpty(iPage.getRecords())) {
            List<SiteShopListVo> records = iPage.getRecords().stream()
                    .filter(Objects::nonNull)
                    .peek(this::enrichShopVo)
                    .filter(v -> v.getSiteId() != null)
                    .toList();
            iPage.setRecords(records);
        }
        return PageResult.build(iPage);
    }


    @Override
    public List<SiteShopListVo> component(ShopSearchParam param) {
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, " +
                        "ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.ne("ns.app_type", "admin");
        queryWrapper.eq("ns.status", SiteStatusEnum.ON.getCode());
        queryWrapper.eq("nss.business_status", 1);
        queryWrapper.gt("nss.mall_up_num", 0);
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        if (ObjectUtil.isNotEmpty(param.getPosition()) && ObjectUtil.isNotEmpty(param.getPosition().getLatitude()) && ObjectUtil.isNotEmpty(param.getPosition().getLongitude())) {

            if (deliveryConfig.getIsShow() == 1) {
                String latitude = param.getPosition().getLatitude();
                String longitude = param.getPosition().getLongitude();
                String siteField = "ns.latitude,ns.longitude";
                siteField += ", ROUND(6371 * acos( " +
                        "cos(radians(" + latitude + ")) " +
                        "        * cos(radians(ns.latitude)) " +
                        "        * cos(radians(ns.longitude) - radians(" + longitude + ")) " +
                        "        + sin(radians(" + latitude + ")) " +
                        "        * sin(radians(ns.latitude)) " +
                        "  ), 2) AS distance";
                queryWrapper.select(siteField);
            }
        }
        //是否支持定位
        if (ObjectUtil.isNotEmpty(param.getPosition()) && ObjectUtil.isNotEmpty(param.getIsLocation()) && param.getIsLocation() > 0 && ObjectUtil.isNotEmpty(param.getPosition().getProvinceId()) &&
                ObjectUtil.isNotEmpty(param.getPosition().getCityId())) {
            String provinceId = param.getPosition().getProvinceId();
            String cityId = param.getPosition().getCityId();
            queryWrapper.eq("ns.province_id", provinceId);
            queryWrapper.eq("ns.city_id", cityId);
            if (ObjectUtil.isNotEmpty(param.getIsLocalDelivery()) && param.getIsLocalDelivery() > 0) {
                queryWrapper.eq("ns.is_local_delivery", 1);
            }
        }

        List<String> orders = Arrays.asList("mall_sale_num", "create_time", "distance");
        if (ObjectUtil.isNotEmpty(param.getOrder()) && orders.contains(param.getOrder())) {
            switch (param.getOrder()) {
                case "create_time":
                    queryWrapper.orderByDesc("ns.create_time");
                    break;
                case "mall_sale_num":
                    queryWrapper.orderByDesc("nss.mall_sale_num");
                    break;
                case "distance":
                    if (ObjectUtil.isNotEmpty(param.getPosition()) && ObjectUtil.isNotEmpty(param.getPosition().getLatitude()) && ObjectUtil.isNotEmpty(param.getPosition().getLongitude()) && deliveryConfig.getIsShow() == 1) {
                        queryWrapper.orderByAsc("distance");
                    } else {
                        queryWrapper.orderByDesc("nss.mall_up_num");
                        queryWrapper.orderByDesc("nss.follow_number");
                    }
                    break;
            }
        } else {
            if (ObjectUtil.isNotEmpty(param.getPosition()) && ObjectUtil.isNotEmpty(param.getPosition().getLatitude()) && ObjectUtil.isNotEmpty(param.getPosition().getLongitude()) && deliveryConfig.getIsShow() == 1) {
                queryWrapper.orderByAsc("distance");
            }
            queryWrapper.orderByDesc("nss.mall_up_num");
            queryWrapper.orderByDesc("nss.follow_number");
        }

        List<Integer> siteIdList = normalizeSiteIdList(param.getSiteIds());
        if (ObjectUtil.isNotEmpty(siteIdList)) {
            queryWrapper.in("ns.site_id", siteIdList);
        }

        if (ObjectUtil.isNotEmpty(param.getNum()) && param.getNum() > 0) {
            queryWrapper.last("limit " + param.getNum());
        }
        List<SiteShopListVo> siteShopListVos = siteMapper.selectJoinList(SiteShopListVo.class, queryWrapper);
        return siteShopListVos.stream()
                .filter(Objects::nonNull)
                .peek(this::enrichShopVo)
                .filter(v -> v.getSiteId() != null)
                .toList();
    }

    /**
     * 补全店铺列表 VO：嵌套 shop、商品列表及字符串字段默认值。
     */
    private void enrichShopVo(SiteShopListVo vo) {
        if (vo == null) {
            return;
        }
        if (vo.getSiteName() == null) {
            vo.setSiteName("");
        }
        if (vo.getFrontEndLogo() == null) {
            vo.setFrontEndLogo("");
        }
        if (vo.getIcon() == null) {
            vo.setIcon("");
        }
        if (vo.getCategoryName() == null) {
            vo.setCategoryName("");
        }

        Integer siteId = vo.getSiteId();
        SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
        if (siteId != null) {
            SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
            if (model != null) {
                BeanUtils.copyProperties(model, shop);
            } else {
                shop.setSiteId(siteId);
            }
        }
        vo.setShop(shop);

        List<MallGoodsListVo> goodsList = Collections.emptyList();
        if (siteId != null) {
            MallShopGoodsListEventDefiner.MallShopGoodsListEvent event = new MallShopGoodsListEventDefiner.MallShopGoodsListEvent();
            event.setSiteId(siteId);
            event.setName("MallShopGoodsListEvent");
            List<MallShopGoodsListEventDefiner.MallShopGoodsListEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (ObjectUtil.isNotEmpty(eventData)) {
                MallShopGoodsListEventDefiner.MallShopGoodsListEventResult result = eventData.get(0);
                if (result.getGoodsList() != null) {
                    goodsList = result.getGoodsList();
                } else if (result.getGoods() != null) {
                    goodsList = result.getGoods();
                }
            }
        }
        vo.setGoods(goodsList);
        vo.setGoodsList(goodsList);
    }

    /**
     * 将 {@link ShopSearchParam#getSiteIds()}（JSON 可能为数组 / 混型）规范为整数列表，供 in 查询使用。
     */
    private List<Integer> normalizeSiteIdList(Object siteIds) {
        if (siteIds == null) {
            return Collections.emptyList();
        }
        List<Integer> out = new ArrayList<>();
        if (siteIds instanceof Collection<?> col) {
            for (Object o : col) {
                addSiteIdIfPresent(out, o);
            }
            return out;
        }
        if (siteIds instanceof int[] arr) {
            for (int v : arr) {
                out.add(v);
            }
            return out;
        }
        if (siteIds instanceof Integer[] arr) {
            for (Integer v : arr) {
                if (v != null) {
                    out.add(v);
                }
            }
            return out;
        }
        if (siteIds instanceof String[] arr) {
            for (String s : arr) {
                addSiteIdIfPresent(out, s);
            }
            return out;
        }
        if (siteIds instanceof JSONArray ja) {
            for (int i = 0; i < ja.size(); i++) {
                addSiteIdIfPresent(out, ja.get(i));
            }
            return out;
        }
        addSiteIdIfPresent(out, siteIds);
        return out;
    }

    private static void addSiteIdIfPresent(List<Integer> out, Object raw) {
        if (raw == null) {
            return;
        }
        if (raw instanceof Integer i) {
            out.add(i);
            return;
        }
        if (raw instanceof Number n) {
            out.add(n.intValue());
            return;
        }
        if (raw instanceof String s) {
            if (s.isBlank()) {
                return;
            }
            try {
                out.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException ignored) {
                // 忽略非法数字，避免整段请求失败
            }
        }
    }

    @Override
    public List<SiteShopCategoryListVo> shopCategory() {
        QueryWrapper<SiteShopCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<SiteShopCategory> list = siteShopCategoryMapper.selectList(queryWrapper);
        List<SiteShopCategoryListVo> listVo = new LinkedList<>();
        for (SiteShopCategory item : list) {
            SiteShopCategoryListVo vo = new SiteShopCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }
}
