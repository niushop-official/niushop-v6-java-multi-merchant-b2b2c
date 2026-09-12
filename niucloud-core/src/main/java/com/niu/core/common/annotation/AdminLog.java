package com.niu.core.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员操作日志注解（对齐 PHP AdminLog + @description）
 * <p>
 * 可标注在类或方法上：
 * <ul>
 *   <li>类：模块描述，如「商品分类」</li>
 *   <li>方法：操作描述，如「新增分类」→ 最终 operation = 「商品分类-新增分类」</li>
 *   <li>skip=true：跳过记录（上传、轮询等）</li>
 * </ul>
 * 未标注时，切面仍会记录非 GET 的后台写请求（adminapi / siteapi），
 * 并尝试从 {@code @Operation(summary)} 取描述。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminLog {

    /**
     * 操作 / 模块描述
     */
    String value() default "";

    /**
     * 是否跳过记录
     */
    boolean skip() default false;
}
