package com.niu.core.controller.api.diy;


import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.diy.param.DiyAdvSearchParam;
import com.niu.core.service.admin.diy.vo.DiyAdvListVo;
import com.niu.core.service.api.diy.IDiyAdvService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告表控制器
 */

@RestController
@RequestMapping("/api/diy")
public class DiyAdvController {

    @Resource
    IDiyAdvService diyAdvService;


    /**
     * 广告列表不分页
     * @param searchParam
     * @return
     */
    @GetMapping("/adv")
    public Result advList(@Validated DiyAdvSearchParam searchParam) {
        return Result.success(diyAdvService.advInfo(searchParam.getApKey()));
    }



}
