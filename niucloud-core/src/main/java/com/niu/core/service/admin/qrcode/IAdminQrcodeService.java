package com.niu.core.service.admin.qrcode;

import com.niu.core.service.admin.qrcode.param.AdminQrcodeParam;

/**
 * 管理端二维码（对齐 app\service\admin\qrcode\QrcodeService）
 */
public interface IAdminQrcodeService {

    String getQrcode(AdminQrcodeParam param);
}
