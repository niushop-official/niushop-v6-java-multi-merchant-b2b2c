package com.niu.core.enums.o2o;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.utils.language.LanguageUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 支付方式枚举
 * nameKey 存 i18n key（如 dict_pay.type_balancepay），展示名通过 {@link #getLangKey()} 运行时解析。
 */
@Getter
public enum PayTypeEnum {
    /** 微信支付 */
    WECHATPAY("wechatpay",
            "dict_pay.type_wechatpay",
            "static/resource/icon/pay_icon/wechatpay.png",
            "/src/app/views/setting/components/pay-wechatpay.vue",
            new String[]{"mch_public_cert_path", "mch_secret_cert", "mch_secret_key", "wechat_public_cert_path"},
            new String[]{}),

    /** 支付宝支付 */
    ALIPAY("alipay",
            "dict_pay.type_alipay",
            "static/resource/icon/pay_icon/alipay.png",
            "/src/app/views/setting/components/pay-alipay.vue",
            new String[]{"app_secret_cert", "app_public_cert_path", "alipay_public_cert_path", "alipay_root_cert_path"},
            new String[]{}),

    /** 线下支付（PHP内未放入getPayType返回列表，仅常量定义） */
    OFFLINEPAY("offlinepay",
            "dict_pay.type_offlinepay",
            "static/resource/icon/pay_icon/offlinepay.png",
            "",
            new String[]{},
            new String[]{}),

    /** 余额支付 */
    BALANCEPAY("balancepay",
            "dict_pay.type_balancepay",
            "static/resource/icon/pay_icon/balancepay.png",
            "",
            new String[]{"secret_key"},
            new String[]{}),

    /** 找朋友帮忙付 */
    FRIENDSPAY("friendspay",
            "dict_pay.type_friendspay",
            "static/resource/icon/pay_icon/friendspay.png",
            "/src/app/views/setting/components/pay-friendspay.vue",
            new String[]{},
            new String[]{}),

    /** 微信支付(汇付) */
    HUIFU_WECHATPAY("huifu_wechatpay",
            "dict_pay.type_huifu_wechatpay",
            "static/resource/icon/pay_icon/wechatpay.png",
            "",
            new String[]{},
            new String[]{"h5"}),

    /** 支付宝支付(汇付) */
    HUIFU_ALIPAY("huifu_alipay",
            "dict_pay.type_huifu_alipay",
            "static/resource/icon/pay_icon/alipay.png",
            "",
            new String[]{},
            new String[]{});

    /** 支付唯一编码 key */
    private final String key;
    /** 语言包 key（对应 PHP get_lang） */
    private final String nameKey;
    /** 图标地址 */
    private final String icon;
    /** 前端配置组件地址 */
    private final String settingComponent;
    /** 需要加密的参数数组 */
    private final String[] encryptParams;
    /** 不支持的渠道 */
    private final String[] notSupportChannel;

    PayTypeEnum(String key, String nameKey, String icon, String settingComponent,
                String[] encryptParams, String[] notSupportChannel) {
        this.key = key;
        this.nameKey = nameKey;
        this.icon = icon;
        this.settingComponent = settingComponent;
        this.encryptParams = encryptParams;
        this.notSupportChannel = notSupportChannel;
    }

    /**
     * 运行时解析国际化名称（勿在枚举静态初始化时调用 LanguageUtils）
     */
    public String getLangKey() {
        String name = LanguageUtils.get(nameKey);
        if (StrUtil.isBlank(name) || name.equals(nameKey)) {
            return fallbackName();
        }
        return name;
    }

    private String fallbackName() {
        return switch (this) {
            case WECHATPAY -> "微信支付";
            case ALIPAY -> "支付宝支付";
            case OFFLINEPAY -> "线下支付";
            case BALANCEPAY -> "余额支付";
            case FRIENDSPAY -> "找朋友帮忙付";
            case HUIFU_WECHATPAY -> "微信支付(汇付)";
            case HUIFU_ALIPAY -> "支付宝支付(汇付)";
        };
    }

    /**
     * 获取所有启用支付类型集合（不含OFFLINEPAY，和PHP逻辑对齐）
     */
    public static List<Map<String, Object>> getPayType() {
        return getPayType(null);
    }

    /**
     * 过滤指定支付类型，返回支付配置列表（对应PHP getPayType(array $types = [])）
     */
    public static List<Map<String, Object>> getPayType(List<String> filterTypes) {
        List<Map<String, Object>> result = new LinkedList<>();
        PayTypeEnum[] allEnums = new PayTypeEnum[]{WECHATPAY, ALIPAY, BALANCEPAY, FRIENDSPAY, HUIFU_WECHATPAY, HUIFU_ALIPAY};

        for (PayTypeEnum pay : allEnums) {
            if (filterTypes != null && !filterTypes.isEmpty() && !filterTypes.contains(pay.getKey())) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("key", pay.getKey());
            item.put("name", pay.getLangKey());
            item.put("icon", pay.getIcon());
            item.put("setting_component", pay.getSettingComponent());
            item.put("encrypt_params", pay.getEncryptParams());
            if (pay.getNotSupportChannel() != null && pay.getNotSupportChannel().length > 0) {
                item.put("not_support_channel", pay.getNotSupportChannel());
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 根据支付类型编码获取展示名
     */
    public static String getNameByKey(String key) {
        PayTypeEnum payType = getByKey(key);
        return payType == null ? (key == null ? "" : key) : payType.getLangKey();
    }

    /**
     * 根据key匹配枚举
     */
    public static PayTypeEnum getByKey(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        for (PayTypeEnum value : values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }
}
