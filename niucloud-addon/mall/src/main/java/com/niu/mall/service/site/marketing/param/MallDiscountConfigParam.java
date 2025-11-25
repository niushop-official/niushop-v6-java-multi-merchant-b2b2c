package com.niu.mall.service.site.marketing.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 限时折扣参数
*/
@Data
public class MallDiscountConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private JSONArray list;


}
