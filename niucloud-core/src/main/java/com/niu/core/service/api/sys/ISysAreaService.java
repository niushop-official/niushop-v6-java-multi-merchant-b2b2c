package com.niu.core.service.api.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.sys.SysArea;

import java.util.Collection;
import java.util.List;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import com.niu.core.service.api.sys.vo.SysAreaLevelVo;
import com.niu.core.service.api.sys.vo.SysAreaListVo;

public interface ISysAreaService {

    List<SysArea> getAreaListByIds(Collection<Integer> ids);

    /**
     * @param id
     * @return
     */
    SysArea getAreaById(Integer id);

    /**
     * 根据PID返回区域列表
     *
     * @param pid
     * @return
     */
    List<SysAreaListVo> getListByPid(Integer pid);

    /**
     * 根据级别返回城市
     *
     * @param level
     * @return
     */
    List<SysAreaLevelVo> getAreaTree(Integer level);

    /**
     * @param code
     * @return
     */
    JSONObject getAreaByAreaCode(Integer code);

    /**
     * @param param
     * @return
     */
    JSONObject getAddressByLatlng(SysAreaAddressByLatlngParam param);

    /**
     * 通过客户端 IP 获取经纬度（对齐 PHP AreaService::getLatlngByIp）
     * 定位失败时的降级方案，返回 lat、lng
     *
     * @return 经纬度对象，包含 lat、lng 字段
     */
    JSONObject getLatlngByIp();

}
