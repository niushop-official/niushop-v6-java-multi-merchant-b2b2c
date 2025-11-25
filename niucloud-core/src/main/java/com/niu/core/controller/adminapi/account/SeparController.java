package com.niu.core.controller.adminapi.account;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.service.admin.account.ISeparService;
import com.niu.core.service.admin.account.param.SeparParam;
import com.niu.core.service.admin.account.param.SeparSearchParam;
import com.niu.core.service.admin.account.vo.SeparDetailVo;
import com.niu.core.service.admin.account.vo.SeparInfoVo;
import com.niu.core.service.admin.account.vo.SeparListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 控制器
 */

@RestController
@RequestMapping("adminapi/spear/admin")
public class SeparController {

    @Resource
    ISeparService separService;

    /**
     * 分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SeparListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SeparListVo>> page(@Validated PageParam pageParam, @Validated SeparSearchParam searchParam) {
        PageResult<SeparListVo> list = separService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 详情
     *
     * @param id 主键ID
     * @return Result<SeparInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<List<SeparDetailVo>> info(@PathVariable("id") Integer id) {
        return Result.success(separService.detail(id));
    }

    /**
     * 分账信息
     *
     * @param id 主键ID
     * @return Result<SeparInfoVo>
     */
    @GetMapping("/info/{id}")
    public Result<SeparInfoVo> getInfo(@PathVariable("id") Integer id) {
        return Result.success(separService.getInfo(id));
    }

    /**
     * 获取分账状态
     * @return
     */
    @GetMapping("/status")
    public Result getStatus() {
        return Result.success(SeparStatusEnum.getMap());
    }

    /**
     * 分账
     * @param id
     * @return
     */
    @PostMapping("/separ/{id}")
    public Result<?> separ(@PathVariable("id") Integer id) {
        separService.separ(id);
        return Result.success();
    }
}
