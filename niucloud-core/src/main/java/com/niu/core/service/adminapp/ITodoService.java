package com.niu.core.service.adminapp;

import com.niu.core.service.adminapp.vo.TodoItemVo;

import java.util.List;

/**
 * 待办服务接口
 */
public interface ITodoService {

    /**
     * 待办列表
     * @return List<TodoItemVo>
     */
    List<TodoItemVo> getTodoList();

    /**
     * 全部待办列表
     * @return List<TodoItemVo>
     */
    List<TodoItemVo> getAllTodoList();

    /**
     * 设置待办列表
     * @param param 参数
     * @return Boolean
     */
    Boolean setTodoList(com.niu.core.service.adminapp.param.PstarEditParam param);
}
