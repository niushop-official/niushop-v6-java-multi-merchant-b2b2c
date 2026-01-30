package com.niu.core.service.admin.goods.vo;

import cn.hutool.json.JSONObject;
import com.niu.core.enums.goods.BrandAuditEnum;
import com.niu.core.enums.goods.BrandPopularEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsBrand列表视图
 */
@Data
public class GoodsBrandInitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private JSONObject auditStatusList;
    private JSONObject statusList;
    private JSONObject isHotList;
}
