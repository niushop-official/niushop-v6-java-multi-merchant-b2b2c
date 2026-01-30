package com.niu.core.service.admin.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class WebConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isShowShop = 0;// 是否显示甄选好店模块
    private String shopAdImage = "static/resource/images/web/shop_01.png";// 店铺广告图片
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String shopAdLink = "";// 店铺广告图跳转链接
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String site_ids = "[]";// 选择展示的店铺id
    private Integer isShowTop = 1;// 是否显示顶部广告图
    private String topImage = "";// 顶部广告图
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String topLink = "";// 顶部广告图跳转链接
    private String closeType = "auto";//  关闭方式 auto:自动关闭 manual:手动关闭
    private Integer closeTime = 10;// 自动关闭时间 单位：秒
}
