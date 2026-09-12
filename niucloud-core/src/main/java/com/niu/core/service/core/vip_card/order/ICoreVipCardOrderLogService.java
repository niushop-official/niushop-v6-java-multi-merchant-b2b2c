package com.niu.core.service.core.vip_card.order;


import com.niu.core.entity.vip_card.MemberVipCardOrderLog;

import java.util.List;

public interface ICoreVipCardOrderLogService {

    /**
     * 添加单条订单日志
     *
     * @param log 日志实体
     */
    void add(MemberVipCardOrderLog log);

    /**
     * 批量添加订单日志
     *
     * @param logs 日志列表
     */
    void addAll(List<MemberVipCardOrderLog> logs);
}