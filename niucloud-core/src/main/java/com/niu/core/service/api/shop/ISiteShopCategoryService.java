package com.niu.core.service.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.shop.param.SiteShopCategoryParam;
import com.niu.core.service.api.shop.param.SiteShopCategorySearchParam;
import com.niu.core.service.api.shop.vo.SiteShopCategoryInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopCategoryListVo;

import java.util.List;

/**
 * 站点分类服务接口
 */
public interface ISiteShopCategoryService {

    /**
     * 站点分类列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteShopCategoryListVo>
     */
    PageResult<SiteShopCategoryListVo> page(PageParam pageParam, SiteShopCategorySearchParam searchParam);

    List<SiteShopCategoryListVo> list(SiteShopCategorySearchParam searchParam);

    List<SiteShopCategoryListVo> all();
    /**
     * 站点分类详情
     * @param id 主键ID
     * @return  SiteShopCategoryInfoVo
     */
    SiteShopCategoryInfoVo info(Integer id);

    /**
     * 站点分类添加
     * @param addParam 添加参数
     */
    void add(SiteShopCategoryParam addParam);

    /**
     * 站点分类编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteShopCategoryParam editParam);

    /**
     * 站点分类删除
     * @param id 主键ID
     */
    void del(Integer id);

}
