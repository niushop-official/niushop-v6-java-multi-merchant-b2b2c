package com.niu.mall.service.api.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.web.vo.MallWebFloorListVo;

import java.util.List;

/**
 * 首页楼层服务接口
 */
public interface IMallWebFloorService {

    /**
     * 首页楼层列表
     * @return PageResult<ShopWebFloorListVo>
     */
    PageResult<MallWebFloorListVo> list(PageParam pageParam);


}
