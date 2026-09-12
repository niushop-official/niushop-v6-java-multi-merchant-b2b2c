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

    @TableId(value = "brand_id", type = IdType.AUTO)
    private Integer brandId;

    private Integer siteId;
    private String brandName;
    private String brandType;
    private String logo;

    @TableField(value = "`desc`")
    private String desc;

    private String colorJson;
    private Integer sort;
    private Long createTime;
    private Long updateTime;
    private Long deleteTime;
    private Integer auditStatus;
    private String auditReason;
    private Long auditTime;
    private Integer status;
    private Integer isHot;
    private String addon;
}
