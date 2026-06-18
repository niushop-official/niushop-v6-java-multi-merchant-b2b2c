package com.niu.mall.service.site.goods.param;

import lombok.Data;

@Data
public class MallGoodsImportParam {

    private Integer recordId;
    private Integer siteId;
    private Integer mainId;
    private String filePath;
    private String goodsType;
    private Integer totalNum;
    private Integer successNum;
    private Integer failNum;
    private Integer status;
    private String output;
    private String failOutput;
    private String failRemark;
    private Integer duration;
}
