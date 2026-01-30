package com.niu.core.service.adminapp;

import com.niu.core.service.adminapp.vo.StatItemVo;

import java.util.List;

/**
 * 统计服务接口
 */
public interface IStatService {

    /**
     * 统计列表
     * @return List<StatItemVo>
     */
    List<StatItemVo> getStatList();

    /**
     * 全部统计列表
     * @return List<StatItemVo>
     */
    List<StatItemVo> getAllStatList();

    /**
     * 设置统计列表
     * @param param 参数
     * @return Boolean
     */
    Boolean setStatList(com.niu.core.service.adminapp.param.PstarEditParam param);
}
