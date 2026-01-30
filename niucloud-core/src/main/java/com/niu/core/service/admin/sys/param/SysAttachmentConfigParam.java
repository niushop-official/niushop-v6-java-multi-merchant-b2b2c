package com.niu.core.service.admin.sys.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 相册设置参数
 */
@Data
public class SysAttachmentConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否启用裁剪 0-否 1-是 */
    private Integer isCropper;

}
