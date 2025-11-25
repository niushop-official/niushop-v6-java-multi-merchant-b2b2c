package com.niu.core.service.api.sys.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.sys.IScanService;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.api.sys.param.SysCheckScanParam;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreScanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ScanServiceImpl implements IScanService {

    @Resource
    private ICoreScanService coreScanService;
    @Override
    public String checkScan(SysCheckScanParam param) {
       return coreScanService.checkScan(param.getKey());
    }
}
