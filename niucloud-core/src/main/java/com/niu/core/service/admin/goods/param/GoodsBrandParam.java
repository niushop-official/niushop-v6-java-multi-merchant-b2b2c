package com.niu.core.service.admin.goods.param;
import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 商品品牌参数
*/
@Data
public class GoodsBrandParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "brand_name参数缺失")
    @NotEmpty(message = "品牌名称不能为空")
    private String brandName;

    @NotNull(message = "logo参数缺失")
    private String logo;

    @NotNull(message = "desc参数缺失")
    private String desc;

    @NotNull(message = "sort参数缺失")
    private String sort;

    private Integer status; // 显示状态 1显示 2隐藏
    private Integer isHot; // 是否热门 1是 2否
    private JSONObject colorJson; // 颜色配置
}
