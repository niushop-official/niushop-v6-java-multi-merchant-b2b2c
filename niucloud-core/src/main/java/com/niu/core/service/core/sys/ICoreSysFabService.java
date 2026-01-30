package com.niu.core.service.core.sys;

import com.niu.core.service.core.sys.vo.FabNavsVo;

public interface ICoreSysFabService {

    /**
     * 获取悬浮按钮配置
     * @return
     */
    FabNavsVo getFabNavs();

    /**
     * 获取底部导航
     * @return
     */
    FabNavsVo getFabConfig();

}
