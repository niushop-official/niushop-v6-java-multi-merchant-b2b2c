package com.niu.core.common.config.aspect;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.NoRepeatSubmit;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 */
@Aspect
@Component
public class NoRepeatSubmitAspect {

    private static final String KEY_PREFIX = "repeat_submit:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        String key = buildKey(joinPoint);
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(
                KEY_PREFIX + key, "1", noRepeatSubmit.interval(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new AdminException(noRepeatSubmit.message());
        }
        return joinPoint.proceed();
    }

    private String buildKey(ProceedingJoinPoint joinPoint) {
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.siteId();
        String uri = RequestUtils.getReqeustURI();
        String paramsHash = DigestUtil.md5Hex(JSONUtil.toJsonStr(joinPoint.getArgs()));
        return (uid != null ? uid : 0) + ":" + (siteId != null ? siteId : 0) + ":" + uri + ":" + paramsHash;
    }
}
