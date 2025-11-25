package com.niu.mall.service.api.cart.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopCart列表视图
 */
@Data
public class MallCartListApiVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String siteName;

    private String logo;

    private List<MallCartListVo> goodsList;
}
