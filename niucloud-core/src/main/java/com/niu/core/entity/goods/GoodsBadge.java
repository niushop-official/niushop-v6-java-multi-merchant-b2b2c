package com.niu.core.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标实体
 * 表 goods_badge / ns_goods_badge
 */
@Data
public class GoodsBadge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "badge_id", type = IdType.AUTO)
    private Integer badgeId;

    /** 站点id */
    private Integer siteId;

    /** 角标名称 */
    private String badgeName;

    /** 角标分组id */
    private Integer groupId;

    /** 效果设置，diy：自定义，icon：图片 */
    private String styleType;

    /** 自定义颜色（文字、背景、边框），json格式 */
    private String colorJson;

    /** 图标 */
    private String icon;

    /** 状态，1：启用，0：关闭 */
    private Integer status;

    /** 角标说明 */
    private String memo;

    /** 排序 */
    private Integer sort;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;
}
