package com.niu.core.service.core.qrcode.impl;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.event.sys.GetQrcodeBySceneDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.qrcode.ICoreQrcodeService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 二维码核心实现（对齐 CoreQrcodeService.php）
 */
@Service
public class CoreQrcodeServiceImpl implements ICoreQrcodeService {

    public static final String TYPE_LINK = "link";
    public static final String TYPE_IMAGE = "image";

    @Resource
    private ICoreSysConfigService coreSysConfigService;

    @Override
    public String getQrcode(Map<String, Object> param, String type, String scene, String channel) {
        try {
            int pathSiteId = parseSiteId(param);
            GetQrcodeBySceneDefiner.GetQrcodeBySceneEvent event = new GetQrcodeBySceneDefiner.GetQrcodeBySceneEvent();
            event.setScene(scene);
            event.setParam(param != null ? param : Map.of());
            List<GetQrcodeBySceneDefiner.GetQrcodeBySceneResult> list =
                    EventAndSubscribeOfPublisher.publishAndCallback(event);
            GetQrcodeBySceneDefiner.GetQrcodeBySceneResult first = list.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> StrUtil.isNotEmpty(r.getPage()))
                    .findFirst()
                    .orElse(null);
            if (first == null) {
                return "";
            }
            SceneDomainVo domain = coreSysConfigService.getSceneDomain();
            String wapUrl = domain != null ? domain.getWapUrl() : "";
            if (StrUtil.isEmpty(wapUrl)) {
                return "";
            }
            String page = first.getPage();
            Map<String, Object> dataMap = sceneDataToDataMap(first.getSceneData());
            if (TYPE_LINK.equals(type)) {
                String query = buildQueryString(param);
                if (StrUtil.isEmpty(query)) {
                    return wapUrl + page;
                }
                return wapUrl + page + "?" + query;
            }
            if (TYPE_IMAGE.equals(type)) {
                String dir = "upload/qrcode/" + channel + "/" + scene + "/" + pathSiteId;
                // 与 PHP common.php qrcode() 一致：生成通道事件内使用 siteId=0
                return QrcodeUtils.qrcodeToFile(0, channel, wapUrl, page, dataMap, dir);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static int parseSiteId(Map<String, Object> param) {
        if (param == null) {
            return 0;
        }
        Object v = param.get("site_id");
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        try {
            return Integer.parseInt(v.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static Map<String, Object> sceneDataToDataMap(List<Map<String, Object>> sceneData) {
        if (sceneData == null || sceneData.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> m = new LinkedHashMap<>();
        for (Map<String, Object> row : sceneData) {
            if (row == null) {
                continue;
            }
            Object k = row.get("key");
            if (k != null && StrUtil.isNotEmpty(k.toString())) {
                m.put(k.toString(), row.get("value"));
            }
        }
        return m;
    }

    private static String buildQueryString(Map<String, Object> param) {
        if (param == null || param.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Object> e : param.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            String vs = e.getValue().toString();
            if (StrUtil.isEmpty(vs)) {
                continue;
            }
            parts.add(e.getKey() + "=" + URLEncoder.encode(vs, StandardCharsets.UTF_8));
        }
        return parts.stream().collect(Collectors.joining("&"));
    }
}
