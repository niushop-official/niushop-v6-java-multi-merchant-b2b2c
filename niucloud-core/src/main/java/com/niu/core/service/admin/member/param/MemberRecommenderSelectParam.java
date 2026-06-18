package com.niu.core.service.admin.member.param;

import lombok.Data;

@Data
public class MemberRecommenderSelectParam {
    private String keyword;      // 搜索关键词
    private Long memberId;       // 当前会员ID，用于排除自身
}