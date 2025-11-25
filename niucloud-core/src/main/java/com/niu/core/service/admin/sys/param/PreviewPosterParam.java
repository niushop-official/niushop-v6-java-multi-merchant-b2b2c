package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 预览海报参数
 * 对应PHP: Poster::preview()方法的参数
 */
@Data
public class PreviewPosterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 海报id，对应PHP中的id，默认值为0 */
    private Integer id = 0;

    /** 海报类型，对应PHP中的type */
    private String type;

    /** 数据参数，对应PHP中的param，默认包含mode=>preview */
    private Map<String, Object> param;

    /** 渠道，对应PHP中的channel，默认值为h5 */
    private String channel = "h5";
}
