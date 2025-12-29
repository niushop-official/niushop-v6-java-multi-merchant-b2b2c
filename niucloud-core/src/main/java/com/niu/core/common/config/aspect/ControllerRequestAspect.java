package com.niu.core.common.config.aspect;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.reflect.ReflectCallMethod;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@Slf4j
public class ControllerRequestAspect {

    // 定义切点，匹配所有Controller中的方法
    @Pointcut("execution(* com..*.controller..*.*(..))")
    public void controllerMethods() {
    }

    // 前置通知
    @Before("controllerMethods()")
    public void beforeControllerMethod(JoinPoint joinPoint) {
        Object currentObject = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        // 只对MemberAccountController的point方法添加详细日志
        if (currentObject.getClass().getSimpleName().contains("MemberAccountController") && "point".equals(methodName)) {
            log.info("MemberAccountController.point方法调用，开始处理参数");
            Map<String, String[]> parameterMap = RequestUtils.handler().getParameterMap();
            log.info("原始参数Map: {}", parameterMap);
        }
        final Map<String, String[]> preprocessingMap = this.receiveRequestArrayParameter();
        if (currentObject.getClass().getSimpleName().contains("MemberAccountController") && "point".equals(methodName)) {
            log.info("预处理参数Map: {}", preprocessingMap);
        }
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (currentObject.getClass().getSimpleName().contains("MemberAccountController") && "point".equals(methodName)) {
                log.info("处理参数对象: {}", arg.getClass().getSimpleName());
            }
            Field[] fields = arg.getClass().getDeclaredFields();
            if (fields != null) {
                Arrays.stream(fields).forEach(field -> {
                    this.doRequestParameter(arg, field, preprocessingMap);
                    if (currentObject.getClass().getSimpleName().contains("MemberAccountController") && "point".equals(methodName) && "createTime".equals(field.getName())) {
                        try {
                            field.setAccessible(true);
                            Object value = field.get(arg);
                            log.info("字段 {} 的值: {}", field.getName(), value != null ? Arrays.toString((String[])value) : "null");
                        } catch (Exception e) {
                            log.error("获取字段值失败", e);
                        }
                    }
                });
            }
        }
    }

    /**
     * @return
     */
    private Map<String, String[]> receiveRequestArrayParameter() {
        Map<String, String[]> preprocessingMap = new HashMap<>();
        Map<String, String[]> parameterMap = RequestUtils.handler().getParameterMap();
        Set<String> parameterSet = parameterMap.keySet();
        if (parameterSet != null && parameterSet.size() > 0) {
            // 处理以[]结尾的参数（如 create_time[]）
            parameterSet.stream().forEach(key -> {
                if (key.endsWith("[]")) {
                    String[] values = parameterMap.get(key);
                    if (values != null && values.length > 0) {
                        if (values.length == 1) {
                            String value = values[0];
                            String[] arrays = value.split(",");
                            String newKey = key.replace("[]", "");
                            newKey = StringUtils.toCamelCase(newKey);
                            preprocessingMap.put(newKey, arrays);
                        } else {
                            String[] arrays = values;
                            String newKey = key.replace("[]", "");
                            newKey = StringUtils.toCamelCase(newKey);
                            preprocessingMap.put(newKey, arrays);
                        }
                    }
                }
            });
            
            Map<String, Map<Integer, String>> indexedArrays = new HashMap<>();
            parameterSet.stream().forEach(key -> {
                if (key.matches(".*\\[\\d+\\]$")) {
                    String baseKey = key.replaceAll("\\[\\d+\\]$", "");
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[(\\d+)\\]$");
                    java.util.regex.Matcher matcher = pattern.matcher(key);
                    if (matcher.find()) {
                        int index = Integer.parseInt(matcher.group(1));
                        String[] values = parameterMap.get(key);
                        if (values != null && values.length > 0) {
                            indexedArrays.computeIfAbsent(baseKey, k -> new TreeMap<>()).put(index, values[0]);
                        }
                    }
                }
            });
            
            indexedArrays.forEach((baseKey, indexMap) -> {
                String camelKey = StringUtils.toCamelCase(baseKey);
                String[] array = new String[indexMap.size()];
                int i = 0;
                for (Integer index : indexMap.keySet()) {
                    array[i++] = indexMap.get(index);
                }
                preprocessingMap.put(camelKey, array);
            });
        }
        return preprocessingMap;
    }

    /**
     * @param target
     * @param field
     * @param preprocessingMap
     */
    private void doRequestParameter(Object target, Field field, Map<String, String[]> preprocessingMap) {
        String filedName = field.getName();
        // 优先处理preprocessingMap中的数组参数（如create_time[0], create_time[1]转换后的createTime）
        boolean isSetFromPreprocessing = false;
        for (Map.Entry<String, String[]> entry : preprocessingMap.entrySet()) {
            if (filedName.equals(entry.getKey())) {
                if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) entry.getValue());
                    isSetFromPreprocessing = true;
                    log.debug("设置数组参数: {} = {}", filedName, Arrays.toString(entry.getValue()));
                }
            }
        }
        
        // 如果已经从preprocessingMap设置，不再处理下划线命名的方式（避免覆盖）
        if (isSetFromPreprocessing && field.getType().equals(String[].class)) {
            return;
        }
        
        if (filedName.matches(".*[A-Z].*") && !"serialVersionUID".equals(filedName)) {
            String underFiledName = StringUtils.toUnderScoreCase(filedName);
            Map<String, String[]> parameterMap = RequestUtils.handler().getParameterMap();
            String[] valueObjects = parameterMap.get(underFiledName);
            if (valueObjects == null || valueObjects.length <= 0) {
                return;
            } else {
                if (field.getType().equals(String.class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects[0]);
                } else if (field.getType().equals(Integer.class)) {
                    try {
                        String valueStr = valueObjects[0];
                        if (valueStr != null && !valueStr.trim().isEmpty()) {
                            Integer value = Integer.parseInt(valueStr);
                            ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Long.class)) {
                    try {
                        String valueStr = valueObjects[0];
                        if (valueStr != null && !valueStr.trim().isEmpty()) {
                            Long value = Long.parseLong(valueStr);
                            ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Double.class)) {
                    try {
                        String valueStr = valueObjects[0];
                        if (valueStr != null && !valueStr.trim().isEmpty()) {
                            Double value = Double.parseDouble(valueStr);
                            ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Float.class)) {
                    try {
                        String valueStr = valueObjects[0];
                        if (valueStr != null && !valueStr.trim().isEmpty()) {
                            Float value = Float.parseFloat(valueStr);
                            ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects);
                } else if (field.getType().equals(Date.class)) {
                    // 时间暂时不处理
                } else if (field.getType().equals(LocalDateTime.class)) {
                    // 时间暂时不处理（JDK.18以上）
                }
            }
        }
    }

}

