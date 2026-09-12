package com.niu.core.service.site.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 店铺会员批量修改参数
 */
@Data
public class SiteShopMemberBatchModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> memberIds;

    private String field;

    @NotNull(message = "value参数缺失")
    private Object value;

    /** 所属插件 */
    private String addon;

}
