package com.niu.core.service.api.shop.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 店铺扩展表实体
*/
@Data
public class SiteShopInShopVo implements Serializable {

private static final long serialVersionUID = 1L;

    /** 支付宝账号 */
    private String alipayAccountNo;

    /** 支付宝所属人姓名 */
    private String alipayName;

    /** 支付宝收款码图片 */
    private String alipayPaymentCode;

    /** 提现账号对应姓名 */
    private String bankAccountName;

    /** 提现银行账号 */
    private String bankAccountNo;

    /** 银行地址 */
    private String bankAddress;

    /** 银行名称 */
    private String bankName;

    /** 提现账户类型（1银行卡，2支付宝，3微信，4微信零钱） */
    private Integer bankType;

    /** 营业执照 */
    private String businessLicense;

    /** 商家分类id */
    private Integer categoryId;

    /** 佣金比率 */
    private BigDecimal commissionRate;

    /** 联系电话 */
    private String contactMobile;

    /** 店铺关注数 */
    private Integer followNumber;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 是否推荐 */
    private Integer isRecommend;

    /** 是否自营 */
    private Integer isSelf;

    /** 当前余额 */
    private BigDecimal money;

    /** 提现中 */
    private BigDecimal moneyCashOuting;

    /** 累计结算 */
    private BigDecimal moneyGet;

    /** 保证金 */
    private BigDecimal shopDeposit;

    /** 提现金额 */
    private BigDecimal withdrawalMoney;

    /** 站点id */
    private Integer siteId;

    /** 微信账号 */
    private String wechatAccountNo;

    /** 微信所属人姓名 */
    private String wechatName;

    /** 微信收款码图片 */
    private String wechatPaymentCode;

}