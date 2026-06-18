package com.niu.mall.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品导入记录行表
 */
@Data
public class MallGoodsImportRows implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    /** 行记录ID */
    private Integer id;
    /** 导入记录ID */
    private Integer recordId;
    /** 外部商品ID */
    private String goodsId;
    /** 外部SKU ID */
    private String skuId;
    /** 商品类型 */
    private String goodsType;
    /** 原始导入数据(JSON) */
    private String data;
    /** 处理状态 0待处理 1处理中 2成功 3失败 */
    private Integer status;
    /** 创建时间 */
    private Long createTime;
    /** 更新时间 */
    private Long updateTime;
    /** 关联商城goods_id */
    private Integer relateGoodsId;
    /** 关联商城sku_id */
    private Integer relateSkuId;
}
