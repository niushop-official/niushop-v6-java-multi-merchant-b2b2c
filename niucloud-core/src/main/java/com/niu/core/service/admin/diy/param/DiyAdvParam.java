package com.niu.core.service.admin.diy.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 广告表参数
*/
@Data
public class DiyAdvParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer advId;

    /** 广告位key */
    private String advKey;

    /** 广告内容描述 */
    private String advTitle;

    /** 广告链接 */
    private JSONObject advUrl;

    /** 广告内容图片 */
    private String advImage;


    /** 背景色 */
    private String background = "#FFFFFF";

    private String apKey;

    private Integer slideSort =0;



}
