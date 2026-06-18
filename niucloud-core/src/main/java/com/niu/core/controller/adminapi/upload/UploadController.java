package com.niu.core.controller.adminapi.upload;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.upload.UploadExtTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.upload.IUploadConfigService;
import com.niu.core.service.core.upload.param.UploadConfigParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adminapi/sys/upload")
public class UploadController {

    @Resource
    private IUploadConfigService uploadConfigService;

    /**
     * 获取略缩图规格
     *
     * @return 上传压缩分类
     */
    @GetMapping("/thumb_type")
    public Result<?> getThumbType() {
        return Result.success(UploadThumbTypeEnum.getTypeList());
    }

    /**
     * 查询配置详情
     *
     * @return 上传文件分类和对应后缀名
     */
    @GetMapping("/ext_of_type")
    public Result<?> getExtOfType() {
        return Result.success(UploadExtTypeEnum.getMap());
    }

    @GetMapping("/config")
    public Result<?> getUploadConfig() {
        return Result.success(uploadConfigService.getUploadConfig());
    }

    @PutMapping("/config")
    public Result<?> setUploadConfig(@Validated @RequestBody UploadConfigParam param) {
        if (param.getVideoSize() <= 0) return Result.fail("视频大小必须大于0");
        if (param.getImageSize() <= 0) return Result.fail("图片大小必须大于0");
        uploadConfigService.setUploadConfig(param);
        return Result.success();
    }

}
