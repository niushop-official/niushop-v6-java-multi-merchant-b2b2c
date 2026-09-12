package com.niu.core.service.admin.site.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺分组（分组权限）参数
 */
@Data
public class SiteGroupParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "套餐ID不能为空")
    private Integer groupId;

    @NotBlank(message = "请输入套餐名称")
    @Size(max = 20, message = "套餐名称长度不能超过20个字符")
    private String groupName;

    @NotNull(message = "请输入套餐介绍")
    private String groupDesc;

    @NotNull(message = "请选择主应用")
    private JSONArray app;

    @NotNull(message = "请选择包含插件")
    private JSONArray addon;

    private Integer syncSite;
}
