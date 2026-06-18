package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberImportAddParam;
import com.niu.core.service.admin.member.param.MemberImportSearchParam;
import com.niu.core.service.admin.member.vo.MemberImportListVo;

public interface IMemberImportService {

    PageResult<MemberImportListVo> getPage(PageParam pageParam, MemberImportSearchParam searchParam);

    Integer memberImport(MemberImportAddParam.MemberImportData addParam);
}
