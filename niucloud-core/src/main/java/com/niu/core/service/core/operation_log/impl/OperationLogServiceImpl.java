package com.niu.core.service.core.operation_log.impl;

import com.niu.core.entity.sys.SysOperationLog;
import com.niu.core.mapper.sys.SysOperationLogMapper;
import com.niu.core.service.core.operation_log.IOperationLogService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements IOperationLogService {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

    @Override
    public void addOperationLog(String type, String mainType, Integer uid, String data) {
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setType(type);
        operationLog.setMainType(mainType);
        operationLog.setMainId(uid);
        operationLog.setContent(data);
        operationLog.setParamJson("");
        operationLog.setCreateTime(System.currentTimeMillis() / 1000);
        sysOperationLogMapper.insert(operationLog);
    }
}
