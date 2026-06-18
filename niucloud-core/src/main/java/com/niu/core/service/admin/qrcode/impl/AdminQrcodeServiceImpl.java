package com.niu.core.service.admin.qrcode.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.service.admin.qrcode.IAdminQrcodeService;
import com.niu.core.service.admin.qrcode.param.AdminQrcodeParam;
import com.niu.core.service.core.qrcode.ICoreQrcodeService;
import com.niu.core.service.core.qrcode.impl.CoreQrcodeServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端二维码（对齐 QrcodeService.php）
 */
@Service
public class AdminQrcodeServiceImpl implements IAdminQrcodeService {

    @Resource
    private ICoreQrcodeService coreQrcodeService;

    @Override
    public String getQrcode(AdminQrcodeParam in) {
        Map<String, Object> param = in.getParam() != null ? new HashMap<>(in.getParam()) : new HashMap<>();
        String channel = StrUtil.emptyToDefault(in.getChannel(), ChannelEnum.WEAPP.getCode());
        String type = StrUtil.emptyToDefault(in.getType(), CoreQrcodeServiceImpl.TYPE_IMAGE);
        String scene = ObjectUtil.defaultIfNull(in.getScene(), "");
        return coreQrcodeService.getQrcode(param, type, scene, channel);
    }
}
