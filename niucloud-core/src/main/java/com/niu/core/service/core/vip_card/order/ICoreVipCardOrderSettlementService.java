package com.niu.core.service.core.vip_card.order;

import java.util.Map;

public interface ICoreVipCardOrderSettlementService {
    void settlement(Map<String, Object> data);

    void dealing(Map<String, Object> data);

    void success(Map<String, Object> data);
}