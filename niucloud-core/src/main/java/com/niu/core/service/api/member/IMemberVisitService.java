package com.niu.core.service.api.member;

import com.niu.core.service.api.member.param.MemberVisitParam;

public interface IMemberVisitService {

    /**
     * 会员信息
     *
     * @param param
     */
    void addVisit(MemberVisitParam param);

}
