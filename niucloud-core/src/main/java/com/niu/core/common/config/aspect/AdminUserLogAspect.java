package com.niu.core.common.config.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.AdminLog;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.param.SysUserLogParam;
import com.niu.core.service.core.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员操作日志切面
 * 对齐 PHP AdminLog：非 GET 的后台写操作写入 sys_user_log
 * <p>
 * Java 包路径：平台端 controller.adminapi，商户端 controller.siteapi（URL 仍为 /adminapi/...）
 */
@Slf4j
@Aspect
@Component
public class AdminUserLogAspect {

    private static final JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);

    @Resource
    private ISysUserLogService sysUserLogService;

    /**
     * 平台端控制器
     */
    @Pointcut("execution(* com.niu..controller.adminapi..*(..))")
    public void adminApiPointcut() {
    }

    /**
     * 商户端控制器（siteapi）
     */
    @Pointcut("execution(* com.niu..controller.siteapi..*(..))")
    public void siteApiPointcut() {
    }

    @Before("adminApiPointcut() || siteApiPointcut()")
    public void before(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = RequestUtils.handler();
            if (request == null) {
                return;
            }
            String method = request.getMethod();
            if (method == null || "GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
                return;
            }

            Integer uid = RequestUtils.uid();
            if (uid == null || uid <= 0) {
                return;
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method targetMethod = signature.getMethod();
            Class<?> targetClass = joinPoint.getTarget().getClass();

            AdminLog methodLog = AnnotationUtils.findAnnotation(targetMethod, AdminLog.class);
            AdminLog classLog = AnnotationUtils.findAnnotation(targetClass, AdminLog.class);
            if ((methodLog != null && methodLog.skip()) || (classLog != null && classLog.skip())) {
                return;
            }

            String uri = ObjectUtil.defaultIfNull(request.getRequestURI(), "");
            if (shouldExclude(uri)) {
                return;
            }

            SysUserLogParam param = new SysUserLogParam();
            param.setUid(uid);
            param.setSiteId(ObjectUtil.defaultIfNull(RequestUtils.siteId(), 0));
            param.setIp(safeIp());
            param.setUsername(resolveUsername());
            param.setUrl(buildUrl(request));
            param.setParams(buildParams(request, joinPoint.getArgs()));
            param.setType(method.toUpperCase());
            param.setOperation(resolveOperation(targetClass, targetMethod, classLog, methodLog));
            sysUserLogService.add(param);
        } catch (Exception e) {
            // 日志失败不影响主业务（try/catch 取描述失败仍继续）
            log.warn("记录管理员操作日志失败: {}", e.getMessage());
        }
    }

    private boolean shouldExclude(String uri) {
        String path = uri.toLowerCase();
        return path.contains("/adminapi/login")
                || path.contains("/adminapi/captcha")
                || path.contains("/adminapi/send/")
                || path.contains("/adminapi/sys/web/");
    }

    private String safeIp() {
        try {
            return ObjectUtil.defaultIfNull(RequestUtils.ip(), "");
        } catch (Exception e) {
            return "";
        }
    }

    private String resolveUsername() {
        UserInfoDto user = RequestUtils.getCurrentUser();
        if (user != null && StrUtil.isNotBlank(user.getUsername())) {
            return user.getUsername();
        }
        return "";
    }

    private String buildUrl(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(ObjectUtil.defaultIfNull(request.getRequestURI(), ""));
        String query = request.getQueryString();
        if (StrUtil.isNotBlank(query)) {
            sb.append('?').append(query);
        }
        return sb.toString();
    }

    /**
     * 对齐 PHP：类 @description + 方法 @description → 「模块-操作」
     * Java 优先 @AdminLog，其次 Swagger @Operation / @Tag
     */
    private String resolveOperation(Class<?> targetClass, Method method, AdminLog classLog, AdminLog methodLog) {
        String classDesc = "";
        if (classLog != null && StrUtil.isNotBlank(classLog.value())) {
            classDesc = classLog.value().trim();
        } else {
            Tag tag = AnnotationUtils.findAnnotation(targetClass, Tag.class);
            if (tag != null && StrUtil.isNotBlank(tag.name())) {
                classDesc = tag.name().trim();
            }
        }

        String methodDesc = "";
        if (methodLog != null && StrUtil.isNotBlank(methodLog.value())) {
            methodDesc = methodLog.value().trim();
        } else {
            Operation operation = AnnotationUtils.findAnnotation(method, Operation.class);
            if (operation != null && StrUtil.isNotBlank(operation.summary())) {
                methodDesc = operation.summary().trim();
            }
        }

        if (StrUtil.isBlank(methodDesc)) {
            return "";
        }
        if (StrUtil.isBlank(classDesc)) {
            return methodDesc;
        }
        // 方法描述已包含模块名时不再重复拼接
        if (methodDesc.startsWith(classDesc)) {
            return methodDesc;
        }
        return classDesc + "-" + methodDesc;
    }

    private String buildParams(HttpServletRequest request, Object[] args) {
        Map<String, Object> params = new LinkedHashMap<>();
        try {
            Enumeration<String> names = request.getParameterNames();
            while (names != null && names.hasMoreElements()) {
                String name = names.nextElement();
                params.put(name, request.getParameter(name));
            }
            if (args != null && args.length > 0) {
                List<Object> bodyArgs = new ArrayList<>();
                for (Object arg : args) {
                    if (arg == null || arg instanceof HttpServletRequest || arg instanceof MultipartFile
                            || arg instanceof MultipartFile[]) {
                        continue;
                    }
                    // 跳过 Servlet / BindingResult 等框架对象
                    String className = arg.getClass().getName();
                    if (className.startsWith("jakarta.servlet.")
                            || className.startsWith("javax.servlet.")
                            || className.startsWith("org.springframework.validation.")
                            || className.startsWith("org.springframework.web.")) {
                        continue;
                    }
                    bodyArgs.add(arg);
                }
                if (!bodyArgs.isEmpty()) {
                    params.put("_body", bodyArgs.size() == 1 ? bodyArgs.get(0) : bodyArgs);
                }
            }
            return JSONUtil.toJsonStr(params, JSON_CONFIG);
        } catch (Exception e) {
            return "{}";
        }
    }
}
