package com.niu.core.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCategory;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.goods.param.MallGoodsSearchParam;
import com.niu.core.service.api.goods.vo.GoodsServiceListVo;
import com.niu.core.service.api.shop.param.ShopSearchParam;
import com.niu.core.service.api.shop.param.ShopSearchPostParam;
import com.niu.core.service.api.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.api.shop.vo.SiteShopListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


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
        Integer page  = searchParam.getPage();
        Integer limit =searchParam.getLimit();
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, " +
                        "ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, nsg.group_name, nss.id, nssc.category_name, nss.is_recommend, nss.is_self, nss.contact_mobile, nss.category_id, nss.commission_rate, nss.shop_deposit, nss.money, nss.money_get, nss.money_cash_outing, nss.bank_type, nss.bank_account_name, nss.bank_account_no, nss.bank_name, nss.bank_address, nss.follow_number, nss.alipay_name, nss.alipay_account_no, nss.alipay_payment_code, nss.wechat_name, nss.wechat_account_no, nss.wechat_payment_code, nss.business_license")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.ne("ns.app_type", "admin");
        AdminDeliveryConfigVo deliveryConfig = coreDeliveryConfigService.getAdminDeliveryConfig();
        if (ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null){

            if(deliveryConfig.getIsShow() == 1){
                String latitude = searchParam.getPosition().getLatitude();
                String longitude = searchParam.getPosition().getLongitude();
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
        if(ObjectUtil.isNotEmpty(searchParam.getRegion())){
            if(ObjectUtil.isNotEmpty(searchParam.getRegion().getProvinceId())  && searchParam.getRegion().getProvinceId() > 0){
                queryWrapper.eq("ns.province_id", searchParam.getRegion().getProvinceId());
            }
            if(ObjectUtil.isNotEmpty(searchParam.getRegion().getCityId())  && searchParam.getRegion().getCityId() > 0){
                queryWrapper.eq("ns.city_id", searchParam.getRegion().getCityId());
            }
            if(ObjectUtil.isNotEmpty(searchParam.getRegion().getDistrictId())  && searchParam.getRegion().getDistrictId() > 0){
                queryWrapper.eq("ns.district_id", searchParam.getRegion().getDistrictId());
            }
        }


        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j-> j.like("ns.keywords", searchParam.getKeyword()))
            );
        }
        if(ObjectUtil.isNotEmpty(searchParam.getCategoryId()))
        {
            queryWrapper.in("nss.category_id", searchParam.getCategoryId());
        }
        if(ObjectUtil.isNotEmpty(searchParam.getGroupId()))
        {
            queryWrapper.eq("nss.group_id", searchParam.getGroupId());
        }
        if(ObjectUtil.isNotEmpty(searchParam.getIsSelf()))
        {
            queryWrapper.eq("nss.is_self", searchParam.getIsSelf());
        }
        queryWrapper.eq("ns.status", 1);
        queryWrapper.eq("nss.business_status", 1);
        queryWrapper.like("ns.app", "mall");
        queryWrapper.ne("ns.app_type", "admin");

        if (ObjectUtil.isNotEmpty(searchParam.getDeliveryType())){
            if(searchParam.getDeliveryType().contains(DeliveryTypeEnum.EXPRESS.getType())){
                queryWrapper.eq("ns.is_express","1");
            }
            if(searchParam.getDeliveryType().contains(DeliveryTypeEnum.LOCAL_DELIVERY.getType())){
                queryWrapper.eq("ns.is_local_delivery", "1");
            }
            if(searchParam.getDeliveryType().contains(DeliveryTypeEnum.STORE.getType())){
                queryWrapper.eq("ns.is_store", "1");
            }
        }

        List<String> orders = Arrays.asList("mall_sale_num", "distance");
        if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && orders.contains(searchParam.getOrder())){
            switch (searchParam.getOrder()){
                case "mall_sale_num":
                    queryWrapper.orderByDesc(searchParam.getOrder());
                    break;
                case "distance":
                    if(ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null && deliveryConfig.getIsShow() == 1){
                        queryWrapper.orderByAsc("distance");
                    }else{
                        queryWrapper.orderByDesc("nss.mall_up_num");
                        queryWrapper.orderByDesc("nss.follow_number");
                    }
                    break;
            }
        }else{
            if(ObjectUtil.isNotEmpty(searchParam.getPosition()) && searchParam.getPosition().getLatitude() != null && searchParam.getPosition().getLongitude() != null && deliveryConfig.getIsShow() == 1){
                queryWrapper.orderByAsc("distance");
            }
            queryWrapper.orderByDesc("nss.mall_up_num");
            queryWrapper.orderByDesc("nss.follow_number");
        }

        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);

        //根据siteId调用监听事件
        if (ObjectUtil.isNotEmpty(iPage.getRecords())){
            List<SiteShopListVo> records = iPage.getRecords();
            records.forEach(k->{
                //根据站点名称查询商品
                Integer siteId = k.getSiteId();
                SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
                SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
                BeanUtils.copyProperties(model, shop);
                k.setShop(shop);
                MallShopGoodsListEventDefiner.MallShopGoodsListEvent event = new MallShopGoodsListEventDefiner.MallShopGoodsListEvent();
                event.setSiteId(siteId);
                event.setName("MallShopGoodsListEvent");
                List<MallShopGoodsListEventDefiner.MallShopGoodsListEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
                if (ObjectUtil.isNotEmpty(eventData)){
                    MallShopGoodsListEventDefiner.MallShopGoodsListEventResult mallShopGoodsListEventResult = eventData.get(0);
                    k.setGoods(mallShopGoodsListEventResult.getGoods());
                }
            });
        }
        return PageResult.build(iPage);
    }


    @Override
    public List<SiteShopListVo> component(ShopSearchParam param, Map<String, String> allParams) {
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
        //是否支持定位
        if(ObjectUtil.isNotEmpty(param.getIsLocation()) && param.getIsLocation() > 0 && allParams.containsKey("position[province_id]") &&
                allParams.containsKey("position[city_id]") && allParams.containsKey("position[district_id]")){
            String provinceId = allParams.get("position[province_id]");
            String cityId = allParams.get("position[city_id]");
            String districtId = allParams.get("position[district_id]");
            queryWrapper.eq("ns.province_id", provinceId);
            queryWrapper.eq("ns.city_id", cityId);
            if (ObjectUtil.isNotEmpty(param.getIsLocalDelivery()) && param.getIsLocalDelivery() > 0 ){
                queryWrapper.eq("ns.is_local_delivery", 1);
            }
        }

        List<String> orders = Arrays.asList("mall_sale_num", "create_time", "distance");
        if (ObjectUtil.isNotEmpty(param.getOrder()) && orders.contains(param.getOrder())){
            switch (param.getOrder()){
                case "create_time":
                case "mall_sale_num":
                    queryWrapper.orderByDesc(param.getOrder());
                    break;
                case "distance":
                    if(allParams.containsKey("position[latitude]") && allParams.containsKey("position[longitude]") && deliveryConfig.getIsShow() == 1){
                        queryWrapper.orderByAsc("distance");
                    }else{
                        queryWrapper.orderByDesc("nss.mall_up_num");
                        queryWrapper.orderByDesc("nss.follow_number");
                    }
                    break;
            }
        }else{
            if(allParams.containsKey("position[latitude]") && allParams.containsKey("position[longitude]") && deliveryConfig.getIsShow() == 1){
                queryWrapper.orderByAsc("distance");
            }
            queryWrapper.orderByDesc("nss.mall_up_num");
            queryWrapper.orderByDesc("nss.follow_number");
        }

        if (ObjectUtil.isNotEmpty(param.getNum()) && param.getNum() > 0) {
            queryWrapper.last("limit " + param.getNum());
        }
        List<SiteShopListVo> siteShopListVos = siteMapper.selectJoinList(SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo siteShopListVo : siteShopListVos) {
            Integer siteId = siteShopListVo.getSiteId();
            //根据站点名称查询商品
            SiteShopListVo.SiteShop shop = new SiteShopListVo.SiteShop();
            SiteShop model = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
            BeanUtils.copyProperties(model, shop);
            siteShopListVo.setShop(shop);
            MallShopGoodsListEventDefiner.MallShopGoodsListEvent event = new MallShopGoodsListEventDefiner.MallShopGoodsListEvent();
            event.setSiteId(siteId);
            event.setName("MallShopGoodsListEvent");
            List<MallShopGoodsListEventDefiner.MallShopGoodsListEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (ObjectUtil.isNotEmpty(eventData)){
                MallShopGoodsListEventDefiner.MallShopGoodsListEventResult mallShopGoodsListEventResult = eventData.get(0);
                siteShopListVo.setGoods(mallShopGoodsListEventResult.getGoods());
                siteShopListVo.setGoodsList(mallShopGoodsListEventResult.getGoodsList());
            }
        }
        return siteShopListVos;
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
