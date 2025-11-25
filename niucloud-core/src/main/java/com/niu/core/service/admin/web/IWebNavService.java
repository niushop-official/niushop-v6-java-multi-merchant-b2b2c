package com.niu.core.service.admin.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.web.param.WebNavParam;
import com.niu.core.service.admin.web.param.WebNavSearchParam;
import com.niu.core.service.admin.web.param.WebNavSortParam;
import com.niu.core.service.admin.web.vo.WebNavInfoVo;
import com.niu.core.service.admin.web.vo.WebNavListVo;

import java.util.List;

/**
 * PC导航管理服务接口
 */
public interface IWebNavService {

    /**
     * PC导航管理列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WebNavListVo>
     */
    PageResult<WebNavListVo> page(PageParam pageParam, WebNavSearchParam searchParam);

    /**
     * 导航列表（不分页）
     * @param searchParam
     * @return
     */
    List<WebNavListVo> list(WebNavSearchParam searchParam);

    /**
     * PC导航管理详情
     * @param id 主键ID
     * @return  WebNavInfoVo
     */
    WebNavInfoVo info(Integer id);

    /**
     * PC导航管理添加
     * @param addParam 添加参数
     */
    void add(WebNavParam addParam);

    /**
     * PC导航管理编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, WebNavParam editParam);

    /**
     * PC导航管理删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改排序号
     * @param sortParam
     */
    void editSort(WebNavSortParam sortParam);


}
