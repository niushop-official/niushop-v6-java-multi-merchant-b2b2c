package com.niu.core.service.admin.sys.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 相册设置视图对象
 */
@Data
public class SysAttachmentConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否启用裁剪 0-否 1-是 */
    private Integer isCropper = 0;

}
