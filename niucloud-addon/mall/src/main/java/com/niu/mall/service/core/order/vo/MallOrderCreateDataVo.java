package com.niu.mall.service.core.order.vo;

import cn.hutool.json.JSONObject;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Data
public class MallOrderCreateDataVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected String orderKey;

    protected Basic basic = new Basic();

    protected Buyer buyer;

    protected Config config = new Config();

    protected Integer buyerId;

    protected Integer groupId;

    private String mainType;

    private Integer mainId;

    private Boolean isSupply;

    protected Integer isPlatCoupon = 1;

    protected OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();

    private Map<String, MallDiscountVo> discount = new HashMap<>();

    protected Map<String, OrderCreateDataVo> orderHandleList = new HashMap<>();
    protected Map<Integer, OrderCreateDataVo> orderList = new HashMap<>();

    protected List<String> error = new LinkedList<>();

    @Data
    public static class Basic {

        protected BigDecimal shopDiscountMoney = new BigDecimal(0);

        protected BigDecimal mallDiscountMoney = new BigDecimal(0);

        protected BigDecimal deliveryMoney = new BigDecimal(0);

        protected BigDecimal goodsMoney = new BigDecimal(0);

        protected BigDecimal costGoodsMoney = new BigDecimal(0);

        protected BigDecimal orderMoney = new BigDecimal(0);

        // -----------------开始----------------- 2026-05-11 （平台券维度）
        /** 平台优惠券金额（聚合） */
        protected BigDecimal mallCouponMoney = new BigDecimal(0);
        // -----------------结束-------------

        protected String body = "";

        protected Set<String> hasGoodsTypes = new HashSet<>();

        protected Set<String> hasSelectDeliveryTypes = new HashSet<>();

        protected Integer virtualVerifyMobile;
        protected Integer isNeedTakeAddress;

        protected Integer isCanPointDeduct;
        protected Integer preUsePoint = 0;
        protected BigDecimal prePointDeductMoney = new BigDecimal(0);
        protected BigDecimal pointDeductMoney = new BigDecimal(0);
        protected Integer usePoint = 0;
        protected BigDecimal fullReductionMoney = new BigDecimal(0);
        protected List<Integer> fullGiftSkuIds = new ArrayList<>();
        protected List<Map<String, Object>> giftGoodsList = new ArrayList<>();
        protected JSONObject extendData;
    }

    @Data
    public static class Buyer {
        protected String nickname;

        protected Integer point;

        protected MemberLevelInfoVo memberLevel;
    }


    @Data
    public static class MallDiscountVo {

        private List<Integer> matchGoodsIds;

        private String type;

        private Integer num;

        private BigDecimal money;

        private String discountType;

        private Integer discountTypeId;

        private String content;

        private String title;
    }

    @Data
    public static class Config {
        protected Delivery delivery = new Delivery();
        protected CloseOrder order = new CloseOrder();
    }

    @Data
    public static class Delivery {
        protected Integer isExpress;

        protected Integer isLocalDelivery;

        protected Integer isLocalDeliveryStoreSelect;

        protected Integer isShow;

        protected Integer isShowPolyline;

        protected Integer isStore;
    }

    @Data
    public static class CloseOrder {
        protected Integer isClose;
        protected Integer closeLength;
    }
}