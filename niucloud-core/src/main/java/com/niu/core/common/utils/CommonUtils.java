package com.niu.core.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.GetPosterParam;
import com.niu.core.service.core.sys.ICoreUploadService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class CommonUtils {
    /**
     * 判断字符串是否是链接
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new URL(str);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 全局唯一ID
     * @return
     */
    public static String createNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 全局唯一ID
     * @return
     */
    public static String createNo(String  prefix, String  tag) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        prefix =  StringUtils.isNotEmpty( prefix) ? prefix : "";
        tag =  StringUtils.isNotEmpty( tag) ? tag : "";
        return prefix + DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + tag + id;
    }


    /**
     * 生成缩略图
     *
     * @param siteId
     * @param path
     * @param type all | small | middle | big
     * @return
     */
    public static Map<String, String> thumbImage(Integer siteId, String path, String type) {
        ICoreUploadService coreUploadService = SpringContext.bean(ICoreUploadService.class);
        return coreUploadService.thumb(siteId, path, type).getDataMap();
    }
    public static String thumbImageMiddle(Integer siteId, String path) {
        ICoreUploadService coreUploadService = SpringContext.bean(ICoreUploadService.class);
        return ObjectUtil.defaultIfEmpty(coreUploadService.thumb(siteId, path, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()), path);
    }

    public static String thumbImageSmall(Integer siteId, String path) {
        ICoreUploadService coreUploadService = SpringContext.bean(ICoreUploadService.class);
        return ObjectUtil.defaultIfEmpty(coreUploadService.thumb(siteId, path, UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()), path);
    }

    public static String thumbImageBig(Integer siteId, String path) {
        ICoreUploadService coreUploadService = SpringContext.bean(ICoreUploadService.class);
        return ObjectUtil.defaultIfEmpty(coreUploadService.thumb(siteId, path, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()), path);
    }

    /**
     * 获取海报
     * @param siteId
     * @return
     */
    public static String poster(Integer siteId,Integer id, String type, String channel, Map<String, Object> params,Boolean isThrowException) {
        ICorePosterService corePosterService = SpringContext.bean(ICorePosterService.class);
        if(isThrowException == null){
            isThrowException = true;
        }
        GetPosterParam param = new GetPosterParam();
        param.setSiteId(siteId);
        param.setId(id);
        param.setType(type);
        param.setParam(params);
        param.setIsThrowException(isThrowException);
        param.setChannel(channel);
        return ObjectUtil.defaultIfEmpty(corePosterService.get(param),"");
    }

    /**
     * 获取site表中 服务器地址
     */
    public static String getServerDomain() {
        ISysConfigService sysConfigService = SpringContext.bean(ISysConfigService.class);
        return ObjectUtil.defaultIfEmpty(sysConfigService.getServerDomain(),"");
    }
}
