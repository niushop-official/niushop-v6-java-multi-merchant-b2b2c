package com.niu.core.service.site.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.shop.ShopMemberIsFollowEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
* 店铺会员表列表
*/
@Data
public class SiteShopMemberListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 累计消费金额 */
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal comsumMoney;

        /** 消费次数 */
        private Integer comsumNum;

        /** 首次消费时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long firstConsumTime;

        /** 主键 */
        private Integer id;

        /** 是否关注 */
        private Integer isFollow;

        /** 最近一次消费时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long lastConsumTime;

        /** 会员id */
        private Integer memberId;

        /** 店铺会员标签 */
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonArraySerializer.class)
        private String shopMemberLabel;

        /** 站点id */
        private Integer siteId;

        private String memberNo; //会员编号
        private String mobile;// 手机号
        private String nickname;// 昵称
        private String username;// 用户名
        private String headimg;// 头像
        private String registerChannel;// 注册渠道
        private List<SiteShopMemberLabelAllListVo> shopMemberLabelArray;

        public String getRegisterChannelName() {
            return MemberRegisterChannelEnum.getNameByType(registerChannel);
        }
        public String getIsFollowName() {
            return ShopMemberIsFollowEnum.getNameByStatus(isFollow);
        }

}
