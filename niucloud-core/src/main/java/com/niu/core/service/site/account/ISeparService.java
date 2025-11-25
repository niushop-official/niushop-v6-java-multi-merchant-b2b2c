package com.niu.core.service.site.account;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.account.param.SeparParam;
import com.niu.core.service.site.account.param.SeparSearchParam;
import com.niu.core.service.site.account.vo.SeparDetailVo;
import com.niu.core.service.site.account.vo.SeparInfoVo;
import com.niu.core.service.site.account.vo.SeparListVo;

import java.awt.*;
import java.util.List;

/**
 * 服务接口
 */
public interface ISeparService {

    /**
     * 列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SeparListVo>
     */
    PageResult<SeparListVo> page(PageParam pageParam, SeparSearchParam searchParam);

    /**
     * 详情
     * @param id 主键ID
     * @return  SeparInfoVo
     */
    List<SeparDetailVo> detail(Integer id);


}
