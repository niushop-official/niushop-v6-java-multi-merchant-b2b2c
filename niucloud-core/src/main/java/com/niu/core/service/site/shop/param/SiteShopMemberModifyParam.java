package com.niu.core.service.site.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 会员参数
 */
@Data
public class SiteShopMemberModifyParam<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private List<Integer> memberIds;

    private String field;

    private String addon;

    @NotNull(message = "value参数缺失")
    private Object value;

}
