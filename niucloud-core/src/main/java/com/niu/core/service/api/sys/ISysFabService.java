package com.niu.core.service.api.sys;

import com.niu.core.service.core.sys.vo.FabNavsVo;


/**
 * 悬浮按钮配置服务层
 */
public interface ISysFabService {

    /**
     * 获取悬浮按钮配置
     * @return
     */
    FabNavsVo getFabNavs();
}