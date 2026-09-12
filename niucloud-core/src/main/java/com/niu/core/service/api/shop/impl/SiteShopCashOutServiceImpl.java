package com.niu.core.service.api.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.api.shop.ISiteShopCashOutService;
import com.niu.core.service.api.shop.param.SiteShopCashOutAuditParam;
import com.niu.core.service.api.shop.param.SiteShopCashOutParam;
import com.niu.core.service.api.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.api.shop.vo.SiteShopCashOutInfoNewVo;
import com.niu.core.service.api.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopCashOutListVo;
import com.niu.core.service.core.shop.ICoreShopCashOutService;
import com.niu.core.service.core.shop.param.ShopTransferParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商家站点提现实现
 */
@Service
public class SiteShopCashOutServiceImpl implements ISiteShopCashOutService {
        
    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;
    @Resource
    ICoreShopCashOutService coreShopCashOutService;
    @Resource
    SiteShopMapper siteShopMapper;
    @Resource
    SiteMapper siteMapper;
    @Resource
    private Cached cached;
    @Override
    public PageResult<SiteShopCashOutListVo> page(PageParam pageParam, SiteShopCashOutSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();


        MPJQueryWrapper<SiteShopCashOut> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nssco.id, nssco.site_id, nssco.cash_out_no, nssco.account_type, nssco.transfer_type, nssco.transfer_realname, nssco.transfer_mobile, nssco.transfer_bank, nssco.transfer_account, nssco.transfer_payee, nssco.transfer_payment_code, nssco.transfer_fail_reason, nssco.transfer_status, nssco.transfer_time, nssco.apply_money, nssco.rate, nssco.service_money, nssco.money, nssco.audit_time, nssco.status, nssco.remark, nssco.create_time, nssco.refuse_reason, nssco.update_time, nssco.transfer_no, nssco.transfer_voucher, nssco.cancel_time, nssco.final_transfer_type, ns.site_name, ns.logo, ns.icon, ns.front_end_name, ns.front_end_logo")
                .setAlias("nssco")
                .leftJoin("?_site ns ON nssco.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j-> j.like("nssco.transfer_mobile", searchParam.getKeyword()))
            );
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssco.create_time", searchParam.getCreateTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getAuditTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssco.audit_time", searchParam.getAuditTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getTransferTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "nssco.transfer_time", searchParam.getTransferTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCashOutNo())) {
            queryWrapper.eq("nssco.cash_out_no", searchParam.getCashOutNo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("nssco.status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getTransferType())) {
            queryWrapper.eq("nssco.transfer_type", searchParam.getTransferType());
        }

        queryWrapper.orderByDesc("nssco.create_time");

        IPage<SiteShopCashOutListVo> iPage = siteShopCashOutMapper.selectJoinPage(new Page<>(page, limit), SiteShopCashOutListVo.class, queryWrapper);
        for (SiteShopCashOutListVo item : iPage.getRecords()) {
            SiteShopCashOutListVo.SiteInfo site = new SiteShopCashOutListVo.SiteInfo();
            site.setSiteId(item.getSiteId());
            site.setSiteName(item.getSiteName());
            site.setIcon(item.getIcon());
            site.setFrontEndName(item.getFrontEndName());
            site.setFrontEndLogo(item.getFrontEndLogo());
            item.setSiteInfo(site);
        }
        return PageResult.build(iPage);
    }

    @Override
    public SiteShopCashOutInfoVo info(Integer id) {


        MPJQueryWrapper<SiteShopCashOut> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("nssco.id, nssco.site_id, nssco.apply_money, nssco.money, nssco.service_money, " +
                        "nssco.status, nssco.create_time, nssco.audit_time, nssco.transfer_type, nssco.transfer_time, " +
                        // 前端页面需要字段
                        "nssco.account_type, nssco.cash_out_no, nssco.transfer_realname, nssco.transfer_mobile, " +
                        "nssco.transfer_bank, nssco.transfer_account, nssco.transfer_payment_code, " +
                        "nssco.transfer_fail_reason, nssco.transfer_status, nssco.rate, nssco.refuse_reason, " +
                        "nssco.remark, nssco.update_time, nssco.transfer_no, nssco.transfer_voucher, " +
                        "nssco.cancel_time, nssco.final_transfer_type, nssco.transfer_payee")
                .setAlias("nssco")
                // 2. 关联站点表
                .leftJoin("?_site ns ON nssco.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON nssco.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ns.site_id as 'siteInfo.siteId', ns.site_name as 'siteInfo.siteName', " +
                        "ns.phone as 'siteInfo.phone', ns.logo as 'siteInfo.logo', " +
                        "ns.group_id as 'siteInfo.groupId', nss.keywords as 'siteInfo.keywords', " +
                        "ns.app_type as 'siteInfo.appType', ns.addons as 'siteInfo.addons', " +
                        "ns.address as 'siteInfo.address', ns.app as 'siteInfo.app', " +
                        "ns.business_hours as 'siteInfo.businessHours', ns.city_id as 'siteInfo.cityId', " +
                        "ns.create_time as 'siteInfo.createTime', ns.desc as 'siteInfo.desc', " +
                        "ns.district_id as 'siteInfo.districtId', ns.expire_time as 'siteInfo.expireTime', " +
                        "ns.front_end_logo as 'siteInfo.frontEndLogo', ns.front_end_name as 'siteInfo.frontEndName', " +
                        "ns.full_address as 'siteInfo.fullAddress', ns.icon as 'siteInfo.icon', " +
                        "ns.latitude as 'siteInfo.latitude', ns.longitude as 'siteInfo.longitude', " +
                        "ns.member_no as 'siteInfo.memberNo', ns.province_id as 'siteInfo.provinceId', " +
                        "ns.site_domain as 'siteInfo.siteDomain', ns.status as 'siteInfo.status'")
                .eq("nssco.id", id) .or()
                .eq("nssco.cash_out_no", id);
        queryWrapper.eq("nssco.site_id", RequestUtils.siteId());
        SiteShopCashOutInfoVo infoVo = siteShopCashOutMapper.selectJoinOne(SiteShopCashOutInfoVo.class, queryWrapper);
        if (infoVo == null) {
            return new SiteShopCashOutInfoVo();
        }
        // 设置状态名称
        infoVo.setStatusName(ShopCashOutEnum.getStatus().get(infoVo.getStatus()));
        if (infoVo.getTransferType() != null) {
            infoVo.setTransferTypeName(ShopTransferEnum.getNameByTag(
                    Integer.parseInt(infoVo.getTransferType())));
        }
        if (infoVo.getSiteInfo() == null) {
            SiteShopCashOutInfoVo.SiteInfoVO siteInfo = new SiteShopCashOutInfoVo.SiteInfoVO();
            QueryWrapper<SiteShop> siteQueryWrapper = new QueryWrapper<>();
            siteQueryWrapper.eq("site_id", infoVo.getSiteId());
            SiteShop siteShop = siteShopMapper.selectOne(siteQueryWrapper);

            if (siteShop != null) {
                // 手动设置站点的基本信息
                siteInfo.setSiteId(siteShop.getSiteId());
                siteInfo.setSiteName(infoVo.getSiteInfo().getSiteName());
                siteInfo.setPhone(infoVo.getSiteInfo().getPhone());
                siteInfo.setLogo(infoVo.getSiteInfo().getLogo());
            }

            infoVo.setSiteInfo(siteInfo);
        }

        return infoVo;
    }

    @Override
    @Transactional
    public void audit(Integer id, SiteShopCashOutAuditParam auditParam) {

    }

    @Override
    public void transfer(Integer id, ShopTransferParam transferParam) {
        transferParam.setChannel(RequestUtils.channel());
        coreShopCashOutService.transfer(id, transferParam);
    }

    @Override
    public Object checkTransfer(String code) {
        if (StringUtils.isEmpty( code)){
            throw new ApiException("转账二维码已过期");
        }
        Object id =  cached.get( code);
        if (ObjectUtil.isEmpty(id)){
            throw new ApiException("转账二维码已过期");
        }
        return id;
    }

    @Override
    public void add(SiteShopCashOutParam addParam) {

        SiteShopCashOut model = new SiteShopCashOut();

        model.setAccountType(addParam.getAccountType());
        model.setApplyMoney(addParam.getApplyMoney());
        model.setAuditTime(addParam.getAuditTime());
        model.setCancelTime(addParam.getCancelTime());
        model.setCashOutNo(addParam.getCashOutNo());
        model.setFinalTransferType(addParam.getFinalTransferType());
        model.setMoney(addParam.getMoney());
        model.setRate(addParam.getRate());
        model.setRefuseReason(addParam.getRefuseReason());
        model.setRemark(addParam.getRemark());
        model.setServiceMoney(addParam.getServiceMoney());
        model.setStatus(addParam.getStatus());
        model.setTransferAccount(addParam.getTransferAccount());
        model.setTransferBank(addParam.getTransferBank());
        model.setTransferFailReason(addParam.getTransferFailReason());
        model.setTransferMobile(addParam.getTransferMobile());
        model.setTransferNo(addParam.getTransferNo());
        model.setTransferPayee(addParam.getTransferPayee());
        model.setTransferPaymentCode(addParam.getTransferPaymentCode());
        model.setTransferRealname(addParam.getTransferRealname());
        model.setTransferStatus(addParam.getTransferStatus());
        model.setTransferTime(addParam.getTransferTime());
        model.setTransferType(addParam.getTransferType());
        model.setTransferVoucher(addParam.getTransferVoucher());

        model.setSiteId(RequestUtils.siteId());

        siteShopCashOutMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopCashOutParam editParam) {

        SiteShopCashOut model = siteShopCashOutMapper.selectOne(
                new QueryWrapper<SiteShopCashOut>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商家站点提现不存在");
        model.setAccountType(editParam.getAccountType());
        model.setApplyMoney(editParam.getApplyMoney());
        model.setAuditTime(editParam.getAuditTime());
        model.setCancelTime(editParam.getCancelTime());
        model.setCashOutNo(editParam.getCashOutNo());
        model.setFinalTransferType(editParam.getFinalTransferType());
        model.setMoney(editParam.getMoney());
        model.setRate(editParam.getRate());
        model.setRefuseReason(editParam.getRefuseReason());
        model.setRemark(editParam.getRemark());
        model.setServiceMoney(editParam.getServiceMoney());
        model.setStatus(editParam.getStatus());
        model.setTransferAccount(editParam.getTransferAccount());
        model.setTransferBank(editParam.getTransferBank());
        model.setTransferFailReason(editParam.getTransferFailReason());
        model.setTransferMobile(editParam.getTransferMobile());
        model.setTransferNo(editParam.getTransferNo());
        model.setTransferPayee(editParam.getTransferPayee());
        model.setTransferPaymentCode(editParam.getTransferPaymentCode());
        model.setTransferRealname(editParam.getTransferRealname());
        model.setTransferStatus(editParam.getTransferStatus());
        model.setTransferTime(editParam.getTransferTime());
        model.setTransferType(editParam.getTransferType());
        model.setTransferVoucher(editParam.getTransferVoucher());
        siteShopCashOutMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteShopCashOutMapper.delete(new QueryWrapper<SiteShopCashOut>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public Map<String, BigDecimal> getWithdrawData() {
        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(money) AS money, SUM(money_get) AS money_get, SUM(money_cash_outing) AS money_cash_outing");
        queryWrapper.ne("site_id", 0);
        List<Map<String, Object>> shopStatMap = siteMapper.selectMaps(queryWrapper);
        Map<String, Object> map = ObjectUtil.defaultIfNull(shopStatMap.get(0), new HashMap<>());
        Map<String, BigDecimal> statMap = new HashMap<>();

        BigDecimal money = ObjectUtil.isNotEmpty(map.get("money")) ? new BigDecimal(map.get("money").toString()) : BigDecimal.ZERO;
        BigDecimal moneyGet = ObjectUtil.isNotEmpty(map.get("money_get")) ? new BigDecimal(map.get("money_get").toString()) : BigDecimal.ZERO;
        BigDecimal moneyCashOuting = ObjectUtil.isNotEmpty(map.get("money_cash_outing")) ? new BigDecimal(map.get("money_cash_outing").toString()) : BigDecimal.ZERO;

        statMap.put("withdrawal_money", moneyGet.subtract(money).subtract(moneyCashOuting));
        statMap.put("money_cash_outing", moneyCashOuting);
        return statMap;
    }

}
