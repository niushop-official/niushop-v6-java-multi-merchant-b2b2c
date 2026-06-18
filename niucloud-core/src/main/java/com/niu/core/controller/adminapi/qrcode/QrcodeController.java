package com.niu.core.controller.adminapi.qrcode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.qrcode.IAdminQrcodeService;
import com.niu.core.service.admin.qrcode.param.AdminQrcodeParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端二维码（对齐 app\adminapi\controller\qrcode\Qrcode.php）
 */
@RestController
@RequestMapping("adminapi/qrcode")
@SaCheckLogin
public class QrcodeController {

    @Resource
    private IAdminQrcodeService adminQrcodeService;

    /**
     * 获取二维码
     *
     * @param scene   场景 wap_index / shop_index / goods_detail
     * @param type    link / image
     * @param channel h5 / weapp / wechat
     * @param param   JSON 字符串，或与 PHP 一致拆成 site_id、goods_id 等平铺参数
     */
    @GetMapping
    public Result<String> qrcode(
            @RequestParam(name = "scene", required = false) String scene,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "channel", required = false) String channel,
            @RequestParam(name = "param", required = false) String paramJson,
            @RequestParam Map<String, String> allParams) {
        AdminQrcodeParam data = new AdminQrcodeParam();
        data.setScene(scene);
        data.setType(type);
        data.setChannel(channel);
        data.setParam(resolveParam(paramJson, allParams));
        return Result.success(adminQrcodeService.getQrcode(data));
    }

    private static Map<String, Object> resolveParam(String paramJson, Map<String, String> allParams) {
        Map<String, Object> param = new HashMap<>();
        if (StrUtil.isNotEmpty(paramJson)) {
            try {
                JSONObject jo = JSONUtil.parseObj(paramJson);
                for (String key : jo.keySet()) {
                    param.put(key, jo.get(key));
                }
            } catch (Exception ignored) {
                // 非 JSON 时走平铺参数
            }
        }
        if (param.isEmpty() && allParams != null) {
            for (Map.Entry<String, String> e : allParams.entrySet()) {
                String k = e.getKey();
                if ("scene".equals(k) || "type".equals(k) || "channel".equals(k) || "param".equals(k)) {
                    continue;
                }
                if (StrUtil.isNotEmpty(e.getValue())) {
                    param.put(k, e.getValue());
                }
            }
        }
        return param;
    }
}
