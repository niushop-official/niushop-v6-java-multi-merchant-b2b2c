package com.niu.core.event.fenxiao.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
import com.niu.core.event.fenxiao.vo.FenxiaoTradeTypeVo;

/**
 * 从插件 classpath JSON 加载分销业务类型配置
 */
public final class FenxiaoTradeTypeConfigLoader {

    private FenxiaoTradeTypeConfigLoader() {
    }

    /**
     * 加载业务类型配置并合并枚举显示名
     *
     * @param addonModule  插件标识，如 mall / o2o
     * @param relativePath 相对资源路径，如 fenxiao/trade_type.json
     * @param tradeType    业务类型枚举
     * @return FenxiaoTradeTypeVo
     */
    public static FenxiaoTradeTypeVo load(String addonModule, String relativePath, FenxiaoTradeTypeEnum tradeType) {
        // 先用枚举填充 type 和 name
        FenxiaoTradeTypeVo vo = new FenxiaoTradeTypeVo();
        vo.setType(tradeType.getType());
        vo.setName(tradeType.getName());

        // 读取插件 JSON 配置
        String json = AddonModuleContext.readResourceAsStreamToText(addonModule, relativePath);
        if (StrUtil.isBlank(json)) {
            return vo;
        }
        FenxiaoTradeTypeConfig config = JSONUtil.toBean(json, FenxiaoTradeTypeConfig.class);
        if (config == null) {
            return vo;
        }
        // 合并 JSON 中的页面路径
        if (StrUtil.isNotBlank(config.getGoodsDetailPath())) {
            vo.setGoodsDetailPath(config.getGoodsDetailPath());
        }
        if (StrUtil.isNotBlank(config.getOrderDetailPath())) {
            vo.setOrderDetailPath(config.getOrderDetailPath());
        }
        return vo;
    }
}
