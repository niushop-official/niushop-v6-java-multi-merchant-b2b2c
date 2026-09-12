package com.niu.core.service.core.point_deduct;


import com.niu.core.entity.point_deduct.PointDeduct;

public interface IPointDeductService {

    /**
     * 设置站点积分抵扣配置
     */
    void setSitePointDeductConfig(PointDeduct param);

    /**
     * 获取站点积分抵扣配置
     */
    PointDeduct getSitePointDeductConfig(Integer siteId);

    /**
     * 设置平台积分抵扣配置
     */
    void setPointDeductConfig(PointDeduct param);

    /**
     * 获取平台积分抵扣配置
     */
    PointDeduct getPointDeductConfig();
}