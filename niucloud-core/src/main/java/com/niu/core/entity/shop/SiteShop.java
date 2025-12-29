package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 店铺扩展表实体
*/
@Data
public class SiteShop implements Serializable {

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

    private Integer businessStatus;//    /** 营业状态（1营业中，2休息中） */

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

    /** 站点id */
    private Integer siteId;

    /** 商家上架商品数量 */
    private Integer mallUpNum;

    /** 商城商品销量 */
    private Integer mallSaleNum;

    /** 微信账号 */
    private String wechatAccountNo;

    /** 微信所属人姓名 */
    private String wechatName;

    /** 微信收款码图片 */
    private String wechatPaymentCode;

    /** 供应商手续费 */
    private BigDecimal supplyServiceRate;

    /** 供应商销售数量 */
    private Integer supplySaleNum;

    /** 供应商上架数量 */
    private Integer supplyUpNum;

    /** 供应商关注数 */
    private Integer supplyFollowNum;

    /** 供应商商品数量 */
    private Integer supplyGoodsNum;

    private BigDecimal qualityScore; // 商品质量
    private BigDecimal serviceScore; // 商家服务
    private BigDecimal logisticsScore; // 发货物流

    private Integer evaluateNum;
}