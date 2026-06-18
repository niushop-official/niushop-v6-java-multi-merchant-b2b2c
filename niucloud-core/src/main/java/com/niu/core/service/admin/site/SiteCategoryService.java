package com.niu.core.service.admin.site;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.service.admin.site.vo.SiteCategoryInfoVo;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;

import java.util.List;

/**
 * 行业类目服务接口
 */
public interface SiteCategoryService {

    /**
     * 获取行业类目列表（分页）
     *
     * @param pageParam 分页参数
     * @param name      分类名称
     * @param addon     归属应用
     * @return 分页结果
     */
    PageResult<SiteCategoryListVo> page(PageParam pageParam, String name, String addon);

    /**
     * 获取顶级行业类目列表
     *
     * @param addon 归属应用
     * @return 类目列表
     */
    List<SiteCategoryListVo> topList(String addon);

    /**
     * 获取子级行业类目列表
     *
     * @param pid 父级ID
     * @return 类目列表
     */
    List<SiteCategoryListVo> childList(Integer pid);

    /**
     * 获取行业类目详情
     *
     * @param id 类目ID
     * @return 类目详情
     */
    SiteCategoryInfoVo info(Integer id);

    /**
     * 添加行业类目
     *
     * @param siteCategory 类目数据
     */
    void add(SiteCategory siteCategory);

    /**
     * 编辑行业类目
     *
     * @param id           类目ID
     * @param siteCategory 类目数据
     */
    void edit(Integer id, SiteCategory siteCategory);

    /**
     * 删除行业类目
     *
     * @param id 类目ID
     */
    void del(Integer id);

    /**
     * 获取行业类目树
     *
     * @param name  分类名称
     * @param addon 归属应用
     * @return 树形结构列表
     */
    List<SiteCategoryListVo> tree(String name, String addon);

    /**
     * 更新类目排序
     *
     * @param siteCategoryList 分类列表
     */
    void updateSort(List<SiteCategory> siteCategoryList);

    /**
     * 设置行业类目轮播图
     *
     * @param id         类目ID
     * @param bannerList 轮播图列表(JSON格式)
     */
    void setBanner(Integer id, String bannerList);
}