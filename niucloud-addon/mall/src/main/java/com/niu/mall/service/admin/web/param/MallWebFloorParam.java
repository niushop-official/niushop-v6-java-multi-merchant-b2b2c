package com.niu.mall.service.admin.web.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 首页楼层参数
*/
@Data
public class MallWebFloorParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 广告图1 */
    private String advImg1 = "";

    /** 广告图2 */
    private String advImg2 = "";

    /** 楼层名称 */
    private String floorName = "";


    /** 配置参数 */
    private String config;

    /** 楼层标题 */
    private String title = "";

    /** 楼层副标题 */
    private String subTitle = "";

    /** 排序号 */
    private Integer sort = 0;

    /** 配置模板 */
    private String temp = "default";

    /** 跳转链接 */
    private JSONObject url;

    /** 商品id */
    private JSONArray goodsIds;

    private  Integer status;
}
