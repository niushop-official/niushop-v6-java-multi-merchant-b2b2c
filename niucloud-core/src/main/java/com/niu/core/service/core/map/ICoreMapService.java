package com.niu.core.service.core.map;


import cn.hutool.json.JSONObject;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;

/**
 * 同城配送服务层
 */
public interface ICoreMapService {

    /**
     * 获取规划路线
     * @param from
     * @param to
     * @return
     */
    JSONObject getPolygon(String from,String to);

    /**
     * 获取地图配置
     * @return
     */
    JSONObject getMapConfig();
}
