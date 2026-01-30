package com.niu.core.service.admin.sys;

import com.niu.core.service.admin.sys.param.SysFabParam;
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

    /**
     * 设置悬浮按钮配置
     * @return
     */
    void getFabConfig(SysFabParam param);
}