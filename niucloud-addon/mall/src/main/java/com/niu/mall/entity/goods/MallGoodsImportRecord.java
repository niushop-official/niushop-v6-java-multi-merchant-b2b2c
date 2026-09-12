package com.niu.mall.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品导入记录总表
 */
@Data
public class MallGoodsImportRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    /** 导入记录ID */
    private Integer recordId;
    /** 站点ID */
    private Integer siteId;
    /** 操作人ID */
    private Integer mainId;
    /** 导入文件路径 */
    private String filePath;
    /** 商品类型 */
    private String goodsType;
    /** 总数 */
    private Integer totalNum;
    /** 成功总数 */
    private Integer successNum;
    /** 失败总数 */
    private Integer failNum;
    /** 处理状态 0待处理 1处理中 2完成 3失败 */
    private Integer status;
    /** 创建时间 */
    private Long createTime;
    /** 更新时间 */
    private Long updateTime;
    /** 对外输出记录 */
    @TableField("`output`")
    private String output;
    /** 失败输出记录 */
    private String failOutput;
    /** 失败原因 */
    private String failRemark;
    /** 耗时（秒） */
    private Integer duration;
}
