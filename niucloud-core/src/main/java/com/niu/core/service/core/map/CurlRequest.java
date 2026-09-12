package com.niu.core.service.core.map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP 请求工具类（对应 PHP CurlRequest）
 */
public class CurlRequest {

    private static final int CONNECT_TIMEOUT = 100; // 秒
    private static final int READ_TIMEOUT = 100;    // 秒

    // 懒加载 HTTP Client，跳过 SSL 验证（仅开发/测试使用）
    private static HttpClient client;

    private static HttpClient getClient() {
        if (client == null) {
            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                    .sslContext(getUnsafeSSLContext())
                    .build();
        }
        return client;
    }

    private static SSLContext getUnsafeSSLContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("SSL context initialization failed", e);
        }
    }

    /**
     * 基础 HTTP 请求
     *
     * @param method     请求方法（GET, POST, PUT, DELETE）
     * @param url        请求地址
     * @param postfields POST 数据（可选，GET 时自动拼接到 URL）
     * @return 响应字符串
     * @throws Exception
     */
    public static String httpRequest(String method, String url, Map<String, Object> postfields) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(READ_TIMEOUT))
                .header("User-Agent", "Java HttpClient");

        // 处理 GET 请求：参数拼接到 URL
        if ("GET".equalsIgnoreCase(method)) {
            if (postfields != null && !postfields.isEmpty()) {
                url += (url.contains("?") ? "&" : "?") + buildQueryString(postfields);
            }
            builder.uri(URI.create(url)).GET();
        }
        // 处理 POST/PUT/DELETE
        else {
            HttpRequest.BodyPublisher bodyPublisher;
            if (postfields != null && !postfields.isEmpty()) {
                // 将 Map 转换为 application/x-www-form-urlencoded 格式
                String bodyStr = buildQueryString(postfields);
                bodyPublisher = HttpRequest.BodyPublishers.ofString(bodyStr, StandardCharsets.UTF_8);
                builder.header("Content-Type", "application/x-www-form-urlencoded");
            } else {
                bodyPublisher = HttpRequest.BodyPublishers.noBody();
            }
            builder.uri(URI.create(url));
            if ("POST".equalsIgnoreCase(method)) {
                builder.POST(bodyPublisher);
            } else if ("PUT".equalsIgnoreCase(method)) {
                builder.PUT(bodyPublisher);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                builder.DELETE();
            } else {
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * 将参数 Map 转换为 URL 编码的查询字符串
     */
    private static String buildQueryString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append('&');
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    // ========== 便捷方法（实例方法） ==========

    public String get(String url, Map<String, Object> params) throws Exception {
        return httpRequest("GET", url, params);
    }

    public String get(String url) throws Exception {
        return httpRequest("GET", url, null);
    }

    public String post(String url, Map<String, Object> params) throws Exception {
        return httpRequest("POST", url, params);
    }

    public String post(String url) throws Exception {
        return httpRequest("POST", url, null);
    }

    public String put(String url, Map<String, Object> params) throws Exception {
        return httpRequest("PUT", url, params);
    }

    public String put(String url) throws Exception {
        return httpRequest("PUT", url, null);
    }

    public String delete(String url, Map<String, Object> params) throws Exception {
        return httpRequest("DELETE", url, params);
    }

    public String delete(String url) throws Exception {
        return httpRequest("DELETE", url, null);
    }
}