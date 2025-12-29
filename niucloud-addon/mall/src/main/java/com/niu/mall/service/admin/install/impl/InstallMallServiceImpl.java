package com.niu.mall.service.admin.install.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.initialize.annotation.Initializer;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.mall.entity.goods.MallGoodsLabel;
import com.niu.mall.entity.web.MallWebFloor;
import com.niu.mall.mapper.goods.MallGoodsLabelMapper;
import com.niu.mall.mapper.web.MallWebFloorMapper;
import com.niu.mall.service.admin.install.IInstallMallService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class InstallMallServiceImpl implements IInstallMallService {

    @Resource
    ICorePosterService corePosterService;

    @Resource
    IDiyService diyService;

    @Resource
    ICoreDiyService coreDiyService;

    @Resource
    MallGoodsLabelMapper mallGoodsLabelMapper;

    @Resource
    MallWebFloorMapper mallWebFloorMapper;





    @Override
    public void install() {

        Integer siteId = RequestUtils.defaultSiteId();
        //初始化系统首页微页面
        diyService.initDefaultPage(0, "DIY_INDEX", null);
        //初始化系统个人中心微页面
        diyService.initDefaultPage(0, "DIY_MEMBER_INDEX", null);
        corePosterService.initDefaultPoster(siteId, "shop_goods", "mall");
        coreDiyService.initDefaultDiyTheme();
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject initData = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "init_mall_data.json");
        //初始化商品标签
        if(initData.containsKey("label_list"))
        {
            JSONArray labelList = initData.getJSONArray("label_list");
            for (int i = 0; i < labelList.size(); i++) {
                JSONObject label = labelList.getJSONObject(i);
                MallGoodsLabel goodsLabel = JSONUtil.toBean(label, MallGoodsLabel.class);
                goodsLabel.setSiteId(siteId);
                goodsLabel.setCreateTime(System.currentTimeMillis()/ 1000);
                mallGoodsLabelMapper.insert(goodsLabel);
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

    }
}
