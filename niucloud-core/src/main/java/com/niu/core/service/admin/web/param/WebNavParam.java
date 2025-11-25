package com.niu.core.service.admin.web.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * PC导航管理参数
*/
@Data
public class WebNavParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 导航名称 */
    private String navTitle;

    /** 链接地址 */
    private JSONObject navUrl;

    /** 排序号 */
    private Integer sort;

    /** 是否新打开 */
    private Integer isBlank;

    /** 是否显示 1显示 0不显示 */
    private Integer isShow;


}
