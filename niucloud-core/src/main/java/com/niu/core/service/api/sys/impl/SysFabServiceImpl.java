package com.niu.core.service.api.sys.impl;

import com.niu.core.service.api.sys.ISysFabService;
import com.niu.core.service.core.sys.ICoreSysFabService;
import com.niu.core.service.core.sys.vo.FabNavsVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 悬浮按钮配置服务层
 */
@Service
@RequiredArgsConstructor
public class SysFabServiceImpl implements ISysFabService {

    @Resource
    private ICoreSysFabService coreSysFabService;
    @Override
    public FabNavsVo getFabNavs() {
        return coreSysFabService.getFabNavs();
    }

}
