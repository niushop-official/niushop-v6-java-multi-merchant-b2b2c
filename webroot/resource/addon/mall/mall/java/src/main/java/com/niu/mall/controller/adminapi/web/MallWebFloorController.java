package com.niu.mall.controller.adminapi.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.web.IMallWebFloorService;
import com.niu.mall.service.admin.web.param.MallWebFloorParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSearchParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSortParam;
import com.niu.mall.service.admin.web.vo.MallWebFloorInfoVo;
import com.niu.mall.service.admin.web.vo.MallWebFloorListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 首页楼层控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/web/floor")
public class MallWebFloorController {

    @Resource
    IMallWebFloorService mallWebFloorService;

    /**
     * 首页楼层分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallWebFloorListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallWebFloorListVo>> page(@Validated PageParam pageParam, @Validated MallWebFloorSearchParam searchParam) {
        PageResult<MallWebFloorListVo> list = mallWebFloorService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 首页楼层详情
     *
     * @param id 主键ID
     * @return Result<MallWebFloorInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MallWebFloorInfoVo> info(@PathVariable("id") Integer id) {
        MallWebFloorInfoVo info = mallWebFloorService.info(id);
        return Result.success(info);
    }

    /**
     * 首页楼层添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallWebFloorParam addParam) {
        mallWebFloorService.add(addParam);
        return Result.success();
    }

    /**
     * 首页楼层编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallWebFloorParam editParam) {
        mallWebFloorService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 首页楼层删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        mallWebFloorService.del(id);
        return Result.success();
    }

    /**
     * 首页楼层排序
     * @param sortParam
     * @return
     */
    @PutMapping("/sort")
    public Result<Object> sort(@Validated @RequestBody MallWebFloorSortParam sortParam) {
        mallWebFloorService.editSort(sortParam);
        return Result.success();
    }

}
