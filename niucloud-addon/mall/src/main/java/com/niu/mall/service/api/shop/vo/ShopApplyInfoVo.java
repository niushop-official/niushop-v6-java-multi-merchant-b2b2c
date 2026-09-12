package com.niu.mall.service.api.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.api.diy.vo.DiyFormRecordsDetailVo;
import lombok.Data;

@Data
public class ShopApplyInfoVo {
    private Integer applyId;
    private Integer memberId;
    private String siteName;
    private String userName;
    private String userMobile;
    private Integer categoryId;
    private Integer childCategoryId;
    private Integer circleId;
    private String businessLicense;
    private Integer status;
    private String statusName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;
    private String refuseReason;
    private Integer formRecordId;
    private DiyFormRecordsDetailVo formInfo;   // 表单详情
    private String memberName;
    private String categoryName;
    private String childCategoryName;
}
