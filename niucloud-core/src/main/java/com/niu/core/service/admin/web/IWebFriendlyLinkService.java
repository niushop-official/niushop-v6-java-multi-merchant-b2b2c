package com.niu.core.service.admin.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.web.param.WebFriendlyLinkParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSearchParam;
import com.niu.core.service.admin.web.param.WebFriendlyLinkSortParam;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkInfoVo;
import com.niu.core.service.admin.web.vo.WebFriendlyLinkListVo;

import java.util.List;

/**
 * 友情链接服务接口
 */
public interface IWebFriendlyLinkService {

    /**
     * 友情链接列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WebFriendlyLinkListVo>
     */
    PageResult<WebFriendlyLinkListVo> page(PageParam pageParam, WebFriendlyLinkSearchParam searchParam);

    List<WebFriendlyLinkListVo> list(WebFriendlyLinkSearchParam searchParam);

    /**
     * 友情链接详情
     * @param id 主键ID
     * @return  WebFriendlyLinkInfoVo
     */
    WebFriendlyLinkInfoVo info(Integer id);

    /**
     * 友情链接添加
     * @param addParam 添加参数
     */
    void add(WebFriendlyLinkParam addParam);

    /**
     * 友情链接编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, WebFriendlyLinkParam editParam);

    /**
     * 友情链接删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改排序
     * @param sortParam
     */
    void editSort(WebFriendlyLinkSortParam sortParam);

}
