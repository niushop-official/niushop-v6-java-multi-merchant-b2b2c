package com.niu.core.service.site.shop.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopAccountLog;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.mapper.shop.SiteShopAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.admin.shop.IShopConfigService;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import com.niu.core.service.core.weapp.ICoreWeappService;
import com.niu.core.service.site.shop.ISiteShopCashOutService;
import com.niu.core.service.admin.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import com.niu.core.service.api.shop.param.WithdrawApplyParam;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import com.niu.core.service.site.shop.vo.SiteShopCashOutCancelVo;
import com.niu.core.service.site.shop.vo.SiteShopCashOutCodeVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 商家站点提现实现
 */
@Service
public class SiteShopCashOutServiceImpl implements ISiteShopCashOutService {

    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;
    @Resource
    private Cached cached;
    @Resource
    SiteShopMapper siteShopMapper;
    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;
    @Resource
    IShopConfigService shopConfigService;
    @Resource
    ICoreSysConfigService coreSysConfigService;
    @Resource
    ICoreWeappService coreWeappService;

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
        queryWrapper.eq("nssco.site_id", RequestUtils.siteId());
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
                        "ns.group_id as 'siteInfo.groupId', ns.keywords as 'siteInfo.keywords', " +
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

        // 获取店铺账户信息
        Integer siteId = RequestUtils.siteId();
        SiteShop shopAccount = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        // 检查转账方式是否支持
        String transferType = cashOutConfig.getTransferType();
        String bankType = String.valueOf(shopAccount.getBankType());
        if (transferType.equals(bankType)) {
            throw new CommonException("系统暂不支持该转账方式");
        }
        if (ObjectUtil.isEmpty(shopAccount)) {
            throw new CommonException("提现账户不存在");
        }
        // 检查提现金额是否合法
        if (withdrawParam.getApplyMoney().signum()<=0) {
            throw new CommonException("提现金额必须大于零");
        }
        // 检查余额是否足够
        if (shopAccount.getMoney().compareTo(withdrawParam.getApplyMoney()) < 0) {
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

            // 设置转账方式信息
            Integer bankTypeInt = shopAccount.getBankType();
            cashOut.setTransferType(bankTypeInt.toString());
            if (bankTypeInt == 1) { // 银行卡
                cashOut.setTransferBank(shopAccount.getBankName());
                cashOut.setTransferRealname(shopAccount.getBankAccountName());
                cashOut.setTransferAccount(shopAccount.getBankAccountNo());
            } else if (bankTypeInt == 2) { // 支付宝
                cashOut.setTransferRealname(shopAccount.getAlipayName());
                cashOut.setTransferAccount(shopAccount.getAlipayAccountNo());
                cashOut.setTransferPaymentCode(shopAccount.getAlipayPaymentCode());
            } else if (bankTypeInt == 3) { // 微信收款码
                cashOut.setTransferRealname(shopAccount.getWechatName());
                cashOut.setTransferAccount(shopAccount.getWechatAccountNo());
                cashOut.setTransferPaymentCode(shopAccount.getWechatPaymentCode());
            }

            // 设置状态：是否自动审核
            cashOut.setStatus("1".equals(cashOutConfig.getIsAutoVerify()) ? 2 : 1);
            cashOut.setCreateTime(System.currentTimeMillis() / 1000);
            // 保存提现记录
            siteShopCashOutMapper.insert(cashOut);

            // 更新店铺账户信息
            shopAccount.setMoneyCashOuting(shopAccount.getMoneyCashOuting().add(withdrawParam.getApplyMoney()));
            shopAccount.setMoney(shopAccount.getMoney().subtract(withdrawParam.getApplyMoney()));
            siteShopMapper.updateById(shopAccount);

            // 添加账户收支记录
            SiteShopAccountLog accountLog = new SiteShopAccountLog();
            accountLog.setSiteId(siteId);
            accountLog.setMoney(withdrawParam.getApplyMoney().negate()); // 负数表示支出
            accountLog.setMoneySum(shopAccount.getMoney());
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

    @Override
    @Transactional
    public Boolean cancel(Integer id) {
        SiteShopCashOut info = siteShopCashOutMapper.selectById(id);
        if(ObjectUtil.isEmpty(info)){
           return true;
        }
        SiteShopCashOut update = new SiteShopCashOut();
        update.setCancelTime(System.currentTimeMillis() / 1000);
        update.setUpdateTime(System.currentTimeMillis() / 1000);
        update.setStatus(-2);
        update.setId(id);
        siteShopCashOutMapper.updateById(update);
        //审核取消，退回余额及减去提现中金额
        SiteShop shopAccount =siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", info.getSiteId()));
        shopAccount.setMoneyCashOuting(shopAccount.getMoneyCashOuting().subtract(info.getApplyMoney()));
        shopAccount.setMoney(shopAccount.getMoney().add(info.getApplyMoney()));
        siteShopMapper.updateById(shopAccount);

        // 添加账户收支记录
        SiteShopAccountLog accountLog = new SiteShopAccountLog();
        accountLog.setSiteId(info.getSiteId());
        accountLog.setMoney(info.getApplyMoney()); // 负数表示支出
        accountLog.setMoneySum(shopAccount.getMoney().add(info.getApplyMoney()));
        accountLog.setFromType("cash_out");
        accountLog.setRelatedId(info.getCashOutNo());
        accountLog.setMemo("用户取消提现，退回余额");
        accountLog.setCreateTime(System.currentTimeMillis() / 1000);
        siteShopAccountLogMapper.insert(accountLog);
        return true;
    }

    @Override
    @Transactional
    public SiteShopCashOutCodeVo transfer(Integer id) {
        SiteShopCashOut siteShopCashOut = siteShopCashOutMapper.selectById(id);
        if(ObjectUtil.isEmpty(siteShopCashOut)){
            throw new CommonException("提现记录不存在");
        }
        List<Integer> status = Arrays.asList(ShopCashOutEnum.WAIT_TRANSFER.getState(), ShopCashOutEnum.TRANSFER_ING.getState());
        if(!status.contains(siteShopCashOut.getStatus())){
            throw new CommonException("审核未通过或已转账");
        }
        if(!ShopTransferEnum.WECHAT.getTag().toString().equals(siteShopCashOut.getTransferType())){
            throw new CommonException("不支持该提现方式");
        }
        SiteShopCashOutCodeVo updateData = new SiteShopCashOutCodeVo();
        String checkCode = generateCheckCode();
        cached.put(checkCode, id, 900);

        updateData.setPage("app/pages/site/transfer?cod="+checkCode);

        try {
            updateData.setQrcode(getQrcode(RequestUtils.siteId(), checkCode));
        }catch ( Exception e){

        }

        try {
            updateData.setUrlLink(getUrlLink(checkCode));
        }catch ( Exception e){

        }

        return updateData;
    }


    /**
     * 生成转账小程序二维码
     * @param checkCode $check_code
     * @return string
     */
    public String getQrcode(Integer siteId, String checkCode) {
        SceneDomainVo sceneDomain = coreSysConfigService.getSceneDomain();
        String page = "app/pages/site/transfer";
        Map<String, Object> data = new HashMap<>();
        data.put("key", "cod");
        data.put("value", checkCode);
        String dir = "upload/qrcode/" + siteId + "/transfer";
        String channel = "weapp";
        return QrcodeUtils.qrcodeToFile(siteId, channel, sceneDomain.getWapUrl(), page, data, dir);
    }

    /**
     * 生成转账小程序短链
     * @param checkCode $check_code
     * @return string
     */
    public String getUrlLink(String checkCode) {
        String page = "app/pages/site/transfer";
        Map<String, Object> data = new HashMap<>();
        data.put("key", "cod");
        data.put("value", checkCode);
        return coreWeappService.getUrlLink(page,data);
    }

    public static String generateCheckCode() {
        try {
            // 生成唯一标识符，类似 PHP 的 uniqid('', true)
            String uniqueId = UUID.randomUUID().toString().replace("-", "");

            // 计算 MD5 哈希值
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(uniqueId.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // 截取前5个字符，类似 PHP 的 substr(..., 0, 5)
            return hexString.substring(0, 5);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
