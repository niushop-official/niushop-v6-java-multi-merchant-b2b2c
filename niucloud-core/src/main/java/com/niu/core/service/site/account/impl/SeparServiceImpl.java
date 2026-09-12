package com.niu.core.service.site.account.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.enums.account.SeparTradeTypeEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.separ_account.SeparAccountDetailMapper;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.service.site.account.ISeparService;
import com.niu.core.service.site.account.param.SeparSearchParam;
import com.niu.core.service.site.account.vo.SeparDetailVo;
import com.niu.core.service.site.account.vo.SeparListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public PageResult<SeparListVo> page(PageParam pageParam, SeparSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SeparAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

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
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SeparDetailVo> detail(Integer id) {
        List<SeparAccountDetail> records = separAccountDetailMapper.selectList(
        new QueryWrapper<SeparAccountDetail>()
        .eq("separ_id", id).eq("separ_id", id));

        List<SeparDetailVo> list = new LinkedList<>();
        for (SeparAccountDetail item : records) {
            SeparDetailVo vo = new SeparDetailVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }

        return list;
    }

}
