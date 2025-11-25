package com.niu.mall.service.admin.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.web.param.MallWebFloorParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSearchParam;
import com.niu.mall.service.admin.web.param.MallWebFloorSortParam;
import com.niu.mall.service.admin.web.vo.MallWebFloorInfoVo;
import com.niu.mall.service.admin.web.vo.MallWebFloorListVo;

/**
 * 首页楼层服务接口
 */
public interface IMallWebFloorService {

    /**
     * 首页楼层列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopWebFloorListVo>
     */
    PageResult<MallWebFloorListVo> page(PageParam pageParam, MallWebFloorSearchParam searchParam);

    /**
     * 首页楼层详情
     * @param id 主键ID
     * @return  ShopWebFloorInfoVo
     */
    MallWebFloorInfoVo info(Integer id);

    /**
     * 首页楼层添加
     * @param addParam 添加参数
     */
    Integer add(MallWebFloorParam addParam);

    /**
     * 首页楼层编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallWebFloorParam editParam);

    /**
     * 首页楼层删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改排序
     * @param sortParam
     */
    void editSort(MallWebFloorSortParam sortParam);


}
