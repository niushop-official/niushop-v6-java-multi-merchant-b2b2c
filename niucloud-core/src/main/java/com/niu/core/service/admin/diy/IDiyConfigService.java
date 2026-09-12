package com.niu.core.service.admin.diy;


import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;

import java.util.List;

/**
 * 自定义配置接口
 */
public interface IDiyConfigService {

    List<BottomConfigVo> getBottomList();

    BottomConfigVo getBottomConfig(String key);

    void setBottomConfig(SetBottomConfigParam param);

    /**
     * 设置启动页配置
     *
     * @param param 启动页配置参数
     */
    void setStartUpPageConfig(StartUpPageConfigParam param);

    /**
     * 获取启动页配置
     *
     * @param name 启动页标识（如 'home', 'personal' 等）
     * @return 启动页配置 VO
     */
    StartUpPageConfigVo getStartUpPageConfig(String name);
}
