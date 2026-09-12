package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图标分类
 */
@Data
public class IconCategoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    @JsonProperty("font_family")
    private String fontFamily;

    @JsonProperty("css_prefix_text")
    private String cssPrefixText;

    private String description;

    private List<IconGlyphVo> glyphs;
}
