package com.niu.core.service.admin.site.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.enums.account.SeparValueSourceEnum;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.mapper.site.SiteFinanceLogMapper;
import com.niu.core.service.admin.site.ISiteFinanceLogService;
import com.niu.core.service.admin.site.param.SiteFinanceLogParam;
import com.niu.core.service.admin.site.vo.SiteFinanceLogVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SiteFinanceLogServiceImpl implements ISiteFinanceLogService {

    @Resource
    private SiteFinanceLogMapper siteFinanceLogMapper;

    @Override
    public PageResult<SiteFinanceLogVo> getPage(PageParam pageParam, SiteFinanceLogParam siteFinanceLogParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<SiteFinanceLog> queryWrapper = new QueryWrapper<>();
        // 设置查询字段
        queryWrapper.select(SiteFinanceLog.class,
                info -> Arrays.asList("id", "main_id", "main_name", "site_id", "type", "value",
                          "trade_type", "trade_no", "create_time",
                "memo", "rae_type").contains(info.getColumn()));
        if(RequestUtils.siteId() != 0){
            queryWrapper.like("site_id",RequestUtils.siteId());
        }
        if (ObjectUtil.isNotEmpty(siteFinanceLogParam.getTradeNo())) {
                queryWrapper.like("trade_no", siteFinanceLogParam.getTradeNo());
        }
      if (ObjectUtil.isNotEmpty(siteFinanceLogParam.getCreateTime())) {
          String[] createTime = siteFinanceLogParam.getCreateTime();
          QueryMapperUtils.buildByTime(queryWrapper, "create_time", createTime);
      }
        // 排序
        queryWrapper.orderByDesc("create_time","id");
        //分页
        IPage<SiteFinanceLog> iPage = siteFinanceLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        //转换为 VO
        List<SiteFinanceLogVo> voList = new ArrayList<>();
        for (SiteFinanceLog record : iPage.getRecords()) {
            SiteFinanceLogVo vo = new SiteFinanceLogVo();
            BeanUtils.copyProperties(record, vo);

            vo.setTypeName(SiteFinanceLogEnum.getName(record.getType()));
            vo.setTradeTypeName(SiteFinanceLogEnum.getName(record.getTradeType()));
            vo.setRaeTypeName( SiteFinanceLogEnum.getName(record.getRaeType()));
            voList.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(voList);
    }
}
