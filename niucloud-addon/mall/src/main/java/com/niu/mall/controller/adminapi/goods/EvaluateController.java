package com.niu.mall.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.param.MallGoodsEvaluateParam;
import com.niu.mall.service.admin.goods.param.MallGoodsEvaluateReplyParam;
import com.niu.mall.service.admin.goods.param.MallGoodsEvaluateSearchParam;
import com.niu.mall.service.admin.goods.IMallGoodsEvaluateService;
import com.niu.mall.service.admin.goods.vo.MallGoodsEvaluateInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsEvaluateListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 商品评价控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/goods/evaluate")
public class EvaluateController {

    @Resource
    IMallGoodsEvaluateService mallGoodsEvaluateService;

    /**
     * 商品评价列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallGoodsEvaluateListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallGoodsEvaluateListVo>> list(@Validated PageParam pageParam,
                                                            @Validated MallGoodsEvaluateSearchParam searchParam) {
        PageResult<MallGoodsEvaluateListVo> list = mallGoodsEvaluateService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品评价详情
     * @param id 主键ID
     * @return Result<MallGoodsEvaluateInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MallGoodsEvaluateInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallGoodsEvaluateInfoVo info = mallGoodsEvaluateService.info(id);
        return Result.success(info);
    }

    /**
     * 商品评价添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallGoodsEvaluateParam addParam) {
        mallGoodsEvaluateService.add(addParam);
        return Result.success();
    }


    /**
     * 商品评价删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        mallGoodsEvaluateService.del(id);
        return Result.success();
    }

    /**
     * 审核通过
     * @param id
     * @return
     */
    @PutMapping("adopt/{id}")
    public Result<Object> adopt(@PathVariable("id") Integer id) {
        mallGoodsEvaluateService.auditAdopt(id);
        return Result.success();
    }

    /**
     * 审核拒绝
     * @param id
     * @return
     */
    @PutMapping("refuse/{id}")
    public Result<Object> refuse(@PathVariable("id") Integer id) {
        mallGoodsEvaluateService.auditRefuse(id);
        return Result.success();
    }

    /**
     * 评价回复
     * @param id
     * @param replyParam
     * @return
     */
    @PutMapping("reply/{id}")
    public Result<Object> evaluateReply(@PathVariable("id") Integer id, @RequestBody MallGoodsEvaluateReplyParam replyParam){
        mallGoodsEvaluateService.reply(id, replyParam.getExplainFirst());
        return Result.success();
    }

    /**
     * 审核置顶
     * @param id
     * @return
     */
    @PutMapping("topping/{id}")
    public Result<Object> topping(@PathVariable("id") Integer id) {
        mallGoodsEvaluateService.topping(id);
        return Result.success();
    }

    /**
     * 审核置顶取消
     * @param id
     * @return
     */
    @PutMapping("cancel_topping/{id}")
    public Result<Object> cancelTopping(@PathVariable("id") Integer id) {
        mallGoodsEvaluateService.cancelTopping(id);
        return Result.success();
    }


}
