package com.niu.mall.service.site.stat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
@Data
public class ShopStatVo {

    @JsonProperty("category_name")
    private categoryName categoryName = new categoryName();

    @Data
    public static class categoryName {
        private Integer categoryId;//站点分类ID
        private String categoryName;//站点分类名称

    }

    private Long createTime;//创建时间

    private Long expireTime;//到期时间（如果是0 无限期）

    private Integer groupId;//分组ID(0:不限制)

    private String groupName;//分组名称

    private Integer isSelf;//是否自营

    private Integer siteId;// 主键

    private String siteName;// 站点名称

    private Integer status;// 状态 1-正常 0-体验期 2-已到期

    private String statusName;

}
