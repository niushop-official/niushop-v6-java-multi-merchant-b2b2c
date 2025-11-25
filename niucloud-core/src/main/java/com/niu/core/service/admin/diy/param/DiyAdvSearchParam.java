package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 广告表参数
 */
@Data
public class DiyAdvSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer advId;

    /** 广告位key */
    private String advKey;

    /** 广告位key */
    private String apKey;

    /** 广告内容描述 */
    private String advTitle;

    /** 广告链接 */
    private String advUrl;

    /** 广告内容图片 */
    private String advImage;

    /** 排序号 */
    private Integer sort;

    /** 背景色 */
    private String background;
    private  Integer slideSort;


}
