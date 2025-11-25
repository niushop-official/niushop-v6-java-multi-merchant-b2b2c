package com.niu.core.service.admin.site.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点参数
*/
@Data
public class SiteFinanceLogParam implements Serializable {

    private String tradeNo;
    private String[] createTime;

}
