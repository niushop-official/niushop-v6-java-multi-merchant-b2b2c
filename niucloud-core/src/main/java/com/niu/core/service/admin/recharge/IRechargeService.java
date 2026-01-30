package com.niu.core.service.admin.recharge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.recharge.param.RechargeEditSortParam;
import com.niu.core.service.admin.recharge.param.RechargeModifyStatusParam;
import com.niu.core.service.admin.recharge.param.RechargeParam;
import com.niu.core.service.admin.recharge.vo.RechargeInitVo;
import com.niu.core.service.admin.recharge.vo.RechargeListVo;
import com.niu.core.service.admin.recharge.param.RechargePageParam;

import java.util.Map;

public interface IRechargeService {

    /**
     * 获取充值套餐列表
     * @param pageParam
     * @param param
     * @return
     */
    PageResult<RechargeListVo> getPage(PageParam pageParam, RechargePageParam param);

    /**
     * 添加充值套餐
     * @param param
     */
    void add(RechargeParam param);

    /**
     * 修改充值套餐
     * @param id
     * @param param
     */
    void edit(Integer id, RechargeParam param);

    /**
     * 获取充值套餐初始化信息
     * @param rechargeId
     * @return
     */
    RechargeInitVo getInit(Integer rechargeId);

    /**
     * 删除充值套餐
     * @param id
     */
    void del(Integer id);

    /**
     * 修改充值套餐状态
     * @param param
     */
    void modifyStatus(RechargeModifyStatusParam param);

    /**
     * 修改充值套餐排序
     * @param param
     */
    void editSort(RechargeEditSortParam param);

    /**
     * 获取充值套餐赠送商品字典
     * @return
     */
    JSONArray getPackageGiftDict();
}
