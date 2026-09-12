package com.niu.core.service.admin.marketing.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 新人礼记录列表
 */
@Data
public class NewcomerGiftRecordsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer recordId;
    private Integer memberId;
    private String memberNo;
    private String nickname;
    private String headimg;
    private String gifts;
    private JSONObject giftsContent = new JSONObject();

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
}
