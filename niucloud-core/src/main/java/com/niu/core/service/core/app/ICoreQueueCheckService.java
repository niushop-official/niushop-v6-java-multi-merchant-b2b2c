package com.niu.core.service.core.app;

import com.niu.core.common.component.context.queue.TaskArgument;

/**
 * 消息队列校验服务
 */
public interface ICoreQueueCheckService {

    /**
     * 写入队列校验标记文件
     */
    void markQueueCheckFile(TaskArgument taskArgument);
}
