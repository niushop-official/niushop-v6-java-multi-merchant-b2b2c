package com.niu.core.service.admin.goods.param;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标参数
 */
@Data
public class GoodsBadgeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "badge_name参数缺失")
    @NotEmpty(message = "角标名称不能为空")
    private String badgeName;

    private Integer groupId = 0;

    private String styleType = "";

    private JSONObject colorJson = new JSONObject();

    private String icon = "";

    private Integer status = 1;

    @NotNull(message = "memo参数缺失")
    private String memo;

    private Integer sort = 0;
}
