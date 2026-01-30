package com.niu.core.controller.adminapp;

import com.niu.core.common.domain.Result;
import com.niu.core.service.adminapp.IStatService;
import com.niu.core.service.adminapp.ITodoService;
import com.niu.core.service.adminapp.param.PstarEditParam;
import com.niu.core.service.adminapp.vo.StatItemVo;
import com.niu.core.service.adminapp.vo.TodoItemVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制台控制器
 */
@RestController
@RequestMapping("adminapi/adminapp/site")
public class IndexController {

    @Resource
    private IStatService statService;

    @Resource
    private ITodoService todoService;

    /**
     * 统计列表
     * @return Result<List<StatItemVo>>
     */
    @GetMapping("/stat")
    public Result<List<StatItemVo>> getStatList() {
        return Result.success(statService.getStatList());
    }

    /**
     * 全部统计列表
     * @return Result<List<StatItemVo>>
     */
    @GetMapping("/stat_of_all")

    public Result<List<StatItemVo>> getAllStatList() {
        return Result.success(statService.getAllStatList());
    }

    /**
     * 设置统计列表
     * @param param 参数
     * @return Result
     */
    @PostMapping("/stat")
    public Result setStatList(@RequestBody PstarEditParam param) {
        return Result.success(statService.setStatList(param));
    }

    /**
     * 待办列表
     * @return Result<List<TodoItemVo>>
     */
    @GetMapping("/todo")
    public Result<List<TodoItemVo>> getTodoList() {
        return Result.success(todoService.getTodoList());
    }

    /**
     * 设置待办列表
     * @param param 参数
     * @return Result
     */
    @PostMapping("/todo")
    public Result setTodoList(@RequestBody PstarEditParam param) {
        return Result.success(todoService.setTodoList(param));
    }

    /**
     * 全部待办列表
     * @return Result<List<TodoItemVo>>
     */
    @GetMapping("/todo_of_all")
    public Result<List<TodoItemVo>> getAllTodoList() {
        return Result.success(todoService.getAllTodoList());
    }
}
