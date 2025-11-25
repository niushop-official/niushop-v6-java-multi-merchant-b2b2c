package com.niu.core.entity.web;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
* 友情链接实体
*/
@Data
public class WebFriendlyLink implements Serializable {

private static final long serialVersionUID = 1L;

    /** 索引id */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

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