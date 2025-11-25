package com.niu.core.service.admin.diy.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
* 广告表详情
*/
@Data
public class DiyAdvInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 主键 */
    private Integer advId;


    /** 广告位key */
    private String advKey;


    /** 广告内容描述 */
    private String advTitle;


    /** 广告链接 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String advUrl;


    /** 广告内容图片 */
    private String advImage;


    /** 排序号 */
    private Integer sort;


    /** 背景色 */
    private String background;

    private String apKey;

    private Integer slideSort;
    private String apName;
}