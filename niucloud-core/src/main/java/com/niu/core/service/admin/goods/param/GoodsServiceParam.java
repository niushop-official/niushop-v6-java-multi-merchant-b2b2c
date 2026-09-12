package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品服务参数
 */
@Data
public class GoodsServiceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "serviceName参数缺失")
    @NotEmpty(message = "服务名称不能为空")
    private String serviceName;

    @NotNull(message = "image参数缺失")
    private String image;

    @NotNull(message = "desc参数缺失")
    private String desc;
    private List<String> addon; //插件
}
