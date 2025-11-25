package com.niu.core.service.admin.site.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加站点套餐
 */
@Data
public class SiteGroupAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "addon参数缺失")
    private JSONArray addon;

    @NotNull(message = "app参数缺失")
    private JSONArray app;

    @NotNull(message = "group_desc参数缺失")
    private String groupDesc;

    @NotNull(message = "group_name参数缺失")
    private String groupName;
    private String createBy;


}
