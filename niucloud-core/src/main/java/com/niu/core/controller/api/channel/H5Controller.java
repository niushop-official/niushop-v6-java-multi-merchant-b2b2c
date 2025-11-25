package com.niu.core.controller.api.channel;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.channel.IH5Service;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channel/h5")
@Slf4j
public class H5Controller {

    @Resource
    private IH5Service h5Service;

    /**
     * H5配置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<?> config() {
        return Result.success(h5Service.getH5());
    }


}
