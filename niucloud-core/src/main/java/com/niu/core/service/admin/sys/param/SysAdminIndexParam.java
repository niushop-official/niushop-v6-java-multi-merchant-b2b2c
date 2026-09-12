package com.niu.core.service.admin.sys.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台首页配置参数
 */
@Data
public class SysAdminIndexParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "viewPath参数缺失")
    private String viewPath;
}
