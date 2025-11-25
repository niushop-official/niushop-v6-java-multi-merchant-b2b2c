package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.admin.diy.param.*;
import com.niu.core.service.admin.diy.vo.DiyPageInfoVo;
import com.niu.core.service.admin.diy.vo.DiyPageListVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 自定义页面管理
 */
@RestController
@RequestMapping("/adminapi/diy/theme")
@SaCheckLogin
public class DiyThemeController {

    @Resource
    IDiyThemeService diyThemeService;

    /**
     * 获取主题色
     * @return
     */
    @GetMapping("")
    public Result<JSONObject> getDiyTheme() {
        return Result.success(diyThemeService.getDiyTheme());
    }

    /**
     * 自定义页面添加
     *
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> setDiyTheme(@Validated @RequestBody DiyThemeSetParam diyThemeSetParam) {
        diyThemeService.setDiyTheme(diyThemeSetParam);
        return Result.success();
    }


    /**
     * 获取主题色
     * @return
     */
    @GetMapping("/color")
    public Result<Object> getDefaultThemeColor(@Validated DiyThemeColorParam data) {
        return Result.success(diyThemeService.getDefaultThemeColor(data));
    }

    /**
     * 自定义页面添加
     *
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> addDiyTheme(@Validated @RequestBody DiyThemeParam diyThemeParam) {
        diyThemeService.addDiyTheme(diyThemeParam);
        return Result.success();
    }

    /**
     * 主题色编辑
     *
     * @return Result<Object>
     */
    @PutMapping("/edit/{id}")
    public Result<Object> editDiyTheme(@PathVariable("id") Integer id, @Validated @RequestBody DiyThemeParam diyThemeParam) {
        diyThemeService.editDiyTheme(id, diyThemeParam);
        return Result.success();
    }

    /**
     * 主题色删除
     *
     * @return Result<Object>
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> editDiyTheme(@PathVariable("id") Integer id) {
        diyThemeService.delDiyTheme(id);
        return Result.success();
    }

}
