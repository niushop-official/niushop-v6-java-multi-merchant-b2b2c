package com.niu.core.service.admin.sys.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysMapParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "is_open参数缺失")
    private Integer isOpen = 0;

    @NotNull(message = "valid_time参数缺失")
    private Integer validTime = 0;

    private String mapType = "tianditu";              // 地图类型（如 amap, tianditu）
    private String tiandituMapKey;       // 天地图地图key
    private String tiandituMapWebKey;    // 天地图网页key
}
