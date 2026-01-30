package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.enums.cashout.CashOutTypeEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.listener.pay.PayExportDataListener;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.pay.ICoreTransferService;
import com.niu.core.service.core.shop.ICoreShopCashOutService;
import com.niu.core.service.core.shop.param.ShopTransferParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoreShopCashOutServiceImpl implements ICoreShopCashOutService {

    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    ICoreTransferService coreTransferService;

    @Override
    public void transferFinish(String transferNo) {
        SiteShopCashOut siteShopCashOut = siteShopCashOutMapper.selectOne(new QueryWrapper<SiteShopCashOut>().eq("transfer_no", transferNo));
        if (siteShopCashOut != null) throw new CommonException("店铺提现不存在");
        if(!siteShopCashOut.getStatus().equals(ShopCashOutEnum.WAIT_TRANSFER.getState()) && !siteShopCashOut.getStatus().equals(ShopCashOutEnum.TRANSFER_ING.getState()) ) throw new CommonException("店铺提现状态不正确");
        this.give(siteShopCashOut);
        siteShopCashOut.setStatus(ShopCashOutEnum.TRANSFERED.getState());
        siteShopCashOut.setTransferTime(System.currentTimeMillis() / 1000);
        siteShopCashOutMapper.updateById(siteShopCashOut);
    }

    @Override
    public void give(SiteShopCashOut siteShopCashOut) {
        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteShopCashOut.getSiteId()));
        if(ObjectUtil.isEmpty(shop)) throw new CommonException("店铺不存在");
        shop.setMoneyCashOuting(shop.getMoneyCashOuting().subtract(siteShopCashOut.getApplyMoney()));
        siteShopMapper.updateById(shop);
    }

    @Override
    public SiteShopCashOut find(Integer id) {
        return siteShopCashOutMapper.selectById(id);
    }

    @Override
    @Transactional
    public void transfer(Integer id, ShopTransferParam shopTransferParam) {
        try {
            String transferType = shopTransferParam.getTransferType() != null ? shopTransferParam.getTransferType() : "";

            SiteShopCashOut cashOut = this.find(id);
            if (cashOut == null) throw new CommonException("提现记录不存在");
            if (!cashOut.getStatus().equals(ShopCashOutEnum.WAIT_TRANSFER.getState()) && !cashOut.getStatus().equals(ShopCashOutEnum.TRANSFER_ING.getState()))
                throw new CommonException("提现状态不属于待转账");

            String transferNo = cashOut.getTransferNo();
            if (transferNo == null || transferNo.isEmpty()) {

                PayTransfer payTransfer = new PayTransfer();
                payTransfer.setSiteId(RequestUtils.defaultSiteId());
                payTransfer.setMoney(cashOut.getApplyMoney());
                payTransfer.setTradeType(CashOutTypeEnum.SHOP_CASH_OUT.getName());
                payTransfer.setMainType("site");
                payTransfer.setMainId(cashOut.getSiteId());
                payTransfer.setRemark("店铺提现转账");
                transferNo = coreTransferService.create(payTransfer);

                cashOut.setTransferNo(transferNo);
                cashOut.setStatus(ShopCashOutEnum.TRANSFER_ING.getState());
                siteShopCashOutMapper.updateById(cashOut);
            }

            Map<String, Object> transferData = new HashMap<>();
            if (!transferType.equals("offline")) {
                // 获取转账类型的key;
                transferType = ShopTransferEnum.getKeyByTag(Integer.parseInt(cashOut.getTransferType()));

                transferData.put("transfer_type", transferType);
                transferData.put("transfer_realname", cashOut.getTransferRealname());
                transferData.put("transfer_mobile", cashOut.getTransferMobile());
                transferData.put("transfer_bank", cashOut.getTransferBank());
                transferData.put("transfer_account", cashOut.getTransferAccount());
                transferData.put("transfer_payment_code", cashOut.getTransferPaymentCode());

                if (ShopTransferEnum.WECHAT.getKey().equals(transferType)) {
                    // 如果是转账到微信钱包，则需要获取openid
                    Map<String, String> payeeInfo = new HashMap<>();
                    payeeInfo.put("open_id", shopTransferParam.getOpenId() != null ? shopTransferParam.getOpenId() : "");
                    payeeInfo.put("channel", shopTransferParam.getChannel() != null ? shopTransferParam.getChannel() : "");
                    transferData.put("transfer_payee", payeeInfo);
                }
            } else {
                // 离线转账，仍然获取转账类型的key
                transferType = shopTransferParam.getTransferType() != null ? shopTransferParam.getTransferType() : "";
            }

            coreTransferService.transfer(transferNo, transferType, transferData);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
