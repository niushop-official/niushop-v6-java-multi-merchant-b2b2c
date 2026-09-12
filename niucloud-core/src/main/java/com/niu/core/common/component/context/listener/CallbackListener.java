package com.niu.core.common.component.context.listener;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class CallbackListener<T extends Event> {

    /**
     * @return
     */
    public String getModelName() {
        return getModelName(this.getClass());
    }

    /**
     * @return
     */
    public String getModelName(Class<?> clazz) {
        String name = "";
        try {
            EventCallback eventCallback = clazz.getAnnotation(EventCallback.class);
            name = eventCallback.value();
        } catch (Exception e) {
            // ignore
        }
        return name;
    }

    /**
     * 处理
     *
     * @param event
     */
    public Object handle(String name, Event event) {
        Object result = null;
        if (event.isAuthority()) {
            if (StrUtil.isNotEmpty(name) && event.contain(name)) {
                try {
                    Method method = this.getClass().getMethod("handleCallback", event.getClass());
                    result = method.invoke(this, (Object) event);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 运行时异常需要抛出去，中继业务
//                    if (e.getCause() != null) {
//                        throw new RuntimeException(e.getCause().getMessage());
//                    } else {
//                        throw new RuntimeException(e.getMessage());
//                    }
                    rethrowCallbackException(e);
                }
            }
        } else {
            if (StrUtil.isNotEmpty(name)) {
                try {
                    Method method = this.getClass().getMethod("handleCallback", event.getClass());
                    result = method.invoke(this, (Object) event);
                } catch (Exception e) {
                    e.printStackTrace();
//                    // 运行时异常需要抛出去，中继业务
//                    if (e.getCause() != null) {
//                        throw new RuntimeException(e.getCause().getMessage());
//                    } else {
//                        throw new RuntimeException(e.getMessage());
//                    }
                    rethrowCallbackException(e);
                }
            }
        }
        return result;
    }

    /**
     * Method.invoke 会将监听器异常包装为 InvocationTargetException，需解包后原样抛出业务异常。
     */
    private static void rethrowCallbackException(Exception e) {
        Throwable target = e;
        if (e instanceof InvocationTargetException) {
            Throwable targetException = ((InvocationTargetException) e).getTargetException();
            if (targetException != null) {
                target = targetException;
            } else if (e.getCause() != null) {
                target = e.getCause();
            }
        }
        if (target instanceof RuntimeException) {
            throw (RuntimeException) target;
        }
        if (target != null) {
            throw new RuntimeException(target);
        }
        throw new RuntimeException(e);
    }

    public abstract EventResult handleCallback(T event);

}
