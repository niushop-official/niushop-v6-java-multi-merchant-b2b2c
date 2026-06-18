package com.niu.core.listener.sys;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.sys.GetQrcodeBySceneDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统场景二维码（对齐 app\listener\qrcode\SysQrcode）
 */
@EventCallback("core")
@Component
public class SysQrcodeBySceneListener extends GetQrcodeBySceneDefiner {

    @Override
    public GetQrcodeBySceneResult handleCallback(GetQrcodeBySceneEvent event) {
        if (!"wap_index".equals(event.getScene())) {
            return null;
        }
        GetQrcodeBySceneResult result = new GetQrcodeBySceneResult();
        result.setPage("app/pages/index/index");
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("key", "");
        row.put("value", "");
        data.add(row);
        result.setSceneData(data);
        return result;
    }
}
