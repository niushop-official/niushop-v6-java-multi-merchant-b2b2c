package com.niu.core.listener.shop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("mall")
public class ShopAccountExportDataListener extends ExportDataEventDefiner {

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;
    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if(!"shop_account_log".equals(event.getType())){
            return new ExportDataEventResult();
        }
        QueryWrapper<SiteShopAccountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        JSONObject where = event.getWhere();
        if(where != null) {
            Object relatedId = where.get("related_id");
            if (ObjectUtil.isNotEmpty(relatedId)) {
                queryWrapper.eq("related_id", relatedId);
            }

            Object fromType = where.get("from_type");
            if (ObjectUtil.isNotEmpty(fromType)) {
                queryWrapper.eq("from_type", fromType);
            }
            if ((where.get("create_time") instanceof JSONArray createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", StringUtils.jsonArrayToStringArray(createTimes));
            }
        }
        PageParam pageParam = event.getPageParam();
        List<SiteShopAccountLog> list = new ArrayList<>();
        if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
            list = siteShopAccountLogMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper).getRecords();
        }else{
            list = siteShopAccountLogMapper.selectList(queryWrapper);
        }

        ExportDataEventResult result = new ExportDataEventResult();
        JSONArray jsonArray = new JSONArray();
        for(SiteShopAccountLog vo : list){
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("from_type_name", ShopAccountEnum.getNameByType(vo.getFromType()).getName())
                    .set("money",vo.getMoney())
                    .set("create_time", DateUtils.timestampToString(vo.getCreateTime()))
                    .set("memo",vo.getMemo());
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
