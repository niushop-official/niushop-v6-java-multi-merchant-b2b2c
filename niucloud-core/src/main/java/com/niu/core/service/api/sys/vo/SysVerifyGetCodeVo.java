package com.niu.core.service.api.sys.vo;

import com.niu.core.service.core.verify.vo.CoreVerifyCreateCodeVo;
import lombok.Data;

@Data
public class SysVerifyGetCodeVo {

    /**
     * 核销码
     */
    private String code;

    /**
     * 核销码详情（对应 PHP item，O2O 含 status/status_name）
     */
    private CoreVerifyCreateCodeVo item;

    /**
     * 二维码图片地址
     */
    private String qrcode;
}
