package com.niu.core.service.admin.verify.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;

/**
 * Verifier视图
 */
@Data
public class VerifierDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer uid;  // 用户id
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String verifyType;  // 核销类型
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;  // 核销类型

    private String memberNo;  // 会员编码
    private String username;  // 会员用户名
    private String mobile;  // 手机号
    private String nickname;  // 会员昵称
    private String headimg;  // 会员头像

    private MemberBriefInfoVo member; // 会员信息
}