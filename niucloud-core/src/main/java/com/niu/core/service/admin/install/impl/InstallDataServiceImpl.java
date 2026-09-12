package com.niu.core.service.admin.install.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.diy.DiyAdv;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.web.WebNav;
import com.niu.core.enums.sys.WebInitDataEnum;
import com.niu.core.mapper.diy.DiyAdvMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.mapper.web.WebNavMapper;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.diy.IDiyAdvService;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.DiyAdvParam;
import com.niu.core.service.admin.install.IInstallDataService;
import com.niu.core.service.admin.web.IWebNavService;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstallDataServiceImpl implements IInstallDataService {

    @Resource
    private IDiyService diyService;

    @Resource
    private IWebNavService webNavService;

    @Resource
    private WebNavMapper webNavMapper;

    @Resource
    private IDiyAdvService diyAdvService;

    @Resource
    private DiyAdvMapper diyAdvMapper;

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
    private ICoreSiteService coreSiteService;

    @Override
    public void install() {
        this.init();
        diyService.initDefaultPage(0, "DIY_INDEX", "", "");
        diyService.initDefaultPage(0, "DIY_MEMBER_INDEX", "", "");
        corePosterService.initDefaultPoster(0, "friendspay", "");
        corePosterService.initDefaultPoster(0, "cash", "");
        coreDiyService.initDefaultDiyTheme();
    }

    private void init() {
        JSONObject initData = WebInitDataEnum.getType();
        JSONArray navs = initData.getJSONArray("nav");
        for (int i = 0; i < navs.size(); i++) {
            JSONObject nav = navs.getJSONObject(i);
            Long navCount = webNavMapper.selectCount(new QueryWrapper<WebNav>()
                    .eq("nav_title", nav.getStr("nav_title")));
            if (navCount != null && navCount > 0) {
                continue;
            }
            WebNavParam webNavParam = new WebNavParam();
            webNavParam.setNavTitle(nav.getStr("nav_title"));
            webNavParam.setNavUrl(nav.getJSONObject("nav_url"));
            webNavParam.setSort(nav.getInt("sort"));
            webNavParam.setIsBlank(nav.getInt("is_blank"));
            webNavParam.setIsShow(nav.getInt("is_show"));
            webNavService.add(webNavParam);
        }

        JSONArray advJSON = initData.getJSONArray("adv");
        for (int i = 0; i < advJSON.size(); i++) {
            JSONObject adv = advJSON.getJSONObject(i);
            Long advCount = diyAdvMapper.selectCount(new QueryWrapper<DiyAdv>()
                    .eq("ap_key", adv.getStr("ap_key")));
            if (advCount != null && advCount > 0) {
                continue;
            }
            DiyAdvParam diyAdvParam = new DiyAdvParam();
            diyAdvParam.setAdvTitle(adv.getStr("adv_title"));
            diyAdvParam.setAdvImage(adv.getStr("adv_image"));
            diyAdvParam.setAdvUrl(adv.getJSONObject("adv_url"));
            diyAdvParam.setApKey(adv.getStr("ap_key"));
            diyAdvParam.setSlideSort(adv.getInt("slide_sort"));
            diyAdvParam.setBackground(adv.getStr("background"));
            diyAdvService.add(diyAdvParam);
        }

        JSONObject siteConfig = initData.getJSONObject("site");
        if (siteConfig != null && !siteConfig.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            if (siteConfig.containsKey("icon")) {
                data.put("icon", siteConfig.getStr("icon"));
            }
            if (siteConfig.containsKey("front_end_logo")) {
                data.put("frontEndLogo", siteConfig.getStr("front_end_logo"));
            }
            if (!data.isEmpty()) {
                coreSiteService.editBasicInfo(RequestUtils.defaultSiteId(), data);
            }
        }

        JSONArray companyList = initData.getJSONArray("company_list");
        List<DeliveryCompany> deliveryCompanies = new ArrayList<>();
        for (int i = 0; i < companyList.size(); i++) {
            JSONObject company = companyList.getJSONObject(i);
            List<DeliveryCompany> list = deliveryCompanyMapper.selectList(new QueryWrapper<DeliveryCompany>()
                    .eq("company_name", company.getStr("company_name")));
            if (list.isEmpty()) {
                DeliveryCompany model = new DeliveryCompany();
                BeanUtils.copyProperties(BeanUtil.toBean(company, DeliveryCompany.class), model);
                model.setCompanyId(null);
                deliveryCompanies.add(model);
            }
        }
        if (!deliveryCompanies.isEmpty()) {
            deliveryCompanyService.insertAll(deliveryCompanies);
        }

        JSONArray serviceList = initData.getJSONArray("service_list");
        List<GoodsService> goodsServiceList = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i++) {
            JSONObject service = serviceList.getJSONObject(i);
            List<GoodsService> list = goodsServiceMapper.selectList(new QueryWrapper<GoodsService>()
                    .eq("service_name", service.getStr("service_name")));
            if (list.isEmpty()) {
                GoodsService model = new GoodsService();
                BeanUtils.copyProperties(BeanUtil.toBean(service, GoodsService.class), model);
                model.setServiceId(null);
                goodsServiceList.add(model);
            }
        }
        if (!goodsServiceList.isEmpty()) {
            goodsServiceMapper.insert(goodsServiceList);
        }
    }
}
