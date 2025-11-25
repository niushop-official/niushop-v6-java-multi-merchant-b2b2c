package com.niu.core.service.api.sys;

import cn.hutool.json.JSONObject;

public interface ITaskService {

    JSONObject getGrowthTask();

    JSONObject getPointTask();
}
