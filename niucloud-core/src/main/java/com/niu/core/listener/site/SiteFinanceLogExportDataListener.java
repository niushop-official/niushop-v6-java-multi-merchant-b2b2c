package com.niu.core.listener.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.enums.account.SeparValueSourceEnum;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.site.SiteFinanceLogMapper;
import com.niu.core.service.admin.site.vo.SiteFinanceLogVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@EventCallback("mall")
public class SiteFinanceLogExportDataListener extends ExportDataEventDefiner {

    @Resource
    private SiteFinanceLogMapper siteFinanceLogMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if(!"site_finance_log".equals(event.getType())){
            return new ExportDataEventResult();
        }
        QueryWrapper<SiteFinanceLog> queryWrapper = new QueryWrapper<>();
        JSONObject where = event.getWhere();
        queryWrapper.select(SiteFinanceLog.class,
                info -> Arrays.asList("id", "main_id", "main_name", "site_id", "type", "value",
                        "trade_type", "trade_no", "create_time",
                        "memo", "rae_type").contains(info.getColumn()));

        if(RequestUtils.siteId() != 0){
            queryWrapper.eq("site_id",RequestUtils.siteId());
        }
        if(where != null){
            Object tradeNo = where.get("trade_no");
            if (ObjectUtil.isNotEmpty(tradeNo)) {
                queryWrapper.like("trade_no", tradeNo);
            }

            if ((where.get("create_time") instanceof String[] createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", createTimes);
            }
        }
        PageParam pageParam = event.getPageParam();
        List<SiteFinanceLog> list = new ArrayList<>();
        List<SiteFinanceLogVo> voList = new ArrayList<>();
        if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
            list = siteFinanceLogMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper).getRecords();
        }else{
            list = siteFinanceLogMapper.selectList(queryWrapper);
        }
        for (SiteFinanceLog record : list) {
            SiteFinanceLogVo vo = new SiteFinanceLogVo();
            BeanUtils.copyProperties(record, vo);
            vo.setTypeName(SeparValueSourceEnum.getName(record.getType()));
            vo.setTradeTypeName(SiteFinanceLogEnum.getName(record.getTradeType()));
            vo.setRaeTypeName( SiteFinanceLogEnum.getName(record.getRaeType()));
            voList.add(vo);
        }
        return exportData(voList);
    }
    private ExportDataEventResult exportData(List<SiteFinanceLogVo> voList){
        ExportDataEventResult result = new ExportDataEventResult();
        if (ObjectUtil.isEmpty(result)) {
            return result;
        }
        JSONArray jsonArray = new JSONArray();
        for(SiteFinanceLogVo vo : voList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("trade_no",vo.getTradeNo())
                    .set("main_name",vo.getMainName())
                    .set("trade_type_name",vo.getTradeTypeName())
                    .set("type_name",vo.getTypeName())
                    .set("rae_type_name",vo.getRaeTypeName())
                    .set("value",vo.getValue())
                    .set("create_time", DateUtils.timestampToString(vo.getCreateTime()))
                    .set("memo",vo.getMemo());
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
