package com.niu.core.service.site.delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetListsParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetPagesParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetParam;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetListVo;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetPageVo;
import com.niu.core.service.core.delivery.vo.ElectronicSheetConfigValueVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 电子面单服务接口
 */
public interface IDeliveryElectronicSheetService {

    List<DeliveryElectronicSheetListVo> lists(PageParam pageParam, DeliveryElectronicSheetListsParam listsParam);

    /**
     * 电子面单列表
     *
     * @param pageParam  分页参数
     * @param pagesParam 搜索参数
     * @return PageResult<ShopDeliveryElectronicSheetListVo>
     */
    PageResult<DeliveryElectronicSheetPageVo> pages(PageParam pageParam, DeliveryElectronicSheetPagesParam pagesParam);

    /**
     * 电子面单详情
     *
     * @param id 主键ID
     * @return ShopDeliveryElectronicSheetInfoVo
     */
    DeliveryElectronicSheetInfoVo info(Integer id);

    /**
     * 电子面单添加
     *
     * @param addParam 添加参数
     */
    void add(DeliveryElectronicSheetParam addParam);

    /**
     * 电子面单编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DeliveryElectronicSheetParam editParam);

    /**
     * 电子面单删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    Map<Integer, String> getPayType();

    @Transactional
    void setDefault(Integer id);

    ElectronicSheetConfigValueVo getElectronicSheetConfig();

    void setElectronicSheetConfig(Object config);
}
