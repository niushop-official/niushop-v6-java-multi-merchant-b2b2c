package com.niu.core.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品品牌实体
 */
@Data
public class GoodsBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="brand_id", type= IdType.AUTO)
    private Integer brandId; // 品牌ID
    private Integer siteId; // 站点id
    private String brandName; // 品牌名称
    private String brandType; // 平台或者站点
    private String logo; // 品牌logo
    @TableField(value = "`desc`")
    private String desc; // 品牌介绍
    private String colorJson; // 颜色配置
    private Integer sort; // 排序
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间
    private Long deleteTime; // 删除时间

    private Integer auditStatus; // 审核状态 1待审核 2已通过 -1已拒绝
    private String auditReason; // 审核拒绝原因
    private Long auditTime; // 审核时间
    private Integer status; // 显示状态 1显示 2隐藏
    private Integer isHot; // 是否热门 1是 2否

}