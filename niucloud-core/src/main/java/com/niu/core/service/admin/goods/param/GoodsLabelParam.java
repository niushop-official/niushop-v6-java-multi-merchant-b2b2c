package com.niu.core.service.admin.goods.param;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签参数
 */
@Data
public class GoodsLabelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_name参数缺失")
    @NotEmpty(message = "标签名称不能为空")
    private String labelName;

    private Integer groupId = 0;

    private String styleType = "";

    private JSONObject colorJson = new JSONObject();

    private String icon = "";

    private Integer status = 1;

    @NotNull(message = "memo参数缺失")
    private String memo;

    private Integer sort = 0;
}
