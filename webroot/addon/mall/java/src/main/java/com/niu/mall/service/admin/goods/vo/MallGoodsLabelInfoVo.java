package com.niu.mall.service.admin.goods.vo;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsLabel视图
 */
@Data
public class MallGoodsLabelInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签ID
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    private String memo;  // 标签说明
    private Integer sort;  // 排序
    private String styleType;
    private String colorJson;
    private String icon;
    private Integer groupId;
    private Integer status;

    public JSONObject getColorJson() {
        if (this.colorJson.isEmpty()) return new JSONObject();
        else return new JSONObject(this.colorJson);
    }
}
