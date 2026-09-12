package com.niu.core.common.config.executable.filter;

import com.alibaba.fastjson2.JSON;
import com.niu.core.common.component.base.ThreadLocalHolder;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 站点资源过滤
 */
@Slf4j
public class WebSiteResourceFilter implements Filter {

    private ApplicationContext applicationContext;

    public WebSiteResourceFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // =======================================================================================//
        // =========访问默认页
        // =======================================================================================//
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        requestUri = requestUri.replace(contextPath, "");
        if ("".equals(requestUri) || "/".equals(requestUri)) {
            response.sendRedirect(GlobalConfig.defaultAccessPath());
            log.info("默认访问： " + GlobalConfig.defaultAccessPath());
            return;
        }
        // =======================================================================================//
        // =========rewrite
        // =======================================================================================//
        String accessPath = WebAppEnvs.get().webRootDownPublic + requestUri;
        File accessFile = new File(accessPath);
        if (accessFile.isFile() && accessFile.exists()) {
            request.getRequestDispatcher(requestUri).forward(request, response);
            return;
        }
        
        // 检查 webRootDownResource/static 目录下的静态资源（如 addon 模板文件）
        // 请求URL: /addon/xxx → 实际路径: webRootDownResource/static/addon/xxx
        String resourcePath = WebAppEnvs.get().webRootDownResource + "static" + requestUri;
        File resourceFile = new File(resourcePath);
        if (resourceFile.isFile() && resourceFile.exists()) {
            log.info("Found static resource: " + resourcePath);
            // 直接读取文件并写入响应
            try {
                response.setContentType("application/octet-stream");
                // 根据URL判断文件类型，设置中文文件名
                String fileName = resourceFile.getName();
                if (requestUri.contains("supply_real_goods_import_template")) {
                    fileName = "实物商品模板.xlsx";
                } else if (requestUri.contains("supply_virtual_goods_import_template")) {
                    fileName = "虚拟商品模板.xlsx";
                }
                response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
                java.io.FileInputStream fis = new java.io.FileInputStream(resourceFile);
                java.io.OutputStream os = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                fis.close();
                os.flush();
                os.close();
            } catch (Exception e) {
                log.error("Error reading static resource: " + resourcePath, e);
            }
            return;
        }
        log.info("Static resource not found: " + resourcePath);
        log.info("url： " + requestUri);
        if (requestUri.startsWith("/adminapp")) {
            request.getRequestDispatcher("/adminapp/index.html").forward(request, response);
            return;
        }

        if (requestUri.startsWith("/admin") && !requestUri.startsWith("/adminapi")) {
            request.getRequestDispatcher("/admin/index.html").forward(request, response);
            return;
        }
        if (requestUri.startsWith("/site") || requestUri.startsWith("/home") || requestUri.startsWith("/directory") || requestUri.startsWith("/circle")) {
            request.getRequestDispatcher("/admin/index.html").forward(request, response);
            return;
        }
        if (requestUri.startsWith("/wap")) {
            request.getRequestDispatcher("/wap/index.html").forward(request, response);
            return;
        }

        if (requestUri.startsWith("/tianmap") && !requestUri.contains(".")) {
            request.getRequestDispatcher("/tianmap/index.html").forward(request, response);
            return;
        }

        if (requestUri.startsWith("/web")) {
            request.getRequestDispatcher("/web/index.html").forward(request, response);
            return;
        }
        Matcher matcher = Pattern.compile("^/MP_verify_(\\w+)\\.txt$").matcher(requestUri);
        if (matcher.find()) {
            String verifyCode = matcher.group(1);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(verifyCode);
            return;
        }
        // =======================================================================================//
        // =========其他业务处理
        // =======================================================================================//
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof BaseException) {
                    BaseException baseException = (BaseException) cause;
                    Result result = Result.fail(baseException.getCode(), baseException.getMsg());
                    String jsonString = JSON.toJSONString(result);
                    contextTypeOfJSON(response, jsonString);
                }
            }
        }
        // 响应之后清除
        ThreadLocalHolder.remove();
    }

    /**
     * @param response
     * @param jsonString
     * @throws IOException
     */
    private void contextTypeOfJSON(HttpServletResponse response, String jsonString) throws IOException {
        response.setHeader("Content-type", "application/json; charset=UTF-8");
        try (
                OutputStream outputStream = response.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write(jsonString);
            writer.flush();
        } catch (Exception e) {
            // ignore
        }

    }

}
