package com.niu.core.service.adminapp.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.adminapp.TodoAppEnum;
import com.niu.core.service.adminapp.ITodoService;
import com.niu.core.service.adminapp.param.PstarEditParam;
import com.niu.core.service.adminapp.vo.TodoItemVo;
import com.niu.core.service.core.adminapp.ICoreAdminAppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 待办服务实现类
 */
@Service
@Slf4j
public class TodoServiceImpl implements ITodoService {

    @Resource
    private ICoreAdminAppService coreAdminAppService;

    @Override
    public List<TodoItemVo> getTodoList() {
        Integer siteId = RequestUtils.siteId();
        JSONArray list = TodoAppEnum.getTodo(siteId);
        Integer uid = RequestUtils.uid();

        JSONArray keys = coreAdminAppService.getValue(uid, siteId, "todo");
        int todoLimit = 4;
        List<JSONObject> todoList;
        if (keys == null || keys.isEmpty()) {
            List<JSONObject> listItems = JSONUtil.toList(list, JSONObject.class);
            int limit = Math.min(todoLimit, listItems.size());
            todoList = listItems.subList(0, limit);
        } else {
            todoList = new ArrayList<>();
            List<String> keysList = JSONUtil.toList(keys, String.class);
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                String itemKey = item.getStr("key");
                if (itemKey != null && keysList.contains(itemKey)) {
                    int sortIndex = keysList.indexOf(itemKey);
                    item.set("sort", sortIndex);
                    todoList.add(item);
                }
            }
        }
        List<JSONObject> sortedList = todoList.stream()
                .sorted(Comparator.comparingInt(item -> item.getInt("sort", 0)))
                .collect(Collectors.toList());

        return convertToVoList(sortedList);
    }

    @Override
    public List<TodoItemVo> getAllTodoList() {
        Integer siteId = RequestUtils.siteId();
        JSONArray list = TodoAppEnum.getTodo(siteId);
        List<JSONObject> sortedList = JSONUtil.toList(list, JSONObject.class)
                .stream()
                .sorted(Comparator.comparingInt(item -> item.getInt("sort", 0)))
                .collect(Collectors.toList());

        return convertToVoList(sortedList);
    }

    @Override
    public Boolean setTodoList(PstarEditParam param) {
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.siteId();
        JSONArray valueArray = new JSONArray();

        if (param != null && param.getValue() != null) {
            valueArray = new JSONArray(param.getValue());
        }

        return coreAdminAppService.setValue(uid, siteId, "todo", valueArray);
    }

    /**
     * 将JSONObject列表转换为TodoItemVo列表
     */
    private List<TodoItemVo> convertToVoList(List<JSONObject> jsonList) {
        List<TodoItemVo> voList = new ArrayList<>();
        for (JSONObject jsonObject : jsonList) {
            TodoItemVo vo = new TodoItemVo();
            BeanUtil.copyProperties(jsonObject, vo);
            voList.add(vo);
        }
        return voList;
    }
}
