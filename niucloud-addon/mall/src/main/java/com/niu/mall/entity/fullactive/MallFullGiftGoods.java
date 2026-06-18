package com.niu.mall.entity.fullactive;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 满赠活动商品实体
 */
@Data
public class MallFullGiftGoods implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    /** 主键ID */
    private Integer id;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 满赠活动ID
     */
    private Integer fullGiftId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * SKU ID
     */
    private Integer skuId;

    /**
     * 适用范围类型
     */
    private String goodsType;

    /**
     * 活动状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Long createTime;

}
