package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ISysAreaService;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/area")
@Slf4j
public class SysAreaController {

    @Autowired
    private ISysAreaService sysAreaService;

    /**
     * @param pid
     * @return
     */
    @GetMapping("/list_by_pid/{pid}")
    public Result<?> getListByPid(@PathVariable("pid") Integer pid) {
        return Result.success(sysAreaService.getListByPid(pid));
    }

    @GetMapping("/tree/{level}")
    public Result<?> tree(@PathVariable("level") Integer level) {
        return Result.success(sysAreaService.getAreaTree(level));
    }

    @GetMapping("/code/{code}")
    public Result<?> code(@PathVariable("code") Integer code) {
        return Result.success(sysAreaService.getAreaByAreaCode(code));
    }

    /**
     * 通过经纬度查询地址
     *
     * @param param
     * @return
     */
    @GetMapping("/address_by_latlng")
    public Result<?> getAddressByLatlng(SysAreaAddressByLatlngParam param) {
        return Result.success(sysAreaService.getAddressByLatlng(param));
    }

    /**
     * 通过客户端 IP 获取经纬度（定位失败降级方案，对齐 PHP area/latlng_by_ip）
     *
     * @return 经纬度对象
     */
    @GetMapping("/latlng_by_ip")
    public Result<?> getLatlngByIp() {
        return Result.success(sysAreaService.getLatlngByIp());
    }

}
