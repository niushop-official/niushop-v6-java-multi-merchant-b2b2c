package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 站点分类实体
*/
@Data
public class SiteShopCategory implements Serializable {

private static final long serialVersionUID = 1L;

    /** 分类id */
    @TableId(value="category_id", type= IdType.AUTO)
    private Integer categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 手续费比率 */
    private BigDecimal commissionRate;

    /** 添加时间 */
    private Long createTime;

    /** 分类图 */
    private String image;

    /** 排序号 */
    private Integer sort;

    /** 更新时间 */
    private Long updateTime;

}