package com.niu.core.service.admin.sys.param;

import cn.hutool.json.JSONArray;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 二维码生成参数
 */
@Data
public class SysQrcodeSearchParam {

    private String folder;

    private String page;
}