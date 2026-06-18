package com.niu.core.service.core.upload.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.config.UploadConfig;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreUploadConfigService;
import com.niu.core.service.core.upload.param.UploadConfigParam;
import com.niu.core.service.core.upload.vo.UploadConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoreUploadConfigService implements ICoreUploadConfigService {

    private static final String UPLOAD_CONFIG_KEY = "upload";
    @Resource
    private ICoreConfigService coreConfigService;
    @Resource
    private UploadConfig uploadConfig;

    @Override
    public UploadConfigVo getUploadConfig() {
        var config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), UPLOAD_CONFIG_KEY);
        if (config == null || config.isEmpty()) {
            UploadConfigVo vo = new UploadConfigVo();

            // 默认值
            List<String> defaultImageExt = Arrays.asList("jpg", "png", "gif", "jpeg");
            List<String> defaultVideoExt = Collections.singletonList("mp4");

            // 默认缩略图尺寸
            Map<String, Map<String, Integer>> defaultThumbType = new LinkedHashMap<>();
            defaultThumbType.put("big", Map.of("width", 1200, "height", 1200));
            defaultThumbType.put("mid", Map.of("width", 800, "height", 800));
            defaultThumbType.put("small", Map.of("width", 200, "height", 200));

            // 获取 rules
            Map<String, UploadConfig.Rule> rules = uploadConfig.getRules();
            if (rules == null) {
                rules = new HashMap<>();
            }

            // 图片配置
            UploadConfig.Rule imageRule = rules.getOrDefault("image", new UploadConfig.Rule());
            List<String> imageExtList = imageRule.getExt() != null ? imageRule.getExt() : defaultImageExt;
            vo.setImageExt(imageExtList.toArray(new String[0]));
            vo.setImageSize(imageRule.getSize() != null ? imageRule.getSize() : 0L);

            // 视频配置
            UploadConfig.Rule videoRule = rules.getOrDefault("video", new UploadConfig.Rule());
            List<String> videoExtList = videoRule.getExt() != null ? videoRule.getExt() : defaultVideoExt;
            vo.setVideoExt(videoExtList.toArray(new String[0]));
            vo.setVideoSize(videoRule.getSize() != null ? videoRule.getSize() : 0L);

            // 文档配置
            UploadConfig.Rule documentRule = rules.getOrDefault("document", new UploadConfig.Rule());
            vo.setDocumentExt(documentRule.getExt() != null ? documentRule.getExt().toArray(new String[0]) : new String[0]);
            vo.setDocumentSize(documentRule.getSize() != null ? documentRule.getSize() : 0L);

            // 缩略图配置（转为 JSON 字符串）
            UploadConfig.Rule thumbRule = rules.getOrDefault("thumb", new UploadConfig.Rule());
            Map<String, Map<String, Integer>> thumbType = thumbRule.getThumb_type() != null ? thumbRule.getThumb_type() : defaultThumbType;
            Map<String, Object> thumbMap = new HashMap<>();
            thumbMap.put("thumb_type", thumbType);
            String thumbJson = JSONUtil.toJsonStr(thumbMap);
            vo.setThumb(thumbJson);

            return vo;
        }
        return JSONUtil.toBean(config, UploadConfigVo.class);
    }

    @Override
    public void setUploadConfig(UploadConfigParam param) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), UPLOAD_CONFIG_KEY, JSONUtil.parse(param));
    }
}
