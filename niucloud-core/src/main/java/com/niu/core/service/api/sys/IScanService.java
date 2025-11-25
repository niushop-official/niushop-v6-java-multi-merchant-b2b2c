package com.niu.core.service.api.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.service.api.sys.param.SysCheckScanParam;

public interface IScanService {

    String checkScan(SysCheckScanParam param);

}
