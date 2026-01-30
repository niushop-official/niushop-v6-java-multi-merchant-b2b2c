package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.fab.FabEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysFabService;
import com.niu.core.service.core.sys.vo.FabNavsVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class CoreSysFabServiceImpl implements ICoreSysFabService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public FabNavsVo getFabNavs() {
        return getFabConfig();
    }

    @Override
    public FabNavsVo getFabConfig() {
        FabNavsVo fabNavsVo = new FabNavsVo();

        JSONObject config = coreConfigService.getConfigValue(0, "FAB");
        if (ObjectUtil.isNotEmpty(config) && !config.isEmpty()){
            JSONObject styleJSON = config.getJSONObject("style");
            if (styleJSON != null && !styleJSON.isEmpty()) {
                FabNavsVo.StyleInfo styleInfo = new FabNavsVo.StyleInfo();
                styleInfo.setType(styleJSON.getInt("type", 1));
                styleInfo.setPosition(styleJSON.getStr("position", "right"));
                styleInfo.setBottom(styleJSON.getInt("bottom", 300));
                if (styleJSON.containsKey("icon")) {
                    styleInfo.setIcon(styleJSON.getStr("icon"));
                }
                fabNavsVo.setStyle(styleInfo);
            }
            fabNavsVo.setIsShow(config.getBool("is_show", false));
            fabNavsVo.setNavs(config.getJSONArray("navs"));
        }else{
            // 如果没有配置，使用默认值
            FabNavsVo.StyleInfo defaultStyle = new FabNavsVo.StyleInfo();
            defaultStyle.setType(1);
            defaultStyle.setPosition("right");
            defaultStyle.setBottom(300);
            fabNavsVo.setStyle(defaultStyle);
            fabNavsVo.setIsShow(false);
            fabNavsVo.setNavs(FabEnum.getFab());
        }
        return fabNavsVo;
    }
}
