package com.niu.mall.service.core.order.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.service.core.order.param.InvoiceCreateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import io.swagger.models.auth.In;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Data
public class MallOrderCreateDataVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected String orderKey;

    protected Basic basic = new Basic();

    protected Buyer buyer;

    protected Integer buyerId;

    protected Integer groupId;

    private String mainType;

    private Integer mainId;

    private Boolean isSupply;

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

        protected String body = "";

        protected Set<String> hasGoodsTypes = new HashSet<>();

        protected Set<String> hasSelectDeliveryTypes = new HashSet<>();

        protected Integer virtualVerifyMobile;
    }

    @Data
    public static class Buyer {
        protected String nickname;

        protected Integer point;

        protected MemberLevelInfoVo memberLevel;
    }


    @Data
    public static class MallDiscountVo{

        private List<Integer> matchGoodsIds;

        private String type;

        private Integer num;

        private BigDecimal money;

        private String discountType;

        private Integer discountTypeId;

        private String content;

        private String title;
    }
}
