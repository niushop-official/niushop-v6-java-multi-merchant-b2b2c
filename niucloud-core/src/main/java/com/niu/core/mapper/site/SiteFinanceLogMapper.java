package com.niu.core.mapper.site;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.site.SiteAccountLog;
import com.niu.core.entity.site.SiteFinanceLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点账单记录 Mapper
 */
@Mapper
public interface SiteFinanceLogMapper extends MPJBaseMapper<SiteFinanceLog> {
}
