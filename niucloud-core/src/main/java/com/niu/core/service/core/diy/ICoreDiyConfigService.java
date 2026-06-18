package com.niu.core.service.core.diy;

import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;

import java.util.List;

public interface ICoreDiyConfigService {

    /**
     * 获取底部导航配置
     * @return
     */
    List<BottomConfigVo> getBottomList();

    /**
     * 获取底部导航配置
     * @return
     */
    List<BottomConfigVo> getSiteBottomList();

    /**
     * 获取自定义导航配置
     * @param siteId
     * @return
     */
    BottomConfigVo getBottomConfig(Integer siteId, String key);

    /**
     * 批量获取自定义导航配置
     * @param siteId
     * @param keys
     * @return
     */
    List<BottomConfigVo> getBottomConfigList(Integer siteId, List<String> keys);

    /**
     * 自定义导航配置
     * @param siteId
     * @param bottomConfigValue
     */
    void setBottomConfig(Integer siteId, BottomConfigValue bottomConfigValue,String key);

    /**
     * 设置启动页配置
     * @param siteId
     * @param value
     * @param name
     */
    void setStartUpPageConfig(Integer siteId, StartUpPageConfigParam value, String name);

    /**
     * 获取启动页配置
     * @param siteId
     * @param name
     * @return
     */
    StartUpPageConfigVo getStartUpPageConfig(Integer siteId, String name);
}
