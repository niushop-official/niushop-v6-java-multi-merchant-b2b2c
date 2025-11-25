package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.diy.AdvPositionEnum;
import com.niu.core.service.admin.diy.IDiyAdvService;
import com.niu.core.service.admin.diy.param.DiyAdvParam;
import com.niu.core.service.admin.diy.param.DiyAdvSearchParam;
import com.niu.core.service.admin.diy.param.DiyAdvSortParam;
import com.niu.core.service.admin.diy.vo.DiyAdvInfoVo;
import com.niu.core.service.admin.diy.vo.DiyAdvListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告表控制器
 */

@RestController
@RequestMapping("adminapi/diy")
@SaCheckLogin
public class DiyAdvController {

    @Resource
    IDiyAdvService webAdvService;

    @GetMapping("adv_position")
    public Result<JSONArray> position() {
        return Result.success(AdvPositionEnum.getPosition());
    }
    /**
     * 广告表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebAdvListVo>>
     */
    @GetMapping("/adv")
    public Result<PageResult<DiyAdvListVo>> page(@Validated PageParam pageParam, @Validated DiyAdvSearchParam searchParam) {
        PageResult<DiyAdvListVo> list = webAdvService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 广告列表不分页
     * @param searchParam
     * @return
     */
    @GetMapping("/adv/list")
    public Result<List<DiyAdvListVo>> list(@Validated DiyAdvSearchParam searchParam) {
        List<DiyAdvListVo> list = webAdvService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 广告表详情
     *
     * @param id 主键ID
     * @return Result<WebAdvInfoVo>
     */
    @GetMapping("/adv/{id}")
    public Result<DiyAdvInfoVo> info(@PathVariable("id") Integer id) {
        DiyAdvInfoVo info = webAdvService.info(id);
        return Result.success(info);
    }

    /**
     * 广告表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/adv")
    public Result<Object> add(@Validated @RequestBody DiyAdvParam addParam) {
        webAdvService.add(addParam);
        return Result.success();
    }

    /**
     * 广告表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("adv/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody DiyAdvParam editParam) {
        webAdvService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 广告表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/adv/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        webAdvService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     * @param sortParam
     * @return
     */
    @PutMapping("/adv/sort")
    public Result<Object> editSort(@Validated @RequestBody DiyAdvSortParam sortParam) {

        webAdvService.editSort(sortParam);
        return Result.success();
    }

}
