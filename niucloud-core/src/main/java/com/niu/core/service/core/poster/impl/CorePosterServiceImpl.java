package com.niu.core.service.core.poster.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        QueryWrapper query = new QueryWrapper<>();
        query.eq("site_id", param.getSiteId());
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
                if (template.size() > 0) {
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
                if (template.size() > 0) {
                    poster = template.getJSONObject(0).getJSONObject("data");
                    System.out.println("从模板文件重新加载海报模板成功");
                } else {
                    System.out.println("从模板文件加载失败，模板文件可能不存在");
                }
            }
            
            Map<String, Object> posterDataparam = BeanUtil.beanToMap(param);
            posterDataparam.putAll(param.getParam());
            System.out.println("海报参数: " + posterDataparam);
            System.out.println("海报类型: " + normalizedType);
            System.out.println("站点ID: " + param.getSiteId());
            System.out.println("渠道: " + param.getChannel());
            
            // 获取海报数据
            GetPosterDataEventDefiner.GetPosterDataEvent event = new GetPosterDataEventDefiner.GetPosterDataEvent();
            event.setSiteId(param.getSiteId());
            event.addAppSign("core");
            event.addAppSign("mall");
            event.addAppSign("mall_fenxiao");
            event.addAppSign("shop");
            event.addAppSign("points_mall");
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

            String dir = "upload/poster/" + param.getSiteId() + "/";
            String posterName = "poster_" + SecureUtil.md5(poster.toString()) + "_" + SecureUtil.md5(JSONUtil.parseObj(posterData).toString()) + "_" + param.getChannel() + ".png";

            if (new File(WebAppEnvs.get().webRootDownResource, dir + posterName).exists()) {
                return dir + posterName;
            }

            // 绘制海报
            create(param.getSiteId(), poster, posterData.getData(), dir, posterName, param.getChannel());
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
     * @param poster
     * @param posterData
     * @param channel 渠道
     */
    private void create(Integer siteId, JSONObject poster, Map<String, Object> posterData, String dir, String posterName, String channel) throws Exception {
        ImageCombiner combiner = null;
        String bgType = poster.getByPath("global.bgType", String.class);
        if ("color".equals(bgType)) {
            Integer widthObj = poster.getByPath("global.width", Integer.class);
            Integer heightObj = poster.getByPath("global.height", Integer.class);
            int width = widthObj != null ? widthObj : 750;
            int height = heightObj != null ? heightObj : 1334;
            String bgColor = poster.getByPath("global.bgColor", String.class);
            if (bgColor == null || bgColor.trim().isEmpty()) {
                bgColor = "#FFFFFF";
            }
            combiner = new ImageCombiner(width, height, Color.decode(bgColor), OutputFormat.PNG);
        } else {
            String bfgUrl = poster.getByPath("global.bgUrl", String.class);
            // 添加空值检查和异常处理
            if (bfgUrl == null || bfgUrl.trim().isEmpty()) {
                // 如果URL为空，使用默认颜色背景
                Integer widthObj = poster.getByPath("global.width", Integer.class);
                Integer heightObj = poster.getByPath("global.height", Integer.class);
                int width = widthObj != null ? widthObj : 750;
                int height = heightObj != null ? heightObj : 1334;
                String bgColor = poster.getByPath("global.bgColor", String.class);
                if (bgColor == null || bgColor.trim().isEmpty()) {
                    bgColor = "#FFFFFF";
                }
                combiner = new ImageCombiner(width, height, Color.decode(bgColor), OutputFormat.PNG);
            } else {
                BufferedImage backgroundImage = ImageUtils.readImage(bfgUrl);
                if (backgroundImage == null) {
                    throw new CommonException("无法读取背景图片: " + bfgUrl);
                }
                combiner = new ImageCombiner(backgroundImage, OutputFormat.PNG);
            }
        }

        JSONArray items = poster.getJSONArray("value");
        if (items == null) {
            items = new JSONArray();
        }
        
        // 对应PHP逻辑：在绘制前处理qrcode类型，将其转换为image类型并生成二维码图片路径
        if (channel == null || channel.isEmpty()) {
            channel = RequestUtils.channel();
        }
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            String itemType = item.getStr("type", "").toLowerCase();
            String relate = item.getStr("relate", "");
            
            // 如果类型是二维码，需要先生成二维码图片路径
            if ("qrcode".equals(itemType) && !relate.isEmpty()) {
                Object urlObj = posterData.get(relate);
                if (urlObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> urlMap = (Map<String, Object>) urlObj;
                    String url = (String) urlMap.getOrDefault("url", "");
                    String page = (String) urlMap.getOrDefault("page", "");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> urlData = (Map<String, Object>) urlMap.getOrDefault("data", new HashMap<>());
                    
                    // 生成二维码图片路径
                    String qrcodePath = QrcodeUtils.qrcodeToFile(siteId, channel, url, page, urlData, "");
                    
                    // 将类型改为image，value设置为二维码图片路径
                    item.set("type", "image");
                    item.set("value", qrcodePath);
                }
            }
        }
        
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            PosterDrawEvent event = new PosterDrawEvent();
            event.setSiteId(siteId);
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
     * 海报模板
     *
     * @param addon
     * @param type
     * @return
     */
    @Override
    public JSONArray template(String addon, String type) {
        final JSONArray template = new JSONArray();
        if (ObjectUtil.isNotEmpty(addon)) {
            JSONArray coreTemplate = JsonModuleLoader.build().findResultSet("code", "poster/template.json");
            coreTemplate.stream().forEach(e -> template.add(e));

            JSONArray addonTemplate = JsonModuleLoader.build().findResultSet(addon, "poster/template.json");
            addonTemplate.stream().forEach(e -> template.add(e));
        } else {
            JSONArray all = JsonModuleLoader.build().mergeResultSet("poster/template.json");
            all.stream().forEach(e -> template.add(e));
        }

        if (ObjectUtil.isNotEmpty(type) && template.size() > 0) {
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
                add.setAddon("mall");
                add(add);
            }
        }
    }
}
