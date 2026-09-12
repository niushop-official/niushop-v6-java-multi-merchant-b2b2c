package com.niu.core.service.admin.site;

import java.util.List;
import java.util.Map;

/**
 * 站点店铺二维码服务
 */
public interface ISiteQrcodeService {

    List<Map<String, String>> getList();

    Map<String, Object> getSiteQrcodeConfig(String type);

    void setSiteQrcodeConfig(Map<String, Object> value, String type);
}
