package com.niu.core.service.admin.member.vo;

import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.common.SexEnum;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.member.MemberRegisterTypeEnum;
import com.niu.core.enums.member.StatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class MemberRecommenderSelectVo {
    private Integer memberId;
    private String memberNo;
    private Integer pid;
    private Integer siteId;
    private String username;
    private String mobile;
    private String nickname;
    private String headimg;
    private Integer memberLevel;
    private String memberLevelName;   // 会员等级名称（关联）
    private String registerChannelName;
    private String registerTypeName;
    private String sexName;
    private String loginChannelName;
    private String loginTypeName;
    private String statusName;
    private Date createTime;
    // 其他字段按需添加...

    private String registerChannel;  // 注册来源
    private String loginChannel;  //
    private String registerType;  // 注册方式
    private String loginType;  // 当前登录的操作终端类型
    private Integer sex;  // 性别 0保密 1男 2女
    private Integer status;  // 用户状态  用户状态默认为1
    
    /**
     * 注册渠道名称
     *
     * @return
     */
    public String getRegisterChannelName() {
        return MemberRegisterChannelEnum.getNameByType(this.registerChannel);
    }

    /**
     * 登录渠道名称
     *
     * @return
     */
    public String getLoginChannelName() {
        return ChannelEnum.getNameByCode(this.loginChannel);
    }

    /**
     * 注册渠道名称
     *
     * @return
     */
    public String getRegisterTypeName() {
        return MemberRegisterTypeEnum.getNameByType(this.registerType);
    }

    /**
     * 登录方式名称
     *
     * @return
     */
    public String getLoginTypeName() {
        return MemberLoginTypeEnum.getNameByType(this.loginType);
    }

    /**
     * 获取性别
     *
     * @return
     */
    public String getSexName() {
        return SexEnum.getNameBySex(this.sex);
    }

    /**
     * 获取状态
     *
     * @return
     */
    public String getStatusName() {
        return StatusEnum.getNameByStatus(this.status);
    }
}