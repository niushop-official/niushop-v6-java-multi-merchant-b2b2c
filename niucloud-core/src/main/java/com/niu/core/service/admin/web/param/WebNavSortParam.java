package com.niu.core.service.admin.web.param;

import lombok.Data;

import java.io.Serializable;

/**
 * PC导航管理参数
*/
@Data
public class WebNavSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 排序号 */
    private Integer sort;


}
