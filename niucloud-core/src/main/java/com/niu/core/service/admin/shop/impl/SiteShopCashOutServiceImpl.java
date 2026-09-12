package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteSettAccount;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.shop.IShopConfigService;
import com.niu.core.service.admin.shop.ISiteShopCashOutService;
import com.niu.core.service.admin.shop.param.SiteShopCashOutAuditParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutTransferParam;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import com.niu.core.service.admin.site.ISiteSettAccountService;
import com.niu.core.service.api.shop.param.WithdrawApplyParam;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    SiteMapper siteMapper;
    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;
    @Resource
    IShopConfigService shopConfigService;
    @Resource
    ISiteSettAccountService siteSettAccountService;

    @Resource
    private Cached cached;

    @Override
    public PageResult<SiteShopCashOutListVo> page(PageParam pageParam, SiteShopCashOutSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<SiteShopCashOut> queryWrapper = new MPJQueryWrapper<>();
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
                        " ns.icon, ns.front_end_name, ns.front_end_logo")
                .setAlias("nssco")
                .leftJoin("?_site ns ON nssco.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j -> j.like("nssco.transfer_mobile", searchParam.getKeyword()))
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
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus()))) {
            queryWrapper.eq("nssco.status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getTransferType()) && !"all".equals(searchParam.getTransferType())) {
            Integer transferType = ShopTransferEnum.getTagByKey(searchParam.getTransferType());
            if (ObjectUtil.isNotEmpty(transferType)) {
                queryWrapper.eq("nssco.transfer_type", transferType);
            }
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
            item.setTransferTypeName(ShopTransferEnum.getNameByTag(Integer.parseInt(item.getTransferType())));
            item.setStatusName(ShopCashOutEnum.getStatus().get(item.getStatus()));
            item.setSiteInfo(site);
        }
        return PageResult.build(iPage);
    }

    @Override
    public SiteShopCashOutInfoVo info(String id) {
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
                .select("ns.site_id as 'siteInfo.siteId', ns.site_name as 'siteInfo.siteName', " +
                        "ns.phone as 'siteInfo.phone', ns.logo as 'siteInfo.logo', " +
                        "ns.group_id as 'siteInfo.groupId', " +
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
                .eq("nssco.id", id).or()
                .eq("nssco.cash_out_no", id);

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

            // 手动设置站点的基本信息
            siteInfo.setSiteId(infoVo.getSiteId());
            siteInfo.setSiteName(infoVo.getSiteInfo().getSiteName());
            siteInfo.setPhone(infoVo.getSiteInfo().getPhone());
            siteInfo.setLogo(infoVo.getSiteInfo().getLogo());

            infoVo.setSiteInfo(siteInfo);
        }

        return infoVo;
    }

    @Override
    @Transactional
    public void audit(Integer id, SiteShopCashOutAuditParam auditParam) {
        //  获取提现记录信息
        QueryWrapper<SiteShopCashOut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        SiteShopCashOut info = siteShopCashOutMapper.selectOne(queryWrapper);
        // 2. 如果记录不存在，直接返回
        if (info == null) {
            return;
        }
        //  准备更新数据
        SiteShopCashOut data = new SiteShopCashOut();
        data.setId(id);
        data.setUpdateTime(System.currentTimeMillis() / 1000);
        data.setAuditTime(System.currentTimeMillis() / 1000);

        // 设置审核相关参数
        data.setStatus(auditParam.getStatus());
        data.setRefuseReason(auditParam.getRefuseReason());
        // 更新提现记录
        siteShopCashOutMapper.updateById(data);

        //  审核拒绝，退回余额及减去提现中金额
        if (auditParam.getStatus() == -1) {
            Site siteInfo = siteMapper.selectById(info.getSiteId());
            BigDecimal cashOuting = siteInfo.getMoneyCashOuting() != null ? siteInfo.getMoneyCashOuting() : BigDecimal.ZERO;
            BigDecimal money = siteInfo.getMoney() != null ? siteInfo.getMoney() : BigDecimal.ZERO;

            Site updateSite = new Site();
            updateSite.setSiteId(info.getSiteId());
            updateSite.setMoneyCashOuting(cashOuting.subtract(info.getApplyMoney()));
            updateSite.setMoney(money.add(info.getApplyMoney()));
            siteMapper.updateById(updateSite);

            SiteShopAccountLog logData = new SiteShopAccountLog();
            logData.setSiteId(info.getSiteId());
            logData.setMoney(info.getApplyMoney());
            logData.setMoneySum(money.add(info.getApplyMoney()));
            logData.setFromType("cash_out");
            logData.setRelatedId(info.getCashOutNo());
            logData.setMemo("审核拒绝，退回余额");
            logData.setCreateTime(System.currentTimeMillis() / 1000);
            siteShopAccountLogMapper.insert(logData);

        }
    }


    @Override
    @Transactional
    public void transfer(Integer id, SiteShopCashOutTransferParam transferParam) {
        // 1. 查询提现记录
        QueryWrapper<SiteShopCashOut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        SiteShopCashOut info = siteShopCashOutMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(info)) {
            return;
        }

        // 2. 查询站点信息
        Site siteInfo = siteMapper.selectById(info.getSiteId());
        //3. 查询提现状态
        if (!List.of(MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus(), MemberCashOutStatusEnum.TRANSFER_ING.getStatus()).contains(info.getStatus())) {
            throw new CommonException("审核未通过或已转账");
        }
        SiteShopCashOut updateData = new SiteShopCashOut();
        updateData.setId(id);
        updateData.setStatus(3); // 已转账状态
        updateData.setTransferStatus(String.valueOf(1)); // 转账状态
        updateData.setTransferTime(System.currentTimeMillis() / 1000);
        updateData.setUpdateTime(System.currentTimeMillis() / 1000);
        updateData.setMoney(info.getApplyMoney());
        // 添加转账参数中的字段
        updateData.setTransferNo(transferParam.getTransferNo());
        updateData.setTransferVoucher(transferParam.getTransferVoucher());
        // 更新提现记录
        siteShopCashOutMapper.updateById(updateData);

        //  减去站点提现中金额
        BigDecimal cashOuting = siteInfo.getMoneyCashOuting() != null ? siteInfo.getMoneyCashOuting() : BigDecimal.ZERO;
        Site updateSite = new Site();
        updateSite.setSiteId(info.getSiteId());
        updateSite.setMoneyCashOuting(cashOuting.subtract(info.getApplyMoney()));
        siteMapper.updateById(updateSite);

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

    /**
     * 创建提现单号
     *
     * @param siteId 站点ID
     * @return String 提现单号
     */
    private String createCashOutNo(Integer siteId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String timeStr = dateFormat.format(new Date());

        String cacheKey = "cash_out_no_" + siteId + "_" + timeStr;
        Integer maxNo = (Integer) cached.get(cacheKey);

        if (maxNo == null) {
            maxNo = 1;
        } else {
            maxNo += 1; // 自增，不是重置为 1
        }

        String cashOutNo = timeStr + siteId + String.format("%03d", maxNo);
        cached.put(cacheKey, maxNo);

        return cashOutNo;
    }

    /**
     * 申请提现
     *
     * @param withdrawParam 提现申请参数
     * @return Integer 提现记录ID
     */
    @Override
    @Transactional
    public Integer apply(WithdrawApplyParam withdrawParam) {
        // 获取提现配置
        ShopCashOutConfigVo cashOutConfig = shopConfigService.getCashOutConfig();

        // 检查提现是否开启
        if ("0".equals(cashOutConfig.getIsOpen())) {
            throw new CommonException("店铺提现未开启");
        }

        // 检查最低提现金额
        BigDecimal minAmount = new BigDecimal(cashOutConfig.getMin());
        if (minAmount.compareTo(withdrawParam.getApplyMoney()) > 0) {
            throw new CommonException("最低提现金额不能小于" + minAmount);
        }

        // 获取站点账户信息
        Integer siteId = RequestUtils.siteId();
        Site siteAccount = siteMapper.selectById(siteId);

        if (ObjectUtil.isEmpty(siteAccount)) {
            throw new CommonException("提现账户不存在");
        }

        SiteSettAccount siteSettAccount = siteSettAccountService.getSiteSettAccountById(withdrawParam.getAccountId());
        if (ObjectUtil.isEmpty(siteSettAccount)) {
            throw new CommonException("提现账户不存在");
        }

        String transferType = cashOutConfig.getTransferType();
        if (!transferType.contains(String.valueOf(siteSettAccount.getType()))) {
            throw new CommonException("系统暂不支持该转账方式");
        }

        // 检查提现金额是否合法
        if (withdrawParam.getApplyMoney().signum() <= 0) {
            throw new CommonException("提现金额必须大于零");
        }

        BigDecimal siteMoney = siteAccount.getMoney() != null ? siteAccount.getMoney() : BigDecimal.ZERO;
        if (siteMoney.compareTo(withdrawParam.getApplyMoney()) < 0) {
            throw new CommonException("超出最大可提现余额");
        }
        // 计算手续费
        BigDecimal rate = new BigDecimal(cashOutConfig.getRate());
        BigDecimal serviceMoney = withdrawParam.getApplyMoney()
                .multiply(rate)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal actualMoney = withdrawParam.getApplyMoney().subtract(serviceMoney);

        try {
            // 生成提现交易号
            String cashOutNo = createCashOutNo(siteId);

            // 创建提现订单
            SiteShopCashOut cashOut = new SiteShopCashOut();
            cashOut.setSiteId(siteId);
            cashOut.setCashOutNo(cashOutNo);
            cashOut.setAccountType("money");
            cashOut.setServiceMoney(serviceMoney);
            cashOut.setMoney(actualMoney);
            cashOut.setApplyMoney(withdrawParam.getApplyMoney());
            cashOut.setRate(rate);
            cashOut.setTransferMobile("");

            // 使用结算账户获取转账信息
            Map<String, Object> transferData = siteSettAccountService.getTransferDataBySettAccount(siteSettAccount);
            cashOut.setTransferType(String.valueOf(transferData.get("transfer_type")));
            cashOut.setTransferBank(String.valueOf(transferData.get("transfer_bank")));
            cashOut.setTransferRealname(String.valueOf(transferData.get("transfer_realname")));
            cashOut.setTransferAccount(String.valueOf(transferData.get("transfer_account")));
            cashOut.setTransferPaymentCode(String.valueOf(transferData.get("transfer_payment_code")));

            // 设置状态：是否自动审核
            cashOut.setStatus("1".equals(cashOutConfig.getIsAutoVerify()) ? 2 : 1);
            cashOut.setCreateTime(System.currentTimeMillis() / 1000);
            if (cashOutConfig.getIsAutoVerify().equals("1")) {
                cashOut.setAuditTime(System.currentTimeMillis() / 1000);
            }
            // 保存提现记录
            siteShopCashOutMapper.insert(cashOut);

            // 更新站点账户信息
            BigDecimal cashOuting = siteAccount.getMoneyCashOuting() != null ? siteAccount.getMoneyCashOuting() : BigDecimal.ZERO;
            siteAccount.setMoneyCashOuting(cashOuting.add(withdrawParam.getApplyMoney()));
            siteAccount.setMoney(siteMoney.subtract(withdrawParam.getApplyMoney()));
            siteMapper.updateById(siteAccount);

            SiteShopAccountLog accountLog = new SiteShopAccountLog();
            accountLog.setSiteId(siteId);
            accountLog.setMoney(withdrawParam.getApplyMoney().negate());
            accountLog.setMoneySum(siteAccount.getMoney());
            accountLog.setFromType("cash_out");
            accountLog.setRelatedId(cashOutNo);
            accountLog.setMemo("店铺提现");
            accountLog.setCreateTime(System.currentTimeMillis() / 1000);
            siteShopAccountLogMapper.insert(accountLog);

            return cashOut.getId();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }


}
