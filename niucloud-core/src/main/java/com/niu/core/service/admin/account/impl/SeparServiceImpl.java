package com.niu.core.service.admin.account.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.enums.account.SeparTradeTypeEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.separ_account.SeparAccountDetailMapper;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.service.admin.account.ISeparService;
import com.niu.core.service.admin.account.param.SeparSearchParam;
import com.niu.core.service.admin.account.vo.SeparDetailVo;
import com.niu.core.service.admin.account.vo.SeparInfoVo;
import com.niu.core.service.admin.account.vo.SeparListVo;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 实现
 */
@Service
public class SeparServiceImpl implements ISeparService {
        
    @Resource
    SeparAccountMapper separAccountMapper;

    @Resource
    SeparAccountDetailMapper separAccountDetailMapper;

    @Resource
    ICoreSeparAccountService coreSeparAccountService;

    @Override
    public PageResult<SeparListVo> page(PageParam pageParam, SeparSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SeparAccount> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getOutTradeNo())) {
            queryWrapper.like("out_trade_no", searchParam.getOutTradeNo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getPayType())) {
            queryWrapper.eq("pay_type", searchParam.getPayType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTradeNo())) {
            queryWrapper.like("trade_no", searchParam.getTradeNo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTradeType())) {
            queryWrapper.eq("trade_type", searchParam.getTradeType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }
        queryWrapper.orderByDesc("id");

        IPage<SeparAccount> iPage = separAccountMapper.selectPage(new Page<>(page, limit), queryWrapper);

        JSONObject payType = PayTypeEnum.getType();
        JSONObject spearTradeType = SeparTradeTypeEnum.getType();

        List<SeparListVo> list = new LinkedList<>();
        for (SeparAccount item : iPage.getRecords()) {
            SeparListVo vo = new SeparListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setPayTypeName(payType.getByPath(vo.getPayType() + ".name", String.class));
            vo.setTradeTypeName(spearTradeType.getStr(vo.getTradeType()));

            List<SeparListVo.SeparData> separDataList = new ArrayList<>();
            List<SeparDetailVo> separDetailVos = detail(item.getId());
            if(!separDetailVos.isEmpty()){
                SeparListVo.SeparData orderSeparData = new SeparListVo.SeparData();
                BigDecimal value = new BigDecimal(0);
                for(SeparDetailVo detailVo : separDetailVos){
                    SeparListVo.SeparData separData = new SeparListVo.SeparData();
                    separData.setValue(detailVo.getValue());
                    separData.setMoneyType(detailVo.getValueSource());
                    separData.setValueType(detailVo.getValueType());
                    separData.setMoneyTypeName(detailVo.getMemo());
                    orderSeparData.setValueType(detailVo.getValueType());
                    value = value.add(detailVo.getValue());
                    separDataList.add(separData);
                }
                orderSeparData.setMoneyTypeName("订单总额");
                orderSeparData.setMoneyType("order_money");
                orderSeparData.setValue(value);
                separDataList.add(orderSeparData);
            }
            Collections.reverse(separDataList);
            vo.setSeparData(separDataList);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SeparDetailVo>  detail(Integer id) {
        List<SeparAccountDetail> records = separAccountDetailMapper.selectList(
        new QueryWrapper<SeparAccountDetail>()
        .eq("separ_id", id));

        List<SeparDetailVo> list = new LinkedList<>();
        for (SeparAccountDetail item : records) {
            SeparDetailVo vo = new SeparDetailVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }

        return list;
    }

    /**
     * 分账
     * @param id
     */
    @Override
    public void separ(Integer id) {
        SeparAccount separ = separAccountMapper.selectById(id);
        Assert.notNull(separ, "分账记录不存在");

        coreSeparAccountService.toSpear(separ);
    }

    @Override
    public SeparInfoVo getInfo(Integer id) {
        QueryWrapper<SeparAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id, site_id, site_name,trade_type,trade_id,trade_no,trade_money,separ_data," +
                "status,fail_reason,create_time,separ_time,out_trade_no,pay_type").eq("id", id);
        SeparAccount separAccount = separAccountMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(separAccount)){
            Assert.notNull(separAccount, "分账记录不存在");
        }
        SeparInfoVo vo = new SeparInfoVo();
        BeanUtils.copyProperties(separAccount, vo);
        // 设置额外的计算字段
        JSONObject payType = PayTypeEnum.getType();
        JSONObject spearTradeType = SeparTradeTypeEnum.getType();
        vo.setPayTypeName(payType.getByPath(vo.getPayType() + ".name", String.class));
        vo.setTradeTypeName(spearTradeType.getStr(vo.getTradeType()));
        vo.setStatusName(vo.getStatus());
        return vo;
    }

}
