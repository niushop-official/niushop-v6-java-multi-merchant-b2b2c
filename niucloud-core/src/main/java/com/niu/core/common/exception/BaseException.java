package com.niu.core.common.exception;

import cn.hutool.core.util.StrUtil;
// 原代码保留
// import lombok.Data;
// 新代码：添加国际化工具类
import lombok.Data;
import com.niu.core.common.utils.language.LanguageUtils;

/**
 * 基础异常类
 */
@Data
public class BaseException extends RuntimeException {

    private Integer code;

    private String msg;

    public BaseException() {
        super("服务器异常");
        this.code = 0;
        this.msg = "服务器异常";
    }

    // 添加静态方法处理国际化消息
    private static String getI18nMsg(String msg) {
        if (StrUtil.isBlank(msg)) {
            return "服务器异常";
        }
        String i18nMsg = LanguageUtils.get(msg);
        return StrUtil.isNotEmpty(i18nMsg) && !i18nMsg.equals(msg) ? i18nMsg : msg;
    }

    private static String formatMsg(String msg, Object... arguments) {
        String formatted = StrUtil.format(getI18nMsg(msg), arguments);
        if (StrUtil.isBlank(formatted)) {
            return StrUtil.isNotBlank(msg) ? msg : "服务器异常";
        }
        return formatted;
    }

    public BaseException(String msg, Object... arguments) {
        super(formatMsg(msg, arguments));
        this.code = 500;
        this.msg = getMessage();
    }

    public BaseException(Integer code, String msg, Object... arguments) {
        super(formatMsg(msg, arguments));
        this.code = code;
        this.msg = getMessage();
    }
}
