package com.niu.mall.service.admin.web.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页楼层参数
*/
@Data
public class MallWebFloorSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;


    /** 排序号 */
    private Integer sort;

}
