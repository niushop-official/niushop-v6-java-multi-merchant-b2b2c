package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告排序修改
*/
@Data
public class DiyAdvSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;


    /** 排序号 */
    private Integer sort;



}
