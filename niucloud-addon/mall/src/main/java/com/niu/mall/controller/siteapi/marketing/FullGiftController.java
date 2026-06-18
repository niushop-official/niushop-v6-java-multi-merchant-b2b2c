package com.niu.mall.controller.siteapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.marketing.param.FullGiftParam;
import com.niu.mall.service.admin.marketing.param.FullGiftSearchParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftListVo;
import com.niu.mall.service.site.marketing.ISiteFullGiftService;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * 站点满赠活动控制器
 * <p>
 * 提供站点满赠活动的管理功能，包括活动的增删改查、状态设置、商品检测等
 */
@RestController
@RequestMapping("adminapi/mall/site/full_gift")
public class FullGiftController {

    @Resource
    private ISiteFullGiftService siteFullGiftService;

    /**
     * 获取站点满赠活动分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索条件（标题、状态、类型、商品类型、时间范围等）
     * @return Result<PageResult < FullGiftListVo>> 满赠活动列表
     */
    @GetMapping("/list")
    public Result<PageResult<FullGiftListVo>> list(@Validated PageParam pageParam,
                                                   @Validated FullGiftSearchParam searchParam,
                                                   @RequestParam(value = "create_time", required = false)
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime[] createTime) {
        if (createTime != null && createTime.length == 2) {
            searchParam.setStartTime(createTime[0].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
            searchParam.setEndTime(createTime[1].atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond());
        }
        PageResult<FullGiftListVo> list = siteFullGiftService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取站点满赠活动初始化数据
     * <p>
     * 返回满赠活动所需的枚举数据，包括状态、商品类型、规则类型、会员类型等
     *
     * @return Result<FullGiftInitVo> 初始化数据
     */
    @GetMapping("/init")
    public Result<FullGiftInitVo> init(@RequestParam(value = "id", required = false) Integer id) {
        FullGiftInitVo initVo = siteFullGiftService.init(id);
        return Result.success(initVo);
    }

    /**
     * 获取站点满赠活动详情
     *
     * @param id 满赠活动ID
     * @return Result<FullGiftInfoVo> 满赠活动详情信息
     */
    @GetMapping("/info/{id}")
    public Result<FullGiftInfoVo> info(@Validated @PathVariable("id") Integer id) {
        FullGiftInfoVo info = siteFullGiftService.info(id);
        return Result.success(info);
    }

    /**
     * 添加站点满赠活动
     *
     * @param addParam 满赠活动添加参数（标题、类型、状态、时间、商品类型、规则、会员类型等）
     * @return Result<Object> 操作结果
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody FullGiftParam addParam) {
        siteFullGiftService.add(addParam);
        return Result.success();
    }

    /**
     * 编辑站点满赠活动
     *
     * @param id          满赠活动ID
     * @param modifyParam 满赠活动修改参数
     * @return Result<Object> 操作结果
     */
    @PostMapping("/edit/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id,
                               @Validated @RequestBody FullGiftParam modifyParam) {
        siteFullGiftService.edit(id, modifyParam);
        return Result.success();
    }

    /**
     * 删除站点满赠活动
     *
     * @param id 满赠活动ID
     * @return Result<Object> 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@Validated @PathVariable("id") Integer id) {
        siteFullGiftService.del(id);
        return Result.success();
    }

    /**
     * 设置站点满赠活动状态
     *
     * @param id   满赠活动ID
     * @param body 状态值
     * @return Result<Object> 操作结果
     */
    @PutMapping("/setStatus/{id}")
    public Result<Object> setStatus(@Validated @PathVariable("id") Integer id,
                                    @Validated @RequestBody Map<String, String> body) {
        String status = body.get("status");
        siteFullGiftService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 检测商品是否可参与站点满赠活动
     * <p>
     * 检查指定商品是否已经参与了满赠活动，返回商品的可用状态
     *
     * @param params 包含goods_ids列表的参数
     * @return Result<Object> 检测结果，包含每个商品的可用状态
     */
    @PostMapping("/check")
    public Result<Object> check(@Validated @RequestBody FullGiftParam params) {
        Map<String, Object> result = siteFullGiftService.checkGoods(params);
        return Result.success(result);
    }

}