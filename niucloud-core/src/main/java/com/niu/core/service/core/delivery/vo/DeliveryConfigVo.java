package com.niu.core.service.core.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 配送配置VO
 */
@Data
public class DeliveryConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

        /** 配送方式名称 */
        private String name;
        
        /** 状态 1-启用 0-禁用 */
        private Integer status;
        
        /** 配送方式key */
        private String key;
    }