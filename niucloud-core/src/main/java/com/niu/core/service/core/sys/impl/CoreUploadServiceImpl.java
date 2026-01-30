package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.upload.model.*;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateFormatUtils;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.enums.upload.UploadRolesEnum;
import com.niu.core.mapper.sys.SysAttachmentMapper;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.service.core.upload.ICoreStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
public class CoreUploadServiceImpl implements ICoreUploadService {

    @Autowired
    private SysAttachmentMapper sysAttachmentMapper;

    @Autowired
    private ICoreStorageService coreStorageService;

    private String rootPath = "upload";

    /**
     * 上传附件
     *
     * @param attachmentUploadParam
     */
    public AttachmentUploadVo upload(AttachmentUploadParam attachmentUploadParam) {
        // 上传相对目录
        attachmentUploadParam.setDir(this.rootPath + "/" + attachmentUploadParam.getDir());
        // 校验上传文件
        this.validate(attachmentUploadParam);
        // 生成新的文件名
        this.generateNewName(attachmentUploadParam);

        JSONObject configObject = null;
        String storageType = null;
        
        // 获取站点的上传配置
        if (ObjectUtil.isNotEmpty(attachmentUploadParam.getStorageType())) {
            storageType = attachmentUploadParam.getStorageType();
            configObject = coreStorageService.getStorageByType(storageType);
        } else {
            configObject = coreStorageService.getDefaultStorage();
            // 如果默认存储配置为空，抛出异常（与PHP逻辑一致）
            if (configObject == null || configObject.isEmpty()) {
                throw new CommonException("上传服务未配置，请先配置存储方式");
            }
            storageType = configObject.getStr("storage_type");
        }

        if (configObject == null) {
            configObject = new JSONObject();
        }
        
        configObject.set("storage_type", storageType);
        
        // 获取配置参数
        JSONObject configParams = configObject.getJSONObject("config_params");
        if (configParams == null) {
            configParams = new JSONObject();
        }

        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(storageType, configParams);
        Assert.notNull(uploadProvider, "上传服务未配置，请先配置存储方式");
        
        // 构建上传参数
        UploadModel uploadModel = new UploadModel();
        uploadModel.setUploadFile(attachmentUploadParam.getFile());
        uploadModel.setUploadType(attachmentUploadParam.getAttType());
        uploadModel.setUploadFilePath(attachmentUploadParam.getDir());
        uploadModel.setUploadFileName(attachmentUploadParam.getNewFilename());
        // 上传操作
        UploadModelResult uploadModelResult = uploadProvider.upload(uploadModel);
        // Service返回结果
        AttachmentUploadVo attachmentUploadVo = new AttachmentUploadVo();
        // 是否上传到相册
        if (attachmentUploadParam.getIsAttachment().equals(1)) {
            SysAttachment newSysAttachment = this.buildSysAttachment(attachmentUploadParam, uploadModelResult);
            newSysAttachment.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
            sysAttachmentMapper.insert(newSysAttachment);
            attachmentUploadVo.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
            attachmentUploadVo.setAttId(newSysAttachment.getAttId());
        } else {
            attachmentUploadVo.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
        }
        return attachmentUploadVo;
    }

    @Override
    public AttachmentUploadVo uploadBase64(AttachmentUploadParam attachmentUploadParam) {
        // 上传相对目录
        attachmentUploadParam.setDir(this.rootPath + "/" + attachmentUploadParam.getDir());

        String fileName = SecureUtil.md5(attachmentUploadParam.getContent()) + ".jpg";
        attachmentUploadParam.setNewFilename(fileName);
        JSONObject configObject = null;
        String storageType = null;

        // 获取站点的上传配置
        if (ObjectUtil.isNotEmpty(attachmentUploadParam.getStorageType())) {
            storageType = attachmentUploadParam.getStorageType();
            configObject = coreStorageService.getStorageByType(storageType);
        } else {
            configObject = coreStorageService.getDefaultStorage();
            // 如果默认存储配置为空，抛出异常（与PHP逻辑一致）
            if (configObject == null || configObject.isEmpty()) {
                throw new CommonException("上传服务未配置，请先配置存储方式");
            }
            storageType = configObject.getStr("storage_type");
        }

        if (configObject == null) {
            configObject = new JSONObject();
        }

        configObject.set("storage_type", storageType);

        // 获取配置参数
        JSONObject configParams = configObject.getJSONObject("config_params");
        if (configParams == null) {
            configParams = new JSONObject();
        }

        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(storageType, configParams);
        Assert.notNull(uploadProvider, "上传服务未配置，请先配置存储方式");

        // 构建上传参数
        Base64Model uploadModel = new Base64Model();
        uploadModel.setContent(attachmentUploadParam.getContent());
        uploadModel.setUploadFilePath(attachmentUploadParam.getDir());
        uploadModel.setUploadFileName(fileName);
        // 上传操作
        String path = uploadProvider.base64(uploadModel);
        String url = uploadProvider.getAccessUrl(path);

        //base64图片大小
        long attSize = getBase64ImageSize(attachmentUploadParam.getContent());
        UploadModelResult uploadModelResult = new UploadModelResult();
        uploadModelResult.setAccessUrl(url);
        uploadModelResult.setSize(attSize);
        uploadModelResult.setOriginalFilename("base64_image_" + System.currentTimeMillis());
        uploadModelResult.setUploadMethod(storageType);

        // Service返回结果
        AttachmentUploadVo attachmentUploadVo = new AttachmentUploadVo();
        // 是否上传到相册
        if (attachmentUploadParam.getIsAttachment().equals(1)) {
            SysAttachment newSysAttachment = this.buildSysAttachment(attachmentUploadParam, uploadModelResult);
            newSysAttachment.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
            sysAttachmentMapper.insert(newSysAttachment);
            attachmentUploadVo.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
            attachmentUploadVo.setAttId(newSysAttachment.getAttId());
        } else {
            attachmentUploadVo.setUrl(uploadProvider.getAccessUrl(uploadModelResult.getAccessUrl()));
        }
        return attachmentUploadVo;
    }

    /**
     * 根据param和上传结果构建实体对象
     *
     * @param attachmentUploadParam
     * @param uploadModelResult
     * @return
     */
    private SysAttachment buildSysAttachment(AttachmentUploadParam attachmentUploadParam, UploadModelResult uploadModelResult) {
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setSiteId(attachmentUploadParam.getSiteId());
        sysAttachment.setName(attachmentUploadParam.getNewFilename());
        sysAttachment.setRealName(uploadModelResult.getOriginalFilename());
        sysAttachment.setPath(attachmentUploadParam.getDir() + attachmentUploadParam.getNewFilename());
        sysAttachment.setDir(attachmentUploadParam.getDir());
        sysAttachment.setAttSize(uploadModelResult.getSize());
        sysAttachment.setAttType(attachmentUploadParam.getAttType());
        sysAttachment.setStorageType(uploadModelResult.getUploadMethod());
        sysAttachment.setCateId(attachmentUploadParam.getCateId());
        sysAttachment.setCreateTime(System.currentTimeMillis() / 1000);
        sysAttachment.setUrl(uploadModelResult.getAccessUrl());
        return sysAttachment;
    }

    /**
     * 生成新文件名
     *
     * @param attachmentUploadParam
     */
    private void generateNewName(AttachmentUploadParam attachmentUploadParam) {
        String ext = FilenameUtils.getExtension(attachmentUploadParam.getFile().getOriginalFilename());
        String name = (System.currentTimeMillis()) + "_" + attachmentUploadParam.getStorageType() + "." + ext;
        attachmentUploadParam.setNewFilename(name);
    }

    /**
     * 上传文件校验
     *
     * @param attachmentUploadParam
     */
    private void validate(AttachmentUploadParam attachmentUploadParam) {
        // 上传文件内容校验
        MultipartFile file = attachmentUploadParam.getFile();
        Assert.notNull(file, "上传失败!");
        // 上传文件后缀和大小以及Mime类型
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        Long size = file.getSize();
        String mime = file.getContentType();
        for (UploadRolesEnum item : UploadRolesEnum.values()) {
            if (item.getName().equals(attachmentUploadParam.getAttType())) {
                if (!Arrays.asList(item.getExt()).contains(ext)) {
                    throw new CommonException("上传文件后缀不允许");
                }
                if (!Arrays.asList(item.getMime()).contains(mime)) {
                    throw new CommonException("上传文件MIME类型不允许");
                }
                if (item.getSize().compareTo(size) == -1) {
                    throw new CommonException("上传文件大小不符");
                }
            }
        }
    }

    @Override
    public void delete(Integer siteId, String storageType, String path) {
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(siteId, "STORAGE");
        String defaultValue = storageType;
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);
        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setFilePath(path);
        DeleteModelResult deleteModelResult = uploadProvider.delete(deleteModel);
        if (deleteModelResult.isResult()) {
            // 删除成功 -> 忽略
        }
    }

    /**
     * 计算base64图片大小
     */
    private long getBase64ImageSize(String base64String) {
        if (base64String.contains("base64,")) {
            base64String = base64String.substring(base64String.indexOf("base64,") + 7);
        }
        long size = (long) (base64String.length() * 3.0 / 4);
        int paddingCount = (int) base64String.chars().filter(ch -> ch == '=').count();
        size -= paddingCount;

        return size;
    }

    /**
     * 生成缩略图
     * @param siteId
     * @param path
     * @param type
     * @return
     */
    @Override
    public ThumbModelResult thumb(Integer siteId, String path, String type) {
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "STORAGE");
        String defaultValue = rootJSONObject.getStr("default", StorageEnum.LOCAL.getCode());
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);

        ThumbModel model = new ThumbModel();
        model.setType(type);
        model.setFilePath(path);
        return uploadProvider.thumb(model);
    }
}
