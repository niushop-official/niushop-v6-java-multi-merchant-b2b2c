package com.niu.core.service.core.addon.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.addon.AddonStatusEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 插件实体
 */
@Data
public class AppSelectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 插件名称 */
    private String title;
    /** 插件标识 */
    @TableField(value = "`key`")
    private String key;


}