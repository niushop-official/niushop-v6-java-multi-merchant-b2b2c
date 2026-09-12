package com.niu.core.service.core.site.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class SiteOptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;  // 主键
    private String siteName;  // 站点名称
}
