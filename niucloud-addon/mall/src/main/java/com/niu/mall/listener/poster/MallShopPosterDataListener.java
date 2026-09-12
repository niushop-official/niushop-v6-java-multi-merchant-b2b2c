package com.niu.mall.listener.poster;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 商城商家海报数据
 * 对应 PHP: addon\mall\app\listener\poster\ShopPoster (mall_shop)
 */
@Component
@EventCallback("mall")
public class MallShopPosterDataListener extends GetPosterDataEventDefiner {

    @Resource
    private ICoreSysConfigService coreSysConfigService;

    @Resource
    private SiteMapper siteMapper;

    @Override
    public GetPosterDataResult handleCallback(GetPosterDataEvent event) {
        if (!"mall_shop".equals(event.getType())) {
            return null;
        }

        Map<String, Object> param = event.getParam() != null ? event.getParam() : Map.of();
        String mode = String.valueOf(param.getOrDefault("mode", ""));
        Integer siteId = parseInt(param.getOrDefault("site_id", event.getSiteId()));
        if (siteId == null || siteId <= 0) {
            siteId = event.getSiteId();
        }

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> urlData = new HashMap<>();
        urlData.put("key", "site_id");
        urlData.put("value", siteId);

        if ("preview".equals(mode) && (siteId == null || siteId <= 0)) {
            putShopInfo(data, "店铺名称", "static/resource/images/site_logo.png");
        } else {
            Site site = siteMapper.selectOne(new LambdaQueryWrapper<Site>().eq(Site::getSiteId, siteId));
            if (site != null) {
                String shopLogo = ObjectUtil.defaultIfBlank(site.getFrontEndLogo(),
                        ObjectUtil.defaultIfBlank(site.getLogo(), "static/resource/images/site_logo.png"));
                String shopName = ObjectUtil.defaultIfBlank(site.getSiteName(),
                        ObjectUtil.defaultIfBlank(site.getFrontEndName(), "店铺名称"));
                putShopInfo(data, shopName, shopLogo);
            } else {
                putShopInfo(data, "店铺名称", "static/resource/images/site_logo.png");
            }
        }

        String qrcode = QrcodeUtils.qrcodeToFile(
                siteId,
                event.getChannel(),
                coreSysConfigService.getSceneDomain().getWapUrl(),
                "addon/mall/pages/site/index",
                urlData,
                ""
        );
        data.put("url", qrcode);

        GetPosterDataResult result = new GetPosterDataResult();
        result.setData(data);
        return result;
    }

    private void putShopInfo(Map<String, Object> data, String shopName, String shopLogo) {
        data.put("site_name", shopName);
        data.put("site_logo", shopLogo);
        data.put("shop_name", shopName);
        data.put("shop_logo", shopLogo);
    }

    private Integer parseInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
