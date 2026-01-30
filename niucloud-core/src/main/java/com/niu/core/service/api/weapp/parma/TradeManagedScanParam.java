
package com.niu.core.service.api.weapp.parma;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class TradeManagedScanParam implements ApiBaseParam {
    // 微信订单号
    private String transactionId;
    // 支付下单商户的商户号
    private String merchantId;
    // 商户系统内部订单号
    private String outTradeNo;

}
