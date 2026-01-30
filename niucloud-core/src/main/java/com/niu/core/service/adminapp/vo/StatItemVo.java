package com.niu.core.service.adminapp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计项视图对象
 */
@Data
public class StatItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 名称 */
    private String name;

    /** 标识 */
    private String key;

    /** URL */
    private String url;

    /** 排序 */
    private Integer sort;

    /** 页面路径 */
    private String page;
}
