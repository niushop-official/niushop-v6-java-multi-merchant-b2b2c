package com.niu.core.mapper.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.site.SiteApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户入驻申请 Mapper 接口
 * 继承 MPJBaseMapper 支持 selectJoinPage/selectJoinOne
 */
@Mapper
public interface SiteApplyMapper extends MPJBaseMapper<SiteApply> {

}
