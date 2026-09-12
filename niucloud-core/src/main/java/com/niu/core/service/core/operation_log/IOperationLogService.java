package com.niu.core.service.core.operation_log;

/**
 * 操作日志服务接口
 */
public interface IOperationLogService {

    /**
     * 添加操作日志
     *
     * @param type      操作类型
     * @param mainType  主类型
     * @param uid       操作用户ID
     * @param data      操作数据
     */
    void addOperationLog(String type, String mainType, Integer uid, String data);
}
