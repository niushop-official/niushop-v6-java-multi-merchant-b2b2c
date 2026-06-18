package com.niu.core.service.core.site_payment;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.shop.param.SitePaymentAEParam;
import com.niu.core.service.admin.shop.param.SitePaymentSearchParam;
import com.niu.core.service.core.site_payment.vo.SitePaymentListVo;
import com.niu.core.service.core.site_payment.vo.SitePaymentVo;

import java.util.List;
import java.util.Map;

/**
 * 分账账户服务层
 */
public interface ICoreSitePaymentService {

    /**
     * 获取平台分账账户列表
     */
    JSONArray getSpearPaymentType(Integer siteId);

    /**
     * 获取店铺分账账户列表
     */
    JSONArray getSiteSpearPaymentTypeListByType(Integer siteId);

    /**
     * 获取支付详情
     *
     * @param isSettle 是否结算
     * @return 支付详情信息
     */
    List<SitePaymentListVo> getList(Integer isSettle);

    /**
     * 获取支付详情
     *
     * @param siteId 站点ID
     * @param type   支付类型
     * @return 支付详情信息
     */
    Map<String, Object> getInfo(Integer siteId, String type);

    /**
     * 添加支付记录
     *
     * @param data   数据
     * @param siteId 站点ID
     * @param type   支付类型
     * @return 创建的记录ID
     */
    Long add(Map<String, Object> data, Integer siteId, String type);

    /**
     * 编辑支付记录
     *
     * @param id     主键ID
     * @param siteId 站点ID
     * @param data   更新数据
     * @return 是否成功
     */
    Boolean edit(Long id, Integer siteId, Map<String, Object> data);


    PageResult<SitePaymentVo> getPage(PageParam pageParam, SitePaymentSearchParam param);

    SitePaymentVo getDetail(Integer id);

    /**
     * 主动绑定用户
     *
     * @param param
     * @return
     */
    Map<String, Object> addByHandle(SitePaymentAEParam param);

    Map<String, Object> editByHandle(SitePaymentAEParam param);
}
