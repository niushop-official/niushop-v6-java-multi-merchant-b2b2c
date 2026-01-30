package com.niu.core.service.core.sys.vo;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.enums.fab.FabEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 悬浮按钮配置VO
*/
@Data
public class FabNavsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("is_show")
    private Boolean isShow = false;

    private StyleInfo style;

    private JSONArray navs;

    @Data
    public static class StyleInfo{
         private Integer type = 1;
         private String position = "right";
         private Integer bottom = 300;
         private String icon; // 悬浮球图标
    }
}
