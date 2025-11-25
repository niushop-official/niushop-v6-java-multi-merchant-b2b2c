package com.niu.core.service.admin.site;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.param.SiteFinanceLogParam;
import com.niu.core.service.admin.site.vo.SiteFinanceLogVo;
import java.util.Map;

/**
 * 站点财务日志服务接口
 */
public interface ISiteFinanceLogService {
    /**
     * 获取分页列表
     * @return 分页数据
     */
    PageResult<SiteFinanceLogVo> getPage(PageParam pageParam ,SiteFinanceLogParam siteFinanceLogParam);
}
