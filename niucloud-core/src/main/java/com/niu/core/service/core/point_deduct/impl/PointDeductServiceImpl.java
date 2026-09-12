package com.niu.core.service.core.point_deduct.impl;

import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.service.core.point_deduct.IPointDeductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointDeductServiceImpl implements IPointDeductService {

    @Autowired
    private ICorePointDeductService corePointDeductService;

    // ==================== Site端 ====================

    @Override
    public void setSitePointDeductConfig(PointDeduct param) {
        corePointDeductService.setSitePointDeductConfig(param);
    }

    @Override
    public PointDeduct getSitePointDeductConfig(Integer siteId) {
        return corePointDeductService.getSitePointDeductConfig(siteId);
    }

    // ==================== Admin端 ====================

    @Override
    public void setPointDeductConfig(PointDeduct param) {
        param.setDesc("每1积分抵扣" + param.getDeductAmount() + "元");
        corePointDeductService.setPointDeductConfig(param);
    }

    @Override
    public PointDeduct getPointDeductConfig() {
        return corePointDeductService.getPointDeductConfig();
    }
}