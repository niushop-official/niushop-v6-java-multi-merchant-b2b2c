package com.niu.core.controller.api.article;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.article.IArticleCategroyService;
import com.niu.core.service.admin.article.param.ArticleCategroyParam;
import com.niu.core.service.admin.article.param.ArticleCategroySearchParam;
import com.niu.core.service.admin.article.vo.ArticleCategroyInfoVo;
import com.niu.core.service.admin.article.vo.ArticleCategroyListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/article")
public class ArticleCategroyController {

    @Resource
    IArticleCategroyService articleCategroyService;


    /**
     * 文章栏目列表分页
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebAdvListVo>>
     */
    @GetMapping("/category")
    public Result<PageResult<ArticleCategroyListVo>> page(@Validated PageParam pageParam, @Validated ArticleCategroySearchParam searchParam) {
        PageResult<ArticleCategroyListVo> list = articleCategroyService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 文章栏目列表不分页
     * @param searchParam
     * @return
     */
    @GetMapping("/category/all")
    public Result<List<ArticleCategroyListVo>> list(@Validated ArticleCategroySearchParam searchParam) {
        List<ArticleCategroyListVo> list = articleCategroyService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 文章栏目详情
     *
     * @param id 主键ID
     * @return Result<WebAdvInfoVo>
     */
    @GetMapping("/category/{id}")
    public Result<ArticleCategroyInfoVo> info(@PathVariable("id") Integer id) {
        ArticleCategroyInfoVo info = articleCategroyService.info(id);
        return Result.success(info);
    }

    /**
     * 文章栏目添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/category")
    public Result<Object> add(@Validated @RequestBody ArticleCategroyParam addParam) {
        articleCategroyService.add(addParam);
        return Result.success();
    }

    /**
     * 文章栏目编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/category/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ArticleCategroyParam editParam) {
        articleCategroyService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 文章栏目删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/category/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        articleCategroyService.del(id);
        return Result.success();
    }
}
