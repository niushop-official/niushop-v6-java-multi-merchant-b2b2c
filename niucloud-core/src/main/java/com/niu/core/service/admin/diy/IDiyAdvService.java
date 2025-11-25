package com.niu.core.service.admin.diy;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.diy.param.DiyAdvParam;
import com.niu.core.service.admin.diy.param.DiyAdvSearchParam;
import com.niu.core.service.admin.diy.param.DiyAdvSortParam;
import com.niu.core.service.admin.diy.vo.DiyAdvInfoVo;
import com.niu.core.service.admin.diy.vo.DiyAdvListVo;

import java.util.List;

/**
 * 广告服务接口
 */
public interface IDiyAdvService {

    /**
     * 广告列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WebAdvListVo>
     */
    PageResult<DiyAdvListVo> page(PageParam pageParam, DiyAdvSearchParam searchParam);

    /**
     * 广告列表（不分页）
     * @param searchParam
     * @return
     */
    List<DiyAdvListVo> list(DiyAdvSearchParam searchParam);

    /**
     * 广告详情
     * @param id 主键ID
     * @return  WebAdvInfoVo
     */
    DiyAdvInfoVo info(Integer id);

    /**
     * 广告添加
     * @param addParam 添加参数
     */
    void add(DiyAdvParam addParam);

    /**
     * 广告编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DiyAdvParam editParam);

    /**
     * 广告删除
     * @param id 主键ID
     */
    void del(Integer id);

    void editSort(DiyAdvSortParam sortParam);

}
