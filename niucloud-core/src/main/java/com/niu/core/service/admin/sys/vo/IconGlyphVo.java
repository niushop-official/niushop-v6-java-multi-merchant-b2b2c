package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图标项
 */
@Data
public class IconGlyphVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("icon_id")
    private String iconId;

    private String name;

    @JsonProperty("font_class")
    private String fontClass;

    private String unicode;

    @JsonProperty("unicode_decimal")
    private Integer unicodeDecimal;

    // 以下字段由后端组装时填充
    @JsonProperty("att_id")
    private String attId;

    private String url;

    @JsonProperty("real_name")
    private String realName;
}
