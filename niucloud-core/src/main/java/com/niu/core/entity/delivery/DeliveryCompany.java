package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 物流公司实体
 */
@Data
public class DeliveryCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value="company_id", type= IdType.AUTO)
    private Integer companyId;

    /** 站点ID */
    private Integer siteId;

    /** 物流公司名称 */
    private String companyName;

    /** 物流公司logo */
    private String logo;

    /** 物流公司网站 */
    private String url;

    /** 物流公司编号(用于物流跟踪) */
    private String expressNo;

    /** 物流公司编号(用于电子面单) */
    private String expressNoElectronicSheet;

    /** 是否支持电子面单（0：不支持，1：支持） */
    private Integer electronicSheetSwitch;

    /** 电子面单打印模板样式，json字符串 */
    private String printStyle;

    /** 物流公司业务类型，json字符串 */
    private String expType;

    /** 快递100:物流公司编号(用于物流跟踪) */
    private String kd100ExpressNo;

    /** 快递100:物流公司编号(用于电子面单) */
    private String kd100ExpressNoElectronicSheet;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime; 

}