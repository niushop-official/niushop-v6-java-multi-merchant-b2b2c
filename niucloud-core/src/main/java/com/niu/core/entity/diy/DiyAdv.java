package com.niu.core.entity.diy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
* 广告表实体
*/
@Data
public class DiyAdv implements Serializable {

private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="adv_id", type= IdType.AUTO)
    private Integer advId;

    /** 广告位key */
//    private String advKey;

    /** 广告位key */
    private String apKey;

    /** 广告内容描述 */
    private String advTitle;

    /** 广告链接 */
    private String advUrl;

    /** 广告内容图片 */
    private String advImage;

    /** 排序号 */
//    private Integer sort;
    private Integer slideSort;

    /** 背景色 */
    private String background;

}