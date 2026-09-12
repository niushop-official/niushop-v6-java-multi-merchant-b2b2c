package com.niu.core.service.admin.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.marketing.param.NewcomerGiftConfigParam;
import com.niu.core.service.admin.marketing.param.NewcomerGiftRecordsSearchParam;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftConfigVo;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftRecordsListVo;

public interface INewcomerGiftService {

    NewcomerGiftConfigVo getConfig();

    void setConfig(NewcomerGiftConfigParam param);

    PageResult<NewcomerGiftRecordsListVo> records(PageParam pageParam, NewcomerGiftRecordsSearchParam searchParam);

    Integer count();
}
