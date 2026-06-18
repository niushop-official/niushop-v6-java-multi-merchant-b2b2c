package com.niu.core.service.core.member;

import com.niu.core.service.admin.member.param.MemberImportAddParam;

public interface ICoreMemberImportService {

    Integer add(MemberImportAddParam.MemberImportData addParam);

    void execute(Integer id);
}
