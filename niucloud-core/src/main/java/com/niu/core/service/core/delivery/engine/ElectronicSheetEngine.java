package com.niu.core.service.core.delivery.engine;

public interface ElectronicSheetEngine {

    /**
     * 申请电子面单
     */
    ApplyResult apply(String cacheKey, ApplyParam param);
}
