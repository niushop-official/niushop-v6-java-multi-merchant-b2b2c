package com.niu.core.service.core.site.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;  // 主键
    private String siteName;  // 站点名称
    private Integer groupId; // 分组ID(0:不限制)
    private String groupName; //分组名称
    private String appType; // 站点类型
    private String addons; // 站点包含的插件
    private Integer status;  // 状态 1-正常 0-体验期 2-已到期
    private String statusName;  // 状态名称
    private Long createTime;  // 创建时间
    private Long expireTime;  // 到期时间（如果是0 无限期）
    
    // shopInfo关联字段
    private Integer isSelf;  // 是否自营
    private String categoryName;  // 分类名称
    private Integer businessStatus;  // 营业状态

    public String getExpireTimeText() {
        if (expireTime != null && expireTime.equals(0L)) {
            return "无限期";
        }
        return expireTime != null ? expireTime.toString() : null;
    }

    public boolean isUnlimited() {
        return expireTime != null && expireTime.equals(0L);
    }
}
