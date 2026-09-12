package com.niu.core.controller.adminapi.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysPrinterService;
import com.niu.core.service.admin.sys.param.SysPrinterModifyStatusParam;
import com.niu.core.service.admin.sys.param.SysPrinterParam;
import com.niu.core.service.admin.sys.param.SysPrinterSearchParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 小票打印机控制器
 */

@RestController
@RequestMapping("adminapi/sys/printer")
public class SysPrinterController {

    @Resource
    ISysPrinterService sysPrinterService;

    /**
     * 小票打印机分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysPrinterListVo>>
     */
    @GetMapping
    public Result<?> page(@Validated PageParam pageParam, @Validated SysPrinterSearchParam searchParam) {
        return Result.success(sysPrinterService.page(pageParam, searchParam));
    }

    @GetMapping("/list")
    public Result<?> lists(@Validated SysPrinterSearchParam searchParam) {
        return Result.success(sysPrinterService.getList(searchParam));
    }

    /**
     * 获取当前站点可用打印机，必须在 /{id} 前声明，避免 usable 被解析为主键。
     */
    @GetMapping("/usable")
    public Result<?> usable() {
        return Result.success(sysPrinterService.getUsableList());
    }

    /**
     * 小票打印机详情
     *
     * @param id 主键ID
     * @return Result<SysPrinterInfoVo>
     */
    @GetMapping("/{id}")
    public Result<?> info(@PathVariable("id") Integer id) {
        return Result.success(sysPrinterService.info(id));
    }

    /**
     * 小票打印机添加

     * @param addParam 添加参数
     * @return Result<?>
     */
    @PostMapping
    public Result<?> add(@Validated @RequestBody SysPrinterParam addParam) {
        Integer id = sysPrinterService.add(addParam);
        return Result.success("ADD_SUCCESS", Map.of("id", id));
    }

    /**
     * 小票打印机编辑
     *
     * @param editParam 编辑参数
     * @return Result<?>
     */
    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody SysPrinterParam editParam) {
        sysPrinterService.edit(id, editParam);
        return Result.success();
    }

    @PutMapping("/status")
    public Result<?> modifyStatus(@Validated @RequestBody SysPrinterModifyStatusParam editParam) {
        sysPrinterService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 小票打印机删除
     *
     * @param id 参数
     * @return Result<?>
     */
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer id) {
        sysPrinterService.del(id);
        return Result.success();
    }

    @GetMapping("/type")
    public Result<?> getType() {
        return Result.success(sysPrinterService.getType());
    }

    /**
     * 获取小票打印机设备品牌
     * 对应 PHP: getBrand($data[ 'brand' ])
     * @param brand 品牌参数（可选）
     * @return
     */
    @GetMapping("/brand")
    public Result<?> getBrand(@RequestParam(value = "brand", required = false, defaultValue = "") String brand) {
        return Result.success(sysPrinterService.getBrand(brand));
    }

    @PutMapping("/refreshtoken/{id}")
    public Result<?> refreshToken(@PathVariable("id") Integer id) {
        sysPrinterService.refreshToken(id);
        return Result.success();
    }

    @PutMapping("/testprint/{id}")
    public Result<?> testPrint(@PathVariable("id") Integer id) {
        sysPrinterService.testPrint(id);
        return Result.success();
    }

    @PostMapping("/printticket")
    public Result<?> printTicket(@RequestBody SysPrinterPrintTicketParam param) {
        SysPrinterPrintTicketVo vo = sysPrinterService.printTicket(param);
        if (vo.getCode() == 0) {
            return Result.success();
        }

        return Result.fail(vo.getMessage());
    }

}
