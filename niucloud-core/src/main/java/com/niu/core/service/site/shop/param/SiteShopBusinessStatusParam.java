package com.niu.core.service.site.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺扩展表参数
 */
@Data
public class SiteShopBusinessStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer businessStatus;
}