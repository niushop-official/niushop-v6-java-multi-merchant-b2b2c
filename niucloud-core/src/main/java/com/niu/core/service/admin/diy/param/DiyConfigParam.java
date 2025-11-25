package com.niu.core.service.admin.diy.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义页面参数
*/
@Data
public class DiyConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key = "app";

}
