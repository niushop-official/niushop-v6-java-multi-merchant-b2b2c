package com.niu.core.service.core.site_payment;

import com.niu.core.service.core.site_payment.vo.SitePaymentApplyInfoVo;

import java.util.Map;

/**
 * 分账账户服务层
 */
public interface ICoreSitePaymenApplyService {

    /**
     * 获取站点支付申请信息
     * @param siteId 站点ID
     * @param type 支付类型，默认为 'huifu'
     * @return 支付申请信息（包含关联的支付信息）
     */
    SitePaymentApplyInfoVo getInfo(Integer siteId, String type);

    /**
     * 更新账户
     * @param id 主键ID
     * @param siteId 站点ID
     * @param data 更新数据
     * @return 是否成功
     */
    Boolean edit(Long id, Integer siteId, Map<String, Object> data);

    /**
     * 添加站点支付申请
     * @param data 申请数据
     * @param siteId 站点ID
     * @param type 支付类型
     * @return 创建的记录ID
     */
    Long add(Map<String, Object> data, Integer siteId, String type);

    /**
     * 审核通过
     * @param siteId 站点ID
     * @param id 主键ID
     * @param extendNo 扩展编号（huifu_id）
     * @return 是否成功
     */
    Boolean agree(Integer siteId, Long id, String extendNo);

}
