package com.niu.core.controller.siteapi.marketing;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.components.ComponentsEnum;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.admin.marketing.param.FullReductionSearchParam;
import com.niu.core.service.admin.marketing.param.FullReductionStatusParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.admin.marketing.vo.FullReductionListVo;
import com.niu.core.service.site.marketing.ISiteFullReductionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 站点满减活动控制器
 * <p>
 * 路径对齐 PHP：adminapi/marketing/site/full_reduction
 * （由 mall/site 迁至 marketing/site）
 */
@RestController
@RequestMapping("adminapi/marketing/site/full_reduction")
public class FullReductionController {

    @Resource
    private ISiteFullReductionService siteFullReductionService;

    /**
     * 获取站点满减活动分页列表
     * GET marketing/site/full_reduction/list
     */
    @GetMapping("/list")
    public Result<PageResult<FullReductionListVo>> list(@Validated PageParam pageParam,
                                                        @Validated FullReductionSearchParam searchParam) {
        PageResult<FullReductionListVo> list = siteFullReductionService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取站点满减活动初始化数据
     * GET marketing/site/full_reduction/init
     */
    @GetMapping("/init")
    public Result<FullReductionInitVo> init(@RequestParam(value = "id", required = false) Integer id) {
        FullReductionInitVo initVo = siteFullReductionService.init(id);
        return Result.success(initVo);
    }

    /**
     * 获取站点满减活动详情
     * GET marketing/site/full_reduction/info/{id}
     */
    @GetMapping("/info/{id}")
    public Result<FullReductionInfoVo> info(@Validated @PathVariable("id") Integer id) {
        FullReductionInfoVo info = siteFullReductionService.info(id);
        return Result.success(info);
    }

    /**
     * 添加站点满减活动
     * POST marketing/site/full_reduction/add
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody FullReductionParam addParam) {
        siteFullReductionService.add(addParam);
        return Result.success();
    }

    /**
     * 编辑站点满减活动
     * POST marketing/site/full_reduction/edit/{id}
     */
    @PostMapping("/edit/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id,
                               @Validated @RequestBody FullReductionParam modifyParam) {
        siteFullReductionService.edit(id, modifyParam);
        return Result.success();
    }

    /**
     * 删除站点满减活动
     * DELETE marketing/site/full_reduction/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@Validated @PathVariable("id") Integer id) {
        siteFullReductionService.del(id);
        return Result.success();
    }

    /**
     * 设置站点满减活动状态
     * PUT marketing/site/full_reduction/setStatus/{id}
     */
    @PutMapping("/setStatus/{id}")
    public Result<Object> setStatus(@Validated @PathVariable("id") Integer id,
                                    @Validated @RequestBody FullReductionStatusParam status) {
        siteFullReductionService.setStatus(id, status.getStatus());
        return Result.success();
    }

    /**
     * 检测商品是否可参与站点满减活动
     * POST marketing/site/full_reduction/check
     */
    @PostMapping("/check")
    public Result<Object> check(@Validated @RequestBody FullReductionParam params) {
        Map<String, Object> result = siteFullReductionService.checkGoods(params);
        return Result.success(result);
    }

    /**
     * 获取商品选择组件
     * GET marketing/site/full_reduction/goods_select/component
     */
    @GetMapping("/goods_select/component")
    public Result<Map<String, String>> getGoodsSelectComponent() {
        Map<String, String> result = new LinkedHashMap<>();
        Object item = ComponentsEnum.getComponents("full_reduction_goods_select");
        if (item instanceof JSONObject json && !json.isEmpty()) {
            String component = json.getStr("component", "");
            if (StrUtil.isNotBlank(component)) {
                result.put("component", component);
                return Result.success(result);
            }
        }
        result.put("component", "/src/addon/mall/views/site/marketing/full_reduction/components/goods-select-popup.vue");
        return Result.success(result);
    }
}
