package com.niu.core.service.admin.install.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.account.SeparMainTypeEnum;
import com.niu.core.enums.sys.WebInitDataEnum;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.diy.IDiyAdvService;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.DiyAdvParam;
import com.niu.core.service.admin.goods.IGoodsServiceService;
import com.niu.core.service.admin.install.IInstallDataService;
import com.niu.core.service.admin.shop.IShopSiteGroupService;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.param.SiteShopCategoryParam;
import com.niu.core.service.admin.shop.param.SiteShopCategorySearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.web.IWebNavService;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InstallDataServiceImpl implements IInstallDataService {

    @Resource
    private IDiyService diyService;

    @Resource
    private IWebNavService webNavService;
    @Resource
    private IDiyAdvService diyAdvService;
    @Resource
    private ISiteService siteService;
    @Resource
    private IDeliveryCompanyService deliveryCompanyService;
    @Resource
    private DeliveryCompanyMapper deliveryCompanyMapper;
    @Resource
    private GoodsServiceMapper goodsServiceMapper;
    @Resource
    private ICoreDiyService coreDiyService;
    @Resource
    private ICorePosterService corePosterService;
    @Resource
    private SiteGroupMapper siteGroupMapper;
    @Resource
    private IShopSiteGroupService shopSiteGroupService;
    @Resource
    private ISiteShopCategoryService siteShopCategoryService;
    @Override
    public void install() {
        //初始化
        this.init();
        //初始化站点套餐
        initSiteGroup();
        //创建默认站点分类
        shopCategoryAdd();
        //初始化系统首页微页面
        diyService.initDefaultPage(0, "DIY_INDEX", null);
        //初始化系统个人中心微页面
        diyService.initDefaultPage(0, "DIY_MEMBER_INDEX", null);
        //创建默认找朋友帮忙付海报
        corePosterService.initDefaultPoster(0, "friendspay", null);
        //创建默认主题风格颜色
        coreDiyService.initDefaultDiyTheme();
    }

    private void init(){
        JSONObject initData = WebInitDataEnum.getType();
        JSONArray navs = initData.getJSONArray("nav");
        for (int i = 0; i < navs.size(); i++){
            JSONObject nav = navs.getJSONObject(i);
            //初始化首页导航
            WebNavParam webNavParam = new WebNavParam();
            webNavParam.setNavTitle(nav.getStr("nav_title"));
            webNavParam.setNavUrl(nav.getJSONObject("nav_url"));
            webNavParam.setSort(nav.getInt("sort"));
            webNavParam.setIsBlank(nav.getInt("is_blank"));
            webNavParam.setIsShow(nav.getInt("is_show"));
            webNavService.add(webNavParam);
        }
        try {
            //初始化首页轮播
            JSONArray adv  =  initData.getJSONArray("adv");
            DiyAdvParam diyAdvParam = new DiyAdvParam();
            diyAdvParam.setAdvTitle(adv.getJSONObject(0).getStr("adv_title"));
            diyAdvParam.setAdvImage(adv.getJSONObject(0).getStr("adv_image"));
            diyAdvParam.setAdvUrl(adv.getJSONObject(0).getJSONObject("adv_url"));
            diyAdvParam.setApKey(adv.getJSONObject(0).getStr("ap_key"));
            diyAdvParam.setSlideSort(adv.getJSONObject(0).getInt("slide_sort"));
            diyAdvParam.setBackground(adv.getJSONObject(0).getStr("background"));
            diyAdvService.add(diyAdvParam);
        }catch (Exception e){
            log.error("初始化数据失败", e);
        }

        //创建物流公司
        JSONArray companyList = initData.getJSONArray("company_list");
        List<DeliveryCompany> deliveryCompanies = new ArrayList<>();
        for (int i = 0; i < companyList.size(); i++){
            JSONObject company = companyList.getJSONObject(i);
            List<DeliveryCompany> list = deliveryCompanyMapper.selectList(new QueryWrapper<DeliveryCompany>().eq("company_name", company.getStr("company_name")));
            if(list.isEmpty()){
                DeliveryCompany model = new DeliveryCompany();
                BeanUtils.copyProperties(BeanUtil.toBean(company, DeliveryCompany.class), model);
                model.setCompanyId( null);
                deliveryCompanies.add( model);
            }
        }
        if(!deliveryCompanies.isEmpty()){
            deliveryCompanyService.insertAll(deliveryCompanies);
        }


        //添加商品服务
        JSONArray serviceList = initData.getJSONArray("service_list");
        List<GoodsService> goodsServiceList = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i++){
            JSONObject service = serviceList.getJSONObject(i);
            List<GoodsService> list = goodsServiceMapper.selectList(new QueryWrapper<GoodsService>().eq("service_name", service.getStr("service_name")));
            if (list.isEmpty()){
                GoodsService model = new GoodsService();
                BeanUtils.copyProperties(BeanUtil.toBean(service, GoodsService.class), model);
                model.setServiceId( null);
                goodsServiceList.add( model);
            }
        }
        if(!goodsServiceList.isEmpty()){
            goodsServiceMapper.insert(goodsServiceList);
        }

    }

    private void initSiteGroup(){
        SiteGroup siteGroup = siteGroupMapper.selectOne(new QueryWrapper<SiteGroup>().eq("create_by", "system").last("limit 1"));
        if(ObjectUtil.isEmpty(siteGroup)){
            SiteGroupAddParam siteGroupAddParam = new SiteGroupAddParam();
            siteGroupAddParam.setCreateBy("system");
            siteGroupAddParam.setGroupName("店铺套餐");
            siteGroupAddParam.setGroupDesc("");
            siteGroupAddParam.setAddon(new JSONArray());
            siteGroupAddParam.setApp(new JSONArray());
            shopSiteGroupService.add(siteGroupAddParam);
        }
    }

    private void shopCategoryAdd(){
        SiteShopCategorySearchParam siteShopCategorySearchParam = new SiteShopCategorySearchParam();
        siteShopCategorySearchParam.setCategoryName("店铺默认分类");
        List<SiteShopCategoryListVo> list = siteShopCategoryService.list(siteShopCategorySearchParam);
        if(list.isEmpty()){
            SiteShopCategoryParam siteShopCategoryParam = new SiteShopCategoryParam();
            siteShopCategoryParam.setCategoryName("店铺默认分类");
            siteShopCategoryService.add(siteShopCategoryParam);
        }
    }
}
