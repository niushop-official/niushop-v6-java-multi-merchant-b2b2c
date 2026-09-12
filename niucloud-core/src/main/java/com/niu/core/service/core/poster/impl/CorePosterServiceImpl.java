package com.niu.core.service.core.poster.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestParamUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.event.sys.PosterDrawEvent;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.AddPosterParam;
import com.niu.core.service.core.poster.param.GetPosterParam;
import com.niu.core.service.core.upload.ICoreFetchService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CorePosterServiceImpl implements ICorePosterService {

    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 获取海报
     */
    @Override
    public String get(GetPosterParam param) {
        String reqType = param.getType();
        String normalizedType = reqType;
        // 店铺海报只能平台配置，模板统一从 site_id=0 读取（对齐 PHP）
        Map<String, Object> posterDataparam = new HashMap<>();
        if (param.getParam() != null) {
            posterDataparam.putAll(param.getParam());
        }
        int dataSiteId = RequestParamUtils.parseInt(posterDataparam.get("site_id"),
                param.getSiteId() != null ? param.getSiteId() : 0);
        posterDataparam.put("site_id", dataSiteId);
        int templateSiteId = 0;

        QueryWrapper query = new QueryWrapper<>();
        query.eq("site_id", templateSiteId);
        query.eq("type", normalizedType);
        query.eq("status", 1);
        query.last("limit 1");
        if (ObjectUtil.isNotEmpty(param.getId()) && param.getId() > 0) {
            query.eq("id", param.getId());
        } else {
            query.eq("is_default", 1);
        }
        SysPoster model = sysPosterMapper.selectOne(query);

        try {
            // 海报模板
            JSONObject poster = new JSONObject();
            if (model == null) {
                JSONArray template = template("", normalizedType);
                System.out.println("海报模板查询结果数量: " + template.size() + ", type: " + normalizedType);
                if (!template.isEmpty()) {
                    poster = template.getJSONObject(0).getJSONObject("data");
                    System.out.println("海报模板加载成功");
                } else {
                    System.out.println("海报模板为空，type: " + normalizedType);
                }
            } else {
                poster = JSONUtil.parseObj(model.getValue());
                System.out.println("从数据库加载海报模板");
            }

            if (poster.isEmpty()) {
                System.out.println("警告：海报模板为空，type: " + normalizedType);
                // 如果海报模板为空，尝试从模板文件加载
                JSONArray template = template("", normalizedType);
                if (!template.isEmpty()) {
                    poster = template.getJSONObject(0).getJSONObject("data");
                    System.out.println("从模板文件重新加载海报模板成功");
                } else {
                    System.out.println("从模板文件加载失败，模板文件可能不存在");
                }
            }

            System.out.println("海报参数: " + posterDataparam);
            System.out.println("海报类型: " + normalizedType);
            System.out.println("站点ID: " + dataSiteId);
            System.out.println("渠道: " + param.getChannel());

            // 获取海报数据
            GetPosterDataEventDefiner.GetPosterDataEvent event = new GetPosterDataEventDefiner.GetPosterDataEvent();
            event.setSiteId(dataSiteId);
            event.addAppSign("core");
            event.addAppSign("mall");
            event.addAppSign("mall_fenxiao");
            event.addAppSign("shop");
            event.addAppSign("points_mall");
            event.addAppSign("seckill");
            event.addAppSign("pintuan");
            event.addAppSign("o2o");
            event.addAppSign("friend_help");
            event.setName("GetPosterDataEvent");
            event.setType(normalizedType);
            event.setParam(posterDataparam);
            event.setChannel(param.getChannel());

            System.out.println("发布GetPosterDataEvent，type: " + normalizedType + ", appSigns: [core, mall, mall_fenxiao, shop, points_mall]");
            List<GetPosterDataEventDefiner.GetPosterDataResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            System.out.println("海报数据监听器返回结果数量: " + eventData.size());
            for (int i = 0; i < eventData.size(); i++) {
                GetPosterDataEventDefiner.GetPosterDataResult result = eventData.get(i);
                System.out.println("结果[" + i + "]: " + (result == null ? "null" : "not null"));
                if (result != null && result.getData() != null) {
                    System.out.println("结果[" + i + "] data: " + result.getData());
                    System.out.println("结果[" + i + "] data isEmpty: " + result.getData().isEmpty());
                }
            }
            GetPosterDataEventDefiner.GetPosterDataResult posterData = eventData.stream()
                    .filter(r -> r != null && r.getData() != null && !r.getData().isEmpty())
                    .findFirst()
                    .orElse(null);
            if (posterData == null) {
                System.out.println("未获取到海报数据，抛出异常");
                throw new CommonException("未获取到海报数据");
            }
            System.out.println("成功获取到海报数据");

            String dir = "upload/poster/" + templateSiteId + "/";
            // v2: 图片拉伸/圆形裁剪逻辑对齐 PHP，变更后需使用新缓存文件名
            String posterName = "poster_v2_" + SecureUtil.md5(poster.toString()) + "_"
                    + SecureUtil.md5(JSONUtil.toJsonStr(posterData.getData())) + "_" + param.getChannel() + ".png";

            if (new File(WebAppEnvs.get().webRootDownResource, dir + posterName).exists()) {
                return dir + posterName;
            }

            // 绘制海报
            create(templateSiteId, dataSiteId, poster, posterData.getData(), dir, posterName, param.getChannel());
            return dir + posterName;
        } catch (Exception e) {
            System.out.println("海报生成错误: " + e.getMessage());
            e.printStackTrace();
            if (param.getIsThrowException()) {
                System.out.println("抛出异常: " + e.getMessage());
                throw new CommonException(e.getMessage());
            }
            System.out.println("返回空字符串（isThrowException=false）");
            return "";
        }
    }

    /**
     * 绘制海报
     *
     * @param poster 海报
     * @param posterData 海报参数
     * @param channel 渠道
     */
    private void create(Integer templateSiteId, Integer dataSiteId, JSONObject poster, Map<String, Object> posterData, String dir, String posterName, String channel) throws Exception {
        BufferedImage background = buildPosterBackground(poster);
        ImageCombiner combiner = new ImageCombiner(background, OutputFormat.PNG);

        JSONArray items = poster.getJSONArray("value");
        if (items == null) {
            items = new JSONArray();
        }

        if (channel == null || channel.isEmpty()) {
            channel = RequestUtils.channel();
        }
        fillPosterTemplateValues(templateSiteId, dataSiteId, poster, items, posterData, channel);

        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            PosterDrawEvent event = new PosterDrawEvent();
            event.setSiteId(dataSiteId);
            event.setName("PosterDrawEvent");
            event.setCombiner(combiner);
            event.setPoster(item);
            event.setPosterData(posterData);
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        // 图片合成
        combiner.combine();

        FileTools.createDirs(new File(WebAppEnvs.get().webRootDownResource, dir).getPath());

        combiner.save(new File(WebAppEnvs.get().webRootDownResource, dir + posterName).getPath());
    }

    /**
     * 构建海报背景（对齐 PHP Poster::createPoster，按 global 宽高缩放背景图）
     */
    private BufferedImage buildPosterBackground(JSONObject poster) {
        Integer widthObj = poster.getByPath("global.width", Integer.class);
        Integer heightObj = poster.getByPath("global.height", Integer.class);
        int width = widthObj != null ? widthObj : 750;
        int height = heightObj != null ? heightObj : 1334;
        String bgColor = poster.getByPath("global.bgColor", String.class);
        if (bgColor == null || bgColor.trim().isEmpty()) {
            bgColor = "#FFFFFF";
        }

        String bgType = poster.getByPath("global.bgType", String.class);
        if ("color".equals(bgType)) {
            return createSolidBackground(width, height, bgColor);
        }

        String bgUrl = poster.getByPath("global.bgUrl", String.class);
        if (bgUrl == null || bgUrl.trim().isEmpty()) {
            return createSolidBackground(width, height, bgColor);
        }

        BufferedImage backgroundImage = null;
        try {
            backgroundImage = ImageUtils.readImage(bgUrl);
        } catch (Exception ignored) {
        }
        if (backgroundImage == null) {
            return createSolidBackground(width, height, bgColor);
        }
        if (backgroundImage.getWidth() == width && backgroundImage.getHeight() == height) {
            return backgroundImage;
        }

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(backgroundImage, 0, 0, width, height, null);
        graphics.dispose();
        return scaled;
    }

    private BufferedImage createSolidBackground(int width, int height, String bgColor) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        graphics.setColor(Color.decode(bgColor));
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        return canvas;
    }

    /**
     * 将海报数据填充到模板项（对齐 PHP CorePosterService::create）
     */
    private void fillPosterTemplateValues(Integer templateSiteId, Integer dataSiteId, JSONObject poster, JSONArray items,
                                          Map<String, Object> posterData, String channel) {
        String bgType = poster.getByPath("global.bgType", String.class);
        if ("url".equals(bgType)) {
            String bgUrl = poster.getByPath("global.bgUrl", String.class);
            if (bgUrl != null && (bgUrl.startsWith("http://") || bgUrl.startsWith("https://"))) {
                try {
                    ICoreFetchService fetchService = SpringContext.bean(ICoreFetchService.class);
                    poster.getJSONObject("global").set("bgUrl", fetchService.image(bgUrl, templateSiteId));
                } catch (Exception ignored) {
                }
            }
        }

        ICoreFetchService fetchService = null;
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            String relate = item.getStr("relate", "");
            if (relate.isEmpty()) {
                continue;
            }
            Object dataValue = posterData.get(relate);
            if (dataValue == null) {
                continue;
            }

            String itemType = item.getStr("type", "").toLowerCase();
            item.set("value", dataValue);

            if ("qrcode".equals(itemType)) {
                item.set("type", "image");
                if (dataValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> urlMap = (Map<String, Object>) dataValue;
                    String url = String.valueOf(urlMap.getOrDefault("url", ""));
                    String page = String.valueOf(urlMap.getOrDefault("page", ""));
                    Object rawData = urlMap.getOrDefault("data", new HashMap<>());
                    Map<String, Object> urlData = normalizeQrcodeData(rawData);
                    item.set("value", QrcodeUtils.qrcodeToFile(dataSiteId, channel, url, page, urlData, ""));
                } else {
                    item.set("value", String.valueOf(dataValue));
                }
            } else if ("image".equals(itemType) && dataValue instanceof String imgUrl) {
                if (imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
                    try {
                        if (fetchService == null) {
                            fetchService = SpringContext.bean(ICoreFetchService.class);
                        }
                        item.set("value", fetchService.image(imgUrl, templateSiteId));
                    } catch (Exception e) {
                        item.set("value", "");
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeQrcodeData(Object rawData) {
        if (rawData instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        if (rawData instanceof List<?> list) {
            Map<String, Object> urlData = new HashMap<>();
            for (Object item : list) {
                if (item instanceof Map<?, ?> entry) {
                    Object key = entry.get("key");
                    Object value = entry.get("value");
                    if (key != null) {
                        urlData.put(String.valueOf(key), value);
                    }
                }
            }
            return urlData;
        }
        return new HashMap<>();
    }


    /**
     * 海报模板
     *
     * @param addon 插件
     * @param type 海报类型
     * @return 末班
     */
    @Override
    public JSONArray template(String addon, String type) {
        final JSONArray template = new JSONArray();
        if (ObjectUtil.isNotEmpty(addon)) {
            JSONArray coreTemplate = JsonModuleLoader.build().findResultSet("code", "poster/template.json");
            template.addAll(coreTemplate);

            JSONArray addonTemplate = JsonModuleLoader.build().findResultSet(addon, "poster/template.json");
            template.addAll(addonTemplate);
        } else {
            JSONArray all = JsonModuleLoader.build().mergeResultSet("poster/template.json");
            template.addAll(all);
        }

        if (ObjectUtil.isNotEmpty(type) && !template.isEmpty()) {
            return CollUtil.filter(template, i -> {
                JSONObject item = (JSONObject) i;
                return item.getStr("type", "").equals(type);
            });
        }

        return template;
    }

    @Override
    public void add(AddPosterParam param) {
        SysPoster sysPoster = new SysPoster();
        BeanUtils.copyProperties(param, sysPoster);
        sysPoster.setCreateTime(System.currentTimeMillis()/1000);
        sysPosterMapper.insert(sysPoster);
    }

    @Override
    public void initDefaultPoster(Integer siteId, String type, String addon) {
        Long count = sysPosterMapper.selectCount(new QueryWrapper<SysPoster>()
                .eq("site_id", siteId).eq("type", type).eq("is_default", 1));
        if(count.intValue() == 0){
            JSONArray templateList = template(addon, type);
            Map<String, JSONObject> templateMap = new HashMap<>();
            for (int i = 0; i < templateList.size(); i++){
                templateMap.put(templateList.getJSONObject(i).getStr("type"), templateList.getJSONObject(i));
            }
            if (!templateMap.get(type).isEmpty()){
                JSONObject template = templateMap.get(type);
                AddPosterParam add = new AddPosterParam();
                add.setName(template.getStr("name"));
                add.setSiteId(siteId);
                add.setType(type);
                add.setValue(template.getJSONObject("data").toString());
                add.setIsDefault(1);
                add.setStatus(1);
                add.setAddon(addon);
                add(add);
            }
        }
    }

    @Override
    public Integer getDefaultPosterIdByType(Integer siteId, String type, String addon) {
        QueryWrapper<SysPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId)
                .eq("type", type)
                .eq("addon", addon)
                .eq("is_default", 1)
                .eq("status", 1);

        SysPoster poster = sysPosterMapper.selectOne(queryWrapper);
        return poster != null ? poster.getId() : 0;
    }
}
