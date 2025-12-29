package com.niu.core.controller.siteapi.article;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.article.IArticleService;
import com.niu.core.service.site.article.param.ArticeSearchParam;
import com.niu.core.service.site.article.vo.ArticleInfoVo;
import com.niu.core.service.site.article.vo.ArticleListVo;
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
    @GetMapping("/site/article")
    public Result<PageResult<ArticleListVo>> page(@Validated PageParam pageParam, @Validated ArticeSearchParam searchParam) {
        PageResult<ArticleListVo> list = articleService.page(pageParam, searchParam);
        return Result.success(list);
    }


    /**
     * 文章详情
     *
     * @param id 主键ID
     * @return Result<WebAdvInfoVo>
     */
    @GetMapping("/site/article/{id}")
    public Result<ArticleInfoVo> info(@PathVariable("id") Integer id) {
        ArticleInfoVo info = articleService.info(id);
        return Result.success(info);
    }

}
