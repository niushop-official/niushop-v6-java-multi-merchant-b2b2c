package com.niu.core.service.core.verify.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核销码创建结果（对应 PHP CoreVerifyService::create 返回的单项）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreVerifyCreateCodeVo {

    /**
     * 核销码
     */
    private String code;

    /**
     * 核销状态（O2O 自定义码时有值，如 0=待核销）
     */
    private Integer status;

    /**
     * 核销状态名称
     */
    private String statusName;

    public static CoreVerifyCreateCodeVo ofCode(String code) {
        return CoreVerifyCreateCodeVo.builder().code(code).build();
    }
}
