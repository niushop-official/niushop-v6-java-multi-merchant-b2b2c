package com.niu.core.service.adminapp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 待办项视图对象
 */
@Data
public class TodoItemVo implements Serializable {

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
