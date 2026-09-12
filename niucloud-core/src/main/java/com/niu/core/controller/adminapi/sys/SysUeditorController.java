package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysAttachmentService;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.admin.sys.vo.SysUeditorConfigVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("adminapi/sys/")
@SaCheckLogin
public class SysUeditorController {

    @Resource
    ISysAttachmentService sysAttachmentService;

    /**
     * 获取百度编辑器配置
     */
    @GetMapping("/ueditor")
    public Result<SysUeditorConfigVo> getConfig(){
        SysUeditorConfigVo sysUeditorConfigVo = new SysUeditorConfigVo();
        return Result.success(sysUeditorConfigVo);
    }

    /**
     * 百度编辑器文件上传
     * action=image 上传图片, action=video 上传视频
     */
    @PostMapping("/ueditor")
    public Map<String, Object> upload(@RequestParam("action") String action,
                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                      AttachmentUploadParam param) {
        Map<String, Object> result = new HashMap<>();
        if (file == null || file.isEmpty()) {
            result.put("state", "请选择文件");
            return result;
        }
        param.setFile(file);
        try {
            AttachmentUploadVo uploadVo;
            if ("image".equals(action)) {
                uploadVo = sysAttachmentService.image(param);
            } else if ("video".equals(action)) {
                uploadVo = sysAttachmentService.video(param);
            } else {
                result.put("state", "不支持的上传类型: " + action);
                return result;
            }
            result.put("state", "SUCCESS");
            result.put("url", uploadVo.getUrl());
            result.put("title", uploadVo.getUrl());
            result.put("original", uploadVo.getUrl());
        } catch (Exception e) {
            result.put("state", e.getMessage());
        }
        return result;
    }
}
