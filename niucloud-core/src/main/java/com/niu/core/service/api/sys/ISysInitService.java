package com.niu.core.service.api.sys;

import com.niu.core.service.api.sys.vo.SysInitVo;

public interface ISysInitService {

    /**
     * 构建 App 初始化数据
     *
     * @param siteId 站点 ID
     * @return 初始化数据
     */
    SysInitVo buildInitData(Integer siteId);
}
