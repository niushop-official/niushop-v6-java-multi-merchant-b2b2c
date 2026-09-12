package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.service.admin.sys.ISysAttachmentService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.admin.sys.vo.IconCategoryVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentCategoryListVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentConfigVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentListVo;
import com.niu.core.service.core.upload.ICoreUploadConfigService;
import com.niu.core.service.core.upload.vo.UploadConfigVo;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 附件管理控制器
 */

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SysAttachmentController {

    @Resource
    ISysAttachmentService sysAttachmentService;

    @Resource
    private ICoreUploadConfigService uploadConfigService;

    /**
     * 附件管理列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysAttachmentListVo>>
     */
    @GetMapping("/attachment")
    public Result<PageResult<SysAttachmentListVo>> list(@Validated PageParam pageParam, @Validated SysAttachmentSearchParam searchParam) {
        PageResult<SysAttachmentListVo> list = sysAttachmentService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 删除附件
     *
     * @return
     */
    @DeleteMapping("/attachment/del")
    public Result del(@RequestBody SysAttachmentDelParam param) {
        sysAttachmentService.del(param);
        return Result.success();
    }

    /**
     * 移动图片分组
     *
     * @param param
     * @return
     */
    @PutMapping("/attachment/batchmove")
    public Result batchMoveCategory(@Validated @RequestBody SysAttachmentMoveParam param) {
        sysAttachmentService.batchMoveCategory(param);
        return Result.success();
    }

    /**
     * 附件图片上传
     *
     * @return
     */
    @PostMapping("/image")
    public Result<AttachmentUploadVo> image(@RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        UploadConfigVo config = uploadConfigService.getUploadConfig();
        // 校验扩展名
        validateExtension(multipartFile, config.getImageExt(), "图片");
        // 校验大小
        validateFileSize(multipartFile, config.getImageSize(), "图片");
        param.setFile(multipartFile);
        return Result.success(sysAttachmentService.image(param));
    }

    /**
     * 附件视频上传
     *
     * @return
     */
    @PostMapping("/video")
    public Result<AttachmentUploadVo> video(@RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        UploadConfigVo config = uploadConfigService.getUploadConfig();
        validateExtension(multipartFile, config.getVideoExt(), "视频");
        validateFileSize(multipartFile, config.getVideoSize(), "视频");
        param.setFile(multipartFile);
        return Result.success(sysAttachmentService.video(param));
    }

    /**
     * 校验扩展名
     */
    private void validateExtension(MultipartFile file, String[] allowedExts, String fileTypeName) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new CommonException(fileTypeName + "文件格式错误");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> extList = Arrays.stream(allowedExts)
                .filter(StringUtils::isNotEmpty)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        if (!extList.contains(ext)) {
            throw new CommonException(fileTypeName + "类型错误，仅支持：" + String.join(",", extList));
        }
    }

    /**
     * 校验文件大小
     *
     * @param file         文件
     * @param maxSizeB     配置的最大大小（B），若为null或<=0则不校验
     * @param fileTypeName 文件类型名称（用于提示）
     */
    private void validateFileSize(MultipartFile file, Long maxSizeB, String fileTypeName) {
        if (maxSizeB == null || maxSizeB <= 0) {
            return;
        }
        if (file.getSize() > maxSizeB) {
            Float maxSizeKb = maxSizeB / 1024f;
            throw new CommonException(fileTypeName + "大小不能超过 " + String.format("%.2f", maxSizeKb) + "KB");
        }
    }

    /**
     * 附件上传
     *
     * @return
     */
    @PostMapping("/document/{type}")
    public Result<AttachmentUploadVo> document(@PathVariable("type") String type, @RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        param.setFile(multipartFile);
        param.setDocumentType(type);
        return Result.success(sysAttachmentService.document(param));
    }

    /**
     * base64图片上传
     */
    @PostMapping("/base64")
    @Description("base64图片上传")
    public Result<AttachmentUploadVo> base64(@Validated @RequestBody AttachmentUploadParam param) {
        return Result.success(sysAttachmentService.base64(param));
    }

    /**
     * 附件分组列表
     *
     * @param searchParam
     * @return
     */
    @GetMapping("/attachment/category")
    public Result<List<SysAttachmentCategoryListVo>> getCategoryList(@Validated SysAttachmentCategorySearchParam searchParam) {
        List<SysAttachmentCategoryListVo> list = sysAttachmentService.getCategoryList(searchParam);
        return Result.success(list);
    }

    /**
     * 新增附件分组
     *
     * @return Result
     */
    @PostMapping("/attachment/category")
    public Result addCategory(@Validated @RequestBody SysAttachmentCategoryParam addParam) {
        sysAttachmentService.addCategory(addParam);
        return Result.success();
    }

    /**
     * 附件分组添加
     *
     * @param id
     * @return Result<Object>
     */
    @PutMapping("/attachment/category/{id}")
    public Result<Object> editCategory(@PathVariable("id") Integer id, @Validated @RequestBody SysAttachmentCategoryParam editParam) {
        sysAttachmentService.editCategory(id, editParam);
        return Result.success();
    }

    /**
     * 删除附件分组
     *
     * @return Result<Object>
     */
    @DeleteMapping("/attachment/category/{id}")
    public Result<Object> delCategory(@PathVariable("id") Integer id) {
        sysAttachmentService.delCategory(id);
        return Result.success();
    }

    public Result<Object> moveCategory(Integer attrId) {
        return Result.success();
    }

    @GetMapping("attachment/icon_category")
    public Result<List<IconCategoryVo>> getIconCategoryList(@RequestParam(value = "name", required = false) String name) {
        return Result.success(sysAttachmentService.getIconCategoryList(name));
    }


    @GetMapping("attachment/icon")
    public Result<Object> getIconList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "limit", defaultValue = "20") Integer limit,
                                      @RequestParam(value = "cate_id", required = false) String cateId,
                                      @RequestParam(value = "real_name", required = false) String realName) {
        return Result.success(sysAttachmentService.getIconList(page, limit, cateId, realName));
    }

    @GetMapping("/attachment/config")
    public Result<SysAttachmentConfigVo> getConfig() {
        SysAttachmentConfigVo config = sysAttachmentService.getConfig();
        return Result.success(config);
    }

    @PutMapping("/attachment/config")
    public Result<Object> setConfig(@Validated @RequestBody SysAttachmentConfigParam param) {
        sysAttachmentService.setConfig(param);
        return Result.success();
    }

}
