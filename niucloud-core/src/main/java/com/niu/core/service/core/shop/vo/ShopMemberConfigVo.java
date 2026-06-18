package com.niu.core.service.core.shop.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopMemberConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 注册多少天后成为老用户 */
    private Integer oldMemberLength = 90;
}
