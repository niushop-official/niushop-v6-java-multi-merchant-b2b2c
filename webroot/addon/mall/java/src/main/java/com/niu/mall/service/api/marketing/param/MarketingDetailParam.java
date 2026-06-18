package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class MarketingDetailParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** GET query：活动类型（如 full_reduction、full_gift、discount）→ 活动 id，与 uni-app qs.stringify 的 market_ids[xxx] 一致 */
    private Map<String, Integer> market_ids = new HashMap<>();

    private Integer num = 0;

    private BigDecimal price = BigDecimal.ZERO;
}
