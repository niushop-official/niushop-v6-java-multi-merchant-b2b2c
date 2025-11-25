package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 同城配送服务实体
 */
@Data
public class DeliveryLocalDeliveryService implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 三方配送key */
    @TableField("`key`")
    private String key;

    /** 三方配送名称 */
    private String name;

    /** 三方配送接入参数 */
    private String config;

    /** 是否商家自配送 */
    private Integer isMerchant;

    /** 是否启用（0否，1是） */
    private Integer isUse;

    private Integer isOpen;
}