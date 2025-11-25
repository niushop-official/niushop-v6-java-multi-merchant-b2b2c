package com.niu.mall.service.core.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * GoodsConfigVo视图
 */
@Data
public class GoodsConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isVerifyOpen = 0;
}
