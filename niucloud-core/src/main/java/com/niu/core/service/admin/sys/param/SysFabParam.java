package com.niu.core.service.admin.sys.param;

import cn.hutool.json.JSONArray;
import com.niu.core.service.core.sys.vo.FabNavsVo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 地址参数
*/
@Data
public class SysFabParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isShow = false;

    private FabNavsVo.StyleInfo style;

    private JSONArray navs;

    @Data
    public static class StyleInfo{
        private Integer type = 1;
        private String position = "right";
        private Integer bottom = 300;
    }
}
