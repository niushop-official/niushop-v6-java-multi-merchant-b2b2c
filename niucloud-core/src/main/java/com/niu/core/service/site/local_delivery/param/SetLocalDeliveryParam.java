package com.niu.core.service.site.local_delivery.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品参数参数
*/
@Data
public class SetLocalDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String feeType; // 费用类型
    private BigDecimal baseDist = BigDecimal.ZERO; // 多少km内
    private BigDecimal basePrice = BigDecimal.ZERO; // 配送费用
    private BigDecimal gradDist = BigDecimal.ZERO; // 每超出多少km内
    private BigDecimal gradPrice = BigDecimal.ZERO; // 配送费用
    private BigDecimal weightStart = BigDecimal.ZERO; // 重量多少内不额外收费
    private BigDecimal weightUnit = BigDecimal.ZERO; // 每超出多少kg
    private BigDecimal weightPrice = BigDecimal.ZERO; // 价格

    /** 配送时间设置 0 关闭 1 开启 */
    private Integer timeIsOpen = 0;

    /** 时间选取类型 0 每天  1 自定义 */
    private Integer timeType = 0;

    /** 营业时间  周一 周二....... */
    private JSONArray timeWeek;

    /** 时段设置单位分钟 */
    private Integer timeInterval = 30;

    /** 时段设置单位分钟 */
    private Integer advanceDay = 0;
    private Integer mostDay = 7;
    /** 当日的起始时间*/
    private Long startTime = 0L;

    /** 当日的营业结束时间 */
    private Long endTime = 0L;

    /** 配送时间段 */
    private JSONArray deliveryTime;

    private JSONObject areaData;
}
