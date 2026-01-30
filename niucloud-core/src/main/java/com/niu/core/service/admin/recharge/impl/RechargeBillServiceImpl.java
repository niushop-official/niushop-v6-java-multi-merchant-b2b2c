package com.niu.core.service.admin.recharge.impl;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.recharge.IRechargeBillService;
import com.niu.core.service.admin.recharge.param.RechargeBillParam;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.admin.stat.vo.SysStatRechargeVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class RechargeBillServiceImpl implements IRechargeBillService {

    @Resource
    private IStatService statService;


    @Override
    public SysStatRechargeVo getBillTotal(RechargeBillParam param) {
        // 处理日期参数
        String startDate = "";
        String endDate = "";
        if (param.getDate() != null && param.getDate().length > 0) {
            startDate = param.getDate()[0];
            if (param.getDate().length > 1) {
                endDate = param.getDate()[1];
            }
        }
        return statService.getRechargeStat(RequestUtils.defaultSiteId(), startDate, endDate);
    }

    @Override
    public PageResult<SysStatRechargeVo> getBillList(PageParam pageParam, RechargeBillParam param) {
        // 处理日期参数
        String startDate = "";
        String endDate = "";
        if (param.getDate() != null && param.getDate().length > 0) {
            startDate = param.getDate()[0];
            if (param.getDate().length > 1) {
                endDate = param.getDate()[1];
            }
        }
        return statService.getRechargeStatList(pageParam,RequestUtils.defaultSiteId(), startDate, endDate, param.getType());
    }
}
