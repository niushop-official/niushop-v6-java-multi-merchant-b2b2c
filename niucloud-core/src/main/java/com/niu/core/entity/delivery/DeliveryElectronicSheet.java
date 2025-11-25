package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 电子面单实体
 */
@Data
public class DeliveryElectronicSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 模板名称 */
    private String templateName;

    /** 物流公司ID */
    private Integer expressCompanyId;

    /** 电子面单客户账号（CustomerName） */
    private String customerName;

    /** 电子面单密码（CustomerPwd） */
    private String customerPwd;

    /** SendSite */
    private String sendSite;

    /** SendStaff */
    private String sendStaff;

    /** MonthCode */
    private String monthCode;

    /** 邮费支付方式（1：现付，2：到付，3：月结） */
    private Integer payType;

    /** 快递员上门揽件（0：否，1：是） */
    private Integer isNotice;

    /** 状态（1：开启，0：关闭） */
    private Integer status;

    /** 物流公司业务类型 */
    private Integer expType;

    /** 电子面单打印模板样式 */
    private String printStyle;

    /** 是否默认（1：是，0：否） */
    private Integer isDefault;

    /** 快递公司类型 kdbird:快递鸟 kd100:快递100 */
    private String interfaceType;

    /** 物流公司业务类型名称 */
    private String expTypeName;

    /** 主模版:快递100用 */
    private String tempId;

    /** 子模版:快递100用 */
    private String childTempId;

    /** 回单模版:快递100用 */
    private String backTempId;

    /** 接口参数 */
    private String interfaceData;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 支持的交易类型 */
    private String supportTrade;
}