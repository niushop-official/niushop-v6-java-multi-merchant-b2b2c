package com.niu.core.controller.api.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.article.IArticleService;
import com.niu.core.service.api.article.param.ArticeSearchParam;
import com.niu.core.service.api.article.vo.ArticleInfoVo;
import com.niu.core.service.api.article.vo.ArticleListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Resource
    IArticleService articleService;



    @GetMapping("/all")
    public Result<List<ArticleListVo>> all(@Validated ArticeSearchParam searchParam) {
        List<ArticleListVo> list = articleService.list(searchParam);
        return Result.success(list);
    }

    @GetMapping("/hot")
    public Result<List<ArticleListVo>> hot(@RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        List<ArticleListVo> list = articleService.hot(limit);
        return Result.success(list);
    }


    @GetMapping("/{id}")
    public Result<ArticleInfoVo> info(@PathVariable("id") Integer id) {
        ArticleInfoVo info = articleService.info(id);
        return Result.success(info);
    }

    @GetMapping("/list")
    public Result<PageResult<ArticleListVo>> lists(@Validated PageParam pageParam, @Validated ArticeSearchParam searchParam) {
        return Result.success(articleService.page(pageParam, searchParam));
    }

    @PutMapping("/incVisit/{id}")
    public Result<Object> incVisit(@PathVariable("id") Integer id) {
        articleService.intVisit(id);
        return Result.success();
    }

}
