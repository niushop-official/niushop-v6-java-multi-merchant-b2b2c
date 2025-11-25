package com.niu.core.service.site.shop.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * MemberLabel视图
 */
@Data
public class SiteShopMemberLabelInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签id
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    private String memo;  // 备注
    private Integer sort;  // 排序

}
