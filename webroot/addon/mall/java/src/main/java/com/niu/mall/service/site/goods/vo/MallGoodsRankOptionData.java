package com.niu.mall.service.site.goods.vo;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallGoodsRankOptionData {
    private JSONObject rankType;
    private JSONObject goodsSource;
    private JSONObject ruleType;
}
