package com.niu.core.controller.adminapi.stat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.admin.stat.param.SysStatSearchParam;
import com.niu.core.service.admin.stat.vo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jdk.jfr.Description;

import jakarta.annotation.Resource;

import java.util.Map;

/**
 * 小时统计控制器
 */

@RestController
@RequestMapping("adminapi/stat")
@SaCheckLogin
public class StatController {

    @Resource
    IStatService statService;


    @GetMapping("/index")
    public Result index() {
        return Result.success(statService.getIndexData());
    }

    /**
     * 查询统计列表
     * @param pageParam
     * @param param
     * @return
     */
    @GetMapping("/finance/accounts/list")
    public Result<PageResult<SysStatListVo>> financeAccountsList(@Validated PageParam pageParam, SysStatSearchParam param) {
        return Result.success(statService.financeAccountsList(pageParam,param));
    }


    /**
     * 查询财务账户统计
     * @param param 包含日期范围参数
     * @return 统计数据
     */
    @GetMapping("/finance/accounts/total")
    public Result<Map<String, Object>> financeAccountsTotal(@Validated SysStatSearchParam param) {
        return Result.success(statService.financeAccountsTotal(param));
    }

    /**
     * 账单详情
     * @param param 包含日期和类型参数（date: 日期，type: 账单类型 day 日账单 month 月账单）
     * @return 账单详情数据
     */
    @Description("账单详情")
    @GetMapping("/finance/accounts/detail")
    public Result<SysStatListVo> financeAccountsDetail(@Validated SysStatSearchParam param) {
        return Result.success(statService.financeAccountsDetail(param));
    }

    /**
     * 详情
     *
     * @param id 主键ID
     * @return Result<SysStatInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SysStatInfoVo> info(@PathVariable("id") Integer id) {
        SysStatInfoVo info = statService.info(id);
        return Result.success(info);
    }

    /**
     * 首页获取数据集
     * @return
     */
    @GetMapping("/index/chart")
    public Result chart(){
        return Result.success(statService.getSartChartVoList());
    }

    /**
     * 首页获取最新动态
     * @return
     */
    @GetMapping("/index/news")
    public Result news(@Validated @RequestParam(name = "num",required = false) Integer num){
        return Result.success(statService.getArticleList(num));
    }

}
