package com.niu.mall.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MallGoodsConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isVerifyOpen = 0;
}
