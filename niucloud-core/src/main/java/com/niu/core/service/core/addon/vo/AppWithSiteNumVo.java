package com.niu.core.service.core.addon.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用选择（含店铺数量）
 */
@Data
public class AppWithSiteNumVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private String key;

    private Integer siteNum;
}
