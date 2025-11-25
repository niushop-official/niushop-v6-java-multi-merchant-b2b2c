package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺会员标签实体
 */
@Data
public class MemberLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标签id */
    @TableId(value="label_id", type= IdType.AUTO)
    private Integer labelId;

    /** 站点id */
    private Integer siteId;

    /** 标签名称 */
    private String labelName;

    /** 备注 */
    private String memo;

    /** 排序 */
    private Integer sort;

    /** 添加时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}