package com.niu.core.service.core.shop;


import java.math.BigDecimal;

public interface ICoreShopAccountService {

    /**
     * 添加账单记录
     * @param siteId
     * @param money
     * @param fromType
     * @param memo
     * @param relatedId
     */
    void addLog(Integer siteId, BigDecimal money, String fromType, String memo, String relatedId);
}
