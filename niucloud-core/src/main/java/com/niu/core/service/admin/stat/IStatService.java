package com.niu.core.service.admin.stat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.stat.param.SysStatSearchParam;
import com.niu.core.service.admin.stat.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 统计数据服务层
 */
public interface IStatService {

    /**
     * 首页统计数据
     * @return
     */
    StatInfoVo getIndexData();

    PageResult<SysStatListVo> financeAccountsList(PageParam pageParam, SysStatSearchParam param);

    SysStatInfoVo info(Integer id);

    Map<String, Object> financeAccountsTotal(SysStatSearchParam param);

    /**
     * 获取账单详情
     * @param param 包含日期和类型参数
     * @return 账单详情数据
     */
    SysStatListVo financeAccountsDetail(SysStatSearchParam param);

    /**
     * 首页获取最新动态
     * @return
     */
    List<StatNewsVo> getArticleList(Integer num);

    /**
     * 首页获取数据集
     * @return
     */
    StartChartVo getSartChartVoList();

    /**
     * 商铺统计
     * @return
     */
    ShopStatVo getStat(Integer siteId,String startDate, String endDate);

    /**
     * 查询充值统计总和
     * @return
     */
    SysStatRechargeVo getRechargeStat(Integer siteId, String startDate, String endDate);

    /**
     * 查询充值统计列表
     * @return
     */
    PageResult<SysStatRechargeVo> getRechargeStatList(PageParam pageParam,Integer siteId, String startDate, String endDate, String type);
}