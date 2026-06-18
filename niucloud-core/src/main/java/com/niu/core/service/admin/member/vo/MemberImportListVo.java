package com.niu.core.service.admin.member.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberImportListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer siteId;
    private Integer mainId;
    private Integer status;
    private String statusName;
    private Integer totalNum;
    private Integer successNum;
    private Integer failNum;
    private Integer repeatNum;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;
    private Integer duration;
    private String output;
    private String failOutput;
    private String failRemark;

    private SysUserVo user;

    @Data
    public static class SysUserVo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer uid;
        private String username;
        private String headImg;
        private String realName;
    }
}
