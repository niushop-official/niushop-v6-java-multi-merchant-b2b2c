package com.niu.core.service.basic;

import com.github.pagehelper.PageInfo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.basic.param.SiteInvoiceParam;
import com.niu.core.service.basic.param.SiteInvoiceSubmitParam;
import com.niu.core.service.basic.param.SiteInvoiceConfigParam;
import com.niu.core.service.basic.vo.SiteInvoiceListVo;
import com.niu.core.service.basic.vo.SiteInvoiceConfigVo;
import java.util.Map;

/**
 * 发票服务接口
 */
public interface ISiteInvoiceService {
    
    /**
     * 获取发票分页列表
     * @param param 查询参数
     * @return PageInfo<SiteInvoiceListVo>
     */
    PageResult<SiteInvoiceListVo> getPage(PageParam pageParam, SiteInvoiceParam param);

    /**
     * 获取发票详情
     * @param id 发票ID
     * @return 发票详情
     */
    SiteInvoiceListVo getInfo(Integer id);

    /**
     * 开具发票
     * @param id 发票ID
     * @param param 开具发票参数
     */
    void invoicing(Integer id, SiteInvoiceSubmitParam param);

    /**
     * 获取发票配置
     * @return 发票配置信息
     */
    SiteInvoiceConfigVo getConfig();
    
    /**
     * 设置发票配置
     * @param param 发票配置参数
     * @return 是否设置成功
     */
    boolean setConfig(SiteInvoiceConfigParam param);
}
