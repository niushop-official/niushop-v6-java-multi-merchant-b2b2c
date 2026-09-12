package com.niu.mall.controller.siteapi;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.StringUtils;
import com.niu.mall.service.api.order.IOrderService;
import com.niu.mall.service.site.order.IMallOrderService;
import com.niu.mall.service.site.stat.IMallStatService;
import com.niu.mall.service.site.stat.vo.StatArticleInfoVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 【请填写功能名称】控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/stat")
public class MallStatController {

    @Resource
    IMallStatService iMallStatService;

    @Resource
    IMallOrderService orderService;

    /**
     *
     * @return
     */
    @GetMapping("")
    public Result stat() {
        return Result.success(iMallStatService.getStat());
    }

    /**
     *  根据时间戳获取数据统计
     * @return
     */
    @GetMapping("/total")
    public Result total(@RequestParam(value = "date",required = false) String date) {
        if(StringUtils.isEmpty(date)){
            date = "today";
        }
        Long startTime = 0L;
        Long endTime = 0L;
        
        LocalDate today = LocalDate.now();
        ZoneId zoneId = ZoneId.systemDefault();
        
        if(date.equals("today")){
  
            startTime = today.atStartOfDay(zoneId).toInstant().toEpochMilli() / 1000;
            endTime = 0L;
        }else if(date.equals("7days")){

            LocalDate startDate = today.minusDays(7);
            startTime = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli() / 1000;
            endTime = today.atStartOfDay(zoneId).toInstant().toEpochMilli() / 1000;
        }else if(date.equals("30days")){
            LocalDate startDate = today.minusDays(30);
            startTime = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli() / 1000;
            endTime = today.atStartOfDay(zoneId).toInstant().toEpochMilli() / 1000;
        }
        return Result.success(iMallStatService.getStatDate(startTime,endTime));
    }

    /**
     *获取代办事项
     * @return
     */
    @GetMapping("/todo")
    public Result todo()  {
        return Result.success(orderService.getOrderCount());
    }

    /**
     *获取使用指南
     * @return
     */
    @GetMapping("/suggestion")
    public Result suggestion() {
        return Result.success(iMallStatService.getSuggestionList());
    }

    /**
     *获取商城分销插件
     * @return
     */
    @GetMapping("/addon")
    public Result addon() {
        return Result.success(iMallStatService.getAddonList());
    }

    /**
     *  获取公告通知
     * @return
     */
    @GetMapping("/article")
    public Result article() {
        return Result.success(iMallStatService.getArticleList());
    }

    /**
     * 获取资讯详情
     */
    @GetMapping("/article/{id}")
    public Result<StatArticleInfoVo> articleInfo(@PathVariable("id") Integer id) {
        return Result.success(iMallStatService.articleInfo(id));
    }

    /**
     * 获取店铺信息
     * @return
     */
    @GetMapping("/shop")
    public Result shop() {
        return Result.success(iMallStatService.getShopInfo());
    }

    /**
     * 获取交易概况
     * @return
     */
    @GetMapping("/sale")
    public Result sale(@RequestParam(value = "date")String date) {
        String startDate = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -6));
        String endDate = DateUtil.formatDate(new Date());
        switch ( date){
            case "7days":
                break;
            case "30days":
                startDate = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -30));
                endDate = DateUtil.formatDate(new Date());
                break;
        }
        return Result.success(iMallStatService.getSaleList(startDate,endDate));
    }

    /**
     * 获取商品排行榜
     * @return
     */
    @GetMapping("/rank")
    public Result rank(@RequestParam(value = "date") String date,
                       @RequestParam(value = "order", required = false) String order,
                       @RequestParam(value = "sort", required = false) String sort) {
        Long startTime = 0L;
        Long endTime = 0L;
        if (date.equals("today")) {
            startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), 0)).getTime() / 1000;
        } else if (date.equals("7days")) {
            startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), -6)).getTime() / 1000;
            endTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), 1)).getTime() / 1000;
        } else if (date.equals("30days")) {
            startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), -29)).getTime() / 1000;
            endTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), 1)).getTime() / 1000;
        }
        return Result.success(iMallStatService.getGoodsRank(startTime, endTime, order, sort));
    }
}
