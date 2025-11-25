package com.niu.core.service.admin.web.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 友情链接参数
*/
@Data
public class WebFriendlyLinkSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 索引id */
    private Integer id;

    /** 排序号 */
    private Integer linkSort;


}
