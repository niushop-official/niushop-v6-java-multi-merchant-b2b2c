package com.niu.mall.service.admin.finance.param;

import lombok.Data;

@Data
public class SettlementSelectParam {
    private String keyword;
    private String[] createTime;
    private String[] payTime;
}
