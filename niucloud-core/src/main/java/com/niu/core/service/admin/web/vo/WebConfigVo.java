package com.niu.core.service.admin.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class WebConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isShowShop;// 是否显示甄选好店模块
    private String shopAdImage;// 店铺广告图片
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String site_ids;// 选择展示的店铺id


}
