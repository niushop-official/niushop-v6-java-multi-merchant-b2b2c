package com.niu.core.service.admin.web.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 友情链接参数
*/
@Data
public class WebFriendlyLinkParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标题 */
    private String linkTitle;

    /** 链接 */
    private String linkUrl;

    /** 图片 */
    private String linkPic;

    /** 排序号 */
    private Integer linkSort;

    /** 是否显示 1.是 2.否 */
    private Integer isShow;


}
