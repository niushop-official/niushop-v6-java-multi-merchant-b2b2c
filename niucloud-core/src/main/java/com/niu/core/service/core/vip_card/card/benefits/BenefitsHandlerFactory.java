package com.niu.core.service.core.vip_card.card.benefits;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BenefitsHandlerFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Map<String, BenefitsContentHandler> handlerCache = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public BenefitsContentHandler getHandler(String beanNameOrClass) {
        return handlerCache.computeIfAbsent(beanNameOrClass, key -> {
            // 尝试作为 Spring Bean 名称获取
            if (applicationContext.containsBean(key)) {
                return applicationContext.getBean(key, BenefitsContentHandler.class);
            }
            // 否则尝试实例化类
            try {
                Class<?> clazz = Class.forName(key);
                return (BenefitsContentHandler) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
//                throw new IllegalArgumentException("无法找到处理器: " + key, e);
                log.info("vip权益 寻找处理器失败: " + key);
                log.info(e.toString());
                return null;
            }
        });
    }
}