package com.niu.mall.listener.qrcode;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.sys.GetQrcodeBySceneDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城场景二维码（对齐 addon\mall\app\listener\qrcode\MallQrcode）
 */
@EventCallback("mall")
@Component
public class MallQrcodeBySceneListener extends GetQrcodeBySceneDefiner {

    @Override
    public GetQrcodeBySceneResult handleCallback(GetQrcodeBySceneEvent event) {
        String scene = event.getScene();
        Map<String, Object> p = event.getParam() != null ? event.getParam() : Map.of();

        if ("goods_detail".equals(scene)) {
            Object goodsId = p.get("goods_id");
            if (ObjectUtil.isEmpty(goodsId)) {
                return null;
            }
            GetQrcodeBySceneResult result = new GetQrcodeBySceneResult();
            result.setPage("addon/mall/pages/goods/detail");
            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("key", "goods_id");
            row.put("value", goodsId);
            data.add(row);
            result.setSceneData(data);
            return result;
        }
        if ("shop_index".equals(scene)) {
            Object siteId = p.get("site_id");
            if (ObjectUtil.isEmpty(siteId)) {
                return null;
            }
            GetQrcodeBySceneResult result = new GetQrcodeBySceneResult();
            result.setPage("addon/mall/pages/index");
            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("key", "site_id");
            row.put("value", siteId);
            data.add(row);
            result.setSceneData(data);
            return result;
        }
        return null;
    }
}
