package com.niu.core.service.admin.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.recharge.param.*;
import com.niu.core.service.admin.stat.vo.SysStatRechargeVo;

public interface IRechargeBillService {

    /**
     *  账单总数统计
     * @param param
     * @param param
     * @return
     */
    SysStatRechargeVo getBillTotal(RechargeBillParam param);

    /**
     *  账单总数统计
     * @param param
     * @param param
     * @return
     */
    PageResult<SysStatRechargeVo> getBillList(PageParam pageParam,RechargeBillParam param);

}
