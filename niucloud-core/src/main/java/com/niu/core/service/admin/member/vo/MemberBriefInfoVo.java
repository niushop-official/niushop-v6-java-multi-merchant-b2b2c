package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * Member视图
 */
@Data
public class MemberBriefInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;  // 主键
    private String memberNo;  // 会员编码
    private String username;  // 会员用户名
    private String mobile;  // 手机号
    private String nickname;  // 会员昵称
    private String headimg;  // 会员头像

    /* 26-04-01添加 订单列表返回会员信息时额外返回会员积分相关信息 */
    /** 可用积分 */
    private Integer point;
    /** 累计获取积分 */
    private Integer pointGet;
}
