package com.niu.mall.listener.sys;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EventCallback("shop")
@Component
public class ShopPosterDataListener extends GetPosterDataEventDefiner {

    @Resource
    private ICoreSysConfigService coreSysConfigService;

    @Override
    public GetPosterDataResult handleCallback(GetPosterDataEvent event) {
        String type = event.getType();
        String mode = (String) event.getParam().getOrDefault("mode", "");

        if ("shop_goods".equals(type)) {
            Integer skuId = toInt(event.getParam().getOrDefault("sku_id", 0));

            Map<String, Object> data = new HashMap<>();
            if ("preview".equals(mode)) {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.jpg");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "￥369.00");
                data.put("goods_market_price", "￥3690.00");
                data.put("goods_img", "addon/mall/goods_template.png");
            } else {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.jpg");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "￥369.00");
                data.put("goods_market_price", "￥3690.00");
                data.put("goods_img", "addon/mall/goods_template.png");
            }

            Map<String, Object> urlData = new HashMap<>();
            urlData.put("sku_id", skuId);
            String qrcode = com.niu.core.common.utils.QrcodeUtils.qrcodeToFile(
                    event.getSiteId(),
                    event.getChannel(),
                    coreSysConfigService.getSceneDomain().getWapUrl(),
                    "addon/mall/pages/goods/detail",
                    urlData,
                    ""
            );
            data.put("url", qrcode);

            GetPosterDataResult result = new GetPosterDataResult();
            result.setData(data);
            return result;
        }

        if ("shop_point_goods".equals(type)) {
            Integer skuId = toInt(event.getParam().getOrDefault("sku_id", 0));

            Map<String, Object> data = new HashMap<>();
            if ("preview".equals(mode)) {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.jpg");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "积分1000+￥369.00");
                data.put("goods_img", "addon/mall/goods_template.png");
            } else {
                data.put("nickname", "会员昵称");
                data.put("headimg", "static/resource/images/default_headimg.jpg");
                data.put("goods_name", "商品名称");
                data.put("goods_price", "积分1000+￥369.00");
                data.put("goods_img", "addon/mall/goods_template.png");
            }

            Map<String, Object> urlData = new HashMap<>();
            urlData.put("sku_id", skuId);
            String qrcode = com.niu.core.common.utils.QrcodeUtils.qrcodeToFile(
                    event.getSiteId(),
                    event.getChannel(),
                    coreSysConfigService.getSceneDomain().getWapUrl(),
                    "addon/points_mall/pages/goods/detail",
                    urlData,
                    ""
            );
            data.put("url", qrcode);

            GetPosterDataResult result = new GetPosterDataResult();
            result.setData(data);
            return result;
        }

        return null;
    }

    private Integer toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer) return (Integer) obj;
        try { return Integer.valueOf(obj.toString()); } catch (Exception e) { return 0; }
    }
}


