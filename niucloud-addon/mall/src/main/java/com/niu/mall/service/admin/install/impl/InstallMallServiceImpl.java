package com.niu.mall.service.admin.install.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.mapper.goods.GoodsLabelMapper;
import com.niu.core.entity.goods.GoodsLabel;
import com.niu.mall.entity.web.MallWebFloor;
import com.niu.mall.mapper.web.MallWebFloorMapper;
import com.niu.mall.service.admin.install.IInstallMallService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class InstallMallServiceImpl implements IInstallMallService {

    @Resource
    ICorePosterService corePosterService;

    @Resource
    IDiyService diyService;

    @Resource
    ICoreDiyService coreDiyService;

    @Resource
    GoodsLabelMapper goodsLabelMapper;

    @Resource
    MallWebFloorMapper mallWebFloorMapper;


    private static final List<String> CATEGORY_NAMES = Arrays.asList(
            "数码家电", "家装建材", "日用百货", "生鲜水果", "母婴玩具", "食品酒水"
    );


    @Override
    public void install() {

        Integer siteId = RequestUtils.defaultSiteId();
        //初始化商城首页微页面
        diyService.initDefaultPage(0, "DIY_MALL_INDEX", "mall", "");
        //初始化商城个人中心微页面
        diyService.initDefaultPage(0, "DIY_MALL_MEMBER_INDEX", "mall", "");
        //初始化平台首页微页面（映射商城模板）
        diyService.initDefaultPage(0, "DIY_MALL_INDEX", "mall", "DIY_INDEX");
        //初始化平台个人中心微页面（映射商城模板）
        diyService.initDefaultPage(0, "DIY_MALL_MEMBER_INDEX", "mall", "DIY_MEMBER_INDEX");
        //初始化商品详情微页面
        diyService.initDefaultPage(0, "DIY_MALL_GOODS_DETAIL", "mall", "");
        corePosterService.initDefaultPoster(siteId, "shop_goods", "mall");
        corePosterService.initDefaultPoster(0, "mall_shop", "mall");
        coreDiyService.initDefaultDiyTheme();
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject initData = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "init_mall_data.json");
        //初始化商品标签
        if(initData.containsKey("label_list"))
        {
            JSONArray labelList = initData.getJSONArray("label_list");
            for (int i = 0; i < labelList.size(); i++) {
                JSONObject label = labelList.getJSONObject(i);
                GoodsLabel goodsLabel = JSONUtil.toBean(label, GoodsLabel.class);
                Long existCount = goodsLabelMapper.selectCount(new QueryWrapper<GoodsLabel>()
                        .eq("label_name", goodsLabel.getLabelName())
                        .eq("site_id", siteId));
                if (existCount != null && existCount > 0) {
                    continue;
                }
                goodsLabel.setSiteId(siteId);
                goodsLabel.setCreateTime(System.currentTimeMillis()/ 1000);
                goodsLabelMapper.insert(goodsLabel);
            }
        }
        //初始化首页楼层
        if(initData.containsKey("floor_list"))
        {
            String str = initData.toString();
            //替换时间
            str = str.replace("$time$", (System.currentTimeMillis() / 1000) + "");
            JSONArray floorList = JSONUtil.parseObj(str).getJSONArray("floor_list");
            for (int i = 0; i < floorList.size(); i++) {
                JSONObject floor = floorList.getJSONObject(i);
                MallWebFloor mallFloor = JSONUtil.toBean(floor, MallWebFloor.class);
                mallFloor.setCreateTime(System.currentTimeMillis()/ 1000);
                mallWebFloorMapper.insert(mallFloor);
            }

        }
        //初始化 行业类目
        handleSiteCategoryData();
    }

    /**
     * M-07: mall 行业类目种子
     */
    public void handleSiteCategoryData() {
        SiteCategoryMapper siteCategoryMapper = SpringContext.bean(SiteCategoryMapper.class);
        long now = System.currentTimeMillis() / 1000;
        for (String name : CATEGORY_NAMES) {
            Long count = siteCategoryMapper.selectCount(new LambdaQueryWrapper<SiteCategory>()
                    .eq(SiteCategory::getName, name)
                    .eq(SiteCategory::getAddon, "mall")
            );
            if (count != null && count > 0) {
                continue;
            }
            SiteCategory category = new SiteCategory();
            category.setName(name);
            category.setLevel(1);
            category.setPid(0);
            category.setStatus(1);
            category.setAddon("mall");
            category.setServiceRate(BigDecimal.ZERO);
            category.setCreateTime(now);
            category.setUpdateTime(now);
            siteCategoryMapper.insert(category);
        }
    }
}
