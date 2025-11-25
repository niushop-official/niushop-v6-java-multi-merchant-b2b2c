package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.goods.ILabelGroupService;
import com.niu.mall.service.site.goods.param.LabelGroupModifySortParam;
import com.niu.mall.service.site.goods.param.LabelGroupParam;
import com.niu.mall.service.site.goods.param.LabelGroupSearchParam;
import com.niu.mall.service.site.goods.vo.LabelGroupInfoVo;
import com.niu.mall.service.site.goods.vo.LabelGroupListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品标签分组表控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods/label/group")
public class LabelGroupController {

    @Resource
    ILabelGroupService labelGroupService;

    /**
     * 商品标签分组表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<LabelGroupListVo>>
     */
    @GetMapping("")
    public Result<PageResult<LabelGroupListVo>> page(@Validated PageParam pageParam, @Validated LabelGroupSearchParam searchParam) {
        PageResult<LabelGroupListVo> list = labelGroupService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签分组列表
     * @param searchParam
     * @return
     */
    @GetMapping("/list")
    public Result<List<LabelGroupListVo>> list(@Validated LabelGroupSearchParam searchParam) {
        return Result.success(labelGroupService.list(searchParam));
    }

    /**
     * 商品标签分组表详情
     *
     * @param id 主键ID
     * @return Result<LabelGroupInfoVo>
     */
    @GetMapping("/{id}")
    public Result<LabelGroupInfoVo> info(@PathVariable("id") Integer id) {
        LabelGroupInfoVo info = labelGroupService.info(id);
        return Result.success(info);
    }

}
