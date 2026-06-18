package com.niu.core.service.core.member;

import com.niu.core.entity.member.Member;

/**
 * 会员账户流水服务层
 */
public interface ICoreMemberAccountService {

    void addLog(Integer siteId, Integer memberId, String accountType, Double accountData, String fromType, String memo, String relatedId);

    Member getMemberAccountInfo(Integer memberId);
}
