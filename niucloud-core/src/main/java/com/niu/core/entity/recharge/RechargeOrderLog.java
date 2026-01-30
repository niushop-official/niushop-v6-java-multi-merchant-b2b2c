package com.niu.core.entity.recharge;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    private Integer orderId;

    /** 操作人类型 */
    private String mainType;

    /** 操作人id */
    private Integer mainId;

    /** 订单状态 */
    private Integer status;

    /** 类型 */
    private String type;

    /** 日志内容 */
    private String content;

    /** 操作时间 */
    private Long createTime;

}
