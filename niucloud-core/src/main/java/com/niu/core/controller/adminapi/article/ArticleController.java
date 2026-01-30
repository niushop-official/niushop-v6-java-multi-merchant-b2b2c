package com.niu.core.controller.adminapi.article;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.article.IArticleService;
import com.niu.core.service.admin.article.param.ArticeSearchParam;
import com.niu.core.service.admin.article.param.ArticleParam;
import com.niu.core.service.admin.article.vo.ArticleInfoVo;
import com.niu.core.service.admin.article.vo.ArticleListVo;
import com.niu.core.service.admin.article.vo.ArticlePageVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminapi/article")
@SaCheckLogin
public class ArticleController {

    @Resource
    IArticleService articleService;


    /**
     * 文章列表分页
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebAdvListVo>>
     */
    @GetMapping("/article")
    public Result<PageResult<ArticleListVo>> page(@Validated PageParam pageParam, @Validated ArticeSearchParam searchParam) {
        PageResult<ArticleListVo> list = articleService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 文章列表不分页
     * @param searchParam
     * @return
     */
    @GetMapping("/list")
    public Result<List<ArticleListVo>> list(@Validated ArticeSearchParam searchParam) {
        List<ArticleListVo> list = articleService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 文章详情
     *
     * @param id 主键ID
     * @return Result<WebAdvInfoVo>
     */
    @GetMapping("/article/{id}")
    public Result<ArticleInfoVo> info(@PathVariable("id") Integer id) {
        ArticleInfoVo info = articleService.info(id);
        return Result.success(info);
    }

    /**
     * 文章添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/article")
    public Result<Object> add(@Validated @RequestBody ArticleParam addParam) {
        articleService.add(addParam);
        return Result.success();
    }

    /**
     * 文章编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/article/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ArticleParam editParam) {
        articleService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 文章删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/article/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        articleService.del(id);
        return Result.success();
    }

    /**
     * 获取文章选择列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WebAdvListVo>>
     */
    @GetMapping("/article/select")
    public Result<PageResult<ArticlePageVo>> pageList(@Validated PageParam pageParam, @Validated ArticeSearchParam searchParam) {
        PageResult<ArticlePageVo> list = articleService.pageList(pageParam, searchParam);
        return Result.success(list);
    }

}
