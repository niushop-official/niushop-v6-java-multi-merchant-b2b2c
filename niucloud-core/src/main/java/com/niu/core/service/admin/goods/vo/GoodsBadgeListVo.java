package com.niu.core.service.admin.goods.vo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标列表视图
 */
@Data
public class GoodsBadgeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer badgeId;
    private Integer siteId;
    private String badgeName;
    private Integer groupId;
    private String styleType;
    private String icon;
    private String memo;
    private Integer sort;
    private Integer status;
    private String colorJson;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    public JSONObject getColorJson() {
        if (StrUtil.isBlank(this.colorJson)) {
            return new JSONObject();
        }
        return new JSONObject(this.colorJson);
    }
}
