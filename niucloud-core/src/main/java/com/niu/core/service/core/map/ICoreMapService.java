package com.niu.core.service.core.map;


import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.vo.SysMapVo;

/**
 * 同城配送服务层
 */
public interface ICoreMapService {

    /**
     * 获取规划路线
     *
     * @param from
     * @param to
     * @return
     */
    JSONObject getPolygon(String from, String to);

    /**
     * 获取规划路线
     * 根据siteId  获取不同地图驱动(天地图/腾讯地图)
     *
     * @param siteId 站点ID
     * @param from
     * @param to
     * @return
     */
    JSONObject getPolyline(Integer siteId, String from, String to);

    /**
     * 获取地图配置
     *
     * @return
     */
    JSONObject getMapConfig();

    /**
     * 获取地图配置（含加密选项）
     *
     * @param siteId      站点ID
     * @param needEncrypt 是否对敏感字段进行加密替换
     * @return 配置Map
     */
    SysMapVo getMapConfig(Integer siteId, boolean needEncrypt);

    /**
     * 获取地图驱动
     *
     * @param siteId
     * @return
     */
    IMapService getDriver(Integer siteId);
}
