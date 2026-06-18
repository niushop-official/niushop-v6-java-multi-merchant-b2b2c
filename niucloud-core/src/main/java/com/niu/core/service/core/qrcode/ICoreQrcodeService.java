package com.niu.core.service.core.qrcode;

import java.util.Map;

/**
 * 二维码核心服务（对齐 app\service\core\qrcode\CoreQrcodeService）
 */
public interface ICoreQrcodeService {

    /**
     * @param param  业务参数（含 site_id、goods_id 等）
     * @param type   link / image
     * @param scene  场景标识
     * @param channel h5 / weapp / wechat 等
     * @return 链接或图片相对路径；失败返回空串
     */
    String getQrcode(Map<String, Object> param, String type, String scene, String channel);
}
