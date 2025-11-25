package com.niu.core.service.core.finance.param;
import lombok.Data;

import java.io.Serializable;

/**
 * GoodsConfigVo视图
 */
@Data
public class OrderSitePlatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isVerifyOpen = 0;
}
