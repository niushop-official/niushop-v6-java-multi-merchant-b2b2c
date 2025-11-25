package com.niu.core.listener.shop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("site")
public class ShopCashOutExportDataListener extends ExportDataEventDefiner {

    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;


    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if(!"site_shop_cash_out".equals(event.getType())){
            return new ExportDataEventResult();
        }
        MPJQueryWrapper<SiteShopCashOut> queryWrapper = new MPJQueryWrapper<>();
        JSONObject where = event.getWhere();
        queryWrapper.eq("nssco.site_id", RequestUtils.siteId());
        if(where != null){
            //sql语句
            queryWrapper.select("nssco.id, nssco.site_id, nssco." +
                            "cash_out_no, nssco.account_type, nssco.transfer_type," +
                            " nssco.transfer_realname, nssco.transfer_mobile, " +
                            "nssco.transfer_bank, nssco.transfer_account," +
                            " nssco.transfer_payee, nssco.transfer_payment_code," +
                            " nssco.transfer_fail_reason, nssco.transfer_status, " +
                            "nssco.transfer_time, nssco.apply_money, nssco.rate, " +
                            "nssco.service_money, nssco.money, nssco.audit_time, " +
                            "nssco.status, nssco.remark, nssco.create_time, nssco.refuse_reason," +
                            " nssco.update_time, nssco.transfer_no, nssco.transfer_voucher, " +
                            "nssco.cancel_time, nssco.final_transfer_type, ns.site_name, ns.logo," +
                            " ns.icon, ns.front_end_name, ns.front_end_logo,ns.phone")
                    .setAlias("nssco")
                    .leftJoin("?_site ns ON nssco.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix));

            Object keyword = where.get("keyword");
            if (ObjectUtil.isNotEmpty(keyword)) {
                queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", keyword))
                        .or(j -> j.like("ns.transfer_mobile", keyword))
                );
            }

            if ((where.get("create_time") instanceof String[] createTimes) && ObjectUtil.isNotEmpty(createTimes)) {
                QueryMapperUtils.buildByTime(queryWrapper, "nssco.create_time", createTimes);
            }

            if ((where.get("audit_time") instanceof String[] auditTime) && ObjectUtil.isNotEmpty(auditTime)) {
                QueryMapperUtils.buildByTime(queryWrapper, "nssco.audit_time", auditTime);
            }

            if ((where.get("transfer_time") instanceof String[] transferTime) && ObjectUtil.isNotEmpty(transferTime)) {
                QueryMapperUtils.buildByTime(queryWrapper, "nssco.transfer_time", transferTime);
            }

            Object cashOutNo = where.get("cash_out_no");
            if (ObjectUtil.isNotEmpty(cashOutNo)) {
                queryWrapper.eq("nssco.cash_out_no", cashOutNo);
            }

            Object status = where.get("status");
            if (ObjectUtil.isNotEmpty(status)) {
                queryWrapper.eq("nssco.status", status);
            }

            Object transferType = where.get("transfer_type");
            if (ObjectUtil.isNotEmpty(transferType)) {
                queryWrapper.eq("nssco.transfer_type", transferType);
            }
        }

        PageParam pageParam = event.getPageParam();


        List<SiteShopCashOutListVo> list = new ArrayList<>();

        if(pageParam.getPage() > 0 && pageParam.getLimit() > 0){
            list = siteShopCashOutMapper.selectJoinList(SiteShopCashOutListVo.class, queryWrapper);
        }else{
            list = siteShopCashOutMapper.selectJoinList(SiteShopCashOutListVo.class, queryWrapper);
        }
        for (SiteShopCashOutListVo item : list) {
            SiteShopCashOutListVo.SiteInfo site = new SiteShopCashOutListVo.SiteInfo();
            site.setSiteId(item.getSiteId());
            site.setSiteName(item.getSiteName());
            site.setIcon(item.getIcon());
            site.setFrontEndName(item.getFrontEndName());
            site.setFrontEndLogo(item.getFrontEndLogo());
            site.setPhone(item.getPhone());
            item.setTransferTypeName(ShopTransferEnum.getNameByTag(Integer.parseInt(item.getTransferType())));
            item.setStatusName(ShopCashOutEnum.getStatus().get(item.getStatus()));
            item.setSiteInfo(site);
        }
        return exportData(list);
    }

    private ExportDataEventResult exportData(List<SiteShopCashOutListVo> list){
        ExportDataEventResult result = new ExportDataEventResult();
        if (ObjectUtil.isEmpty(result)) {
            return result;
        }
        JSONArray jsonArray = new JSONArray();
        for(SiteShopCashOutListVo vo : list){
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("cash_out_no",vo.getCashOutNo())
                    .set("site_name",vo.getSiteName())
                    .set("phone",vo.getPhone())
                    .set("transfer_type_name",vo.getTransferTypeName())
                    .set("apply_money",vo.getApplyMoney())
                    .set("rate",vo.getRate())
                    .set("service_money", vo.getServiceMoney())
                    .set("money",vo.getMoney())
                    .set("status_name", vo.getStatusName())
                    .set("transfer_realname", vo.getTransferRealname())
                    .set("transfer_account",vo.getTransferAccount())
                    .set("transfer_bank",vo.getTransferBank())
                    .set("create_time", DateUtils.timestampToString(vo.getCreateTime()))
                    .set("audit_time",DateUtils.timestampToString(vo.getAuditTime()))
                    .set("cancel_time",DateUtils.timestampToString(vo.getCancelTime()))
                    .set("refuse_reason",vo.getRefuseReason())
                    .set("transfer_time",DateUtils.timestampToString(vo.getTransferTime()));
            jsonArray.add(jsonObject);
        }
        result.setResultData(jsonArray);
        return result;
    }
}
