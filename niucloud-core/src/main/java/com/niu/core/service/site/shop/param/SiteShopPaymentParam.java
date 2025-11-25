package com.niu.core.service.site.shop.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteShopPaymentParam implements Serializable {
    private static final long serialVersionUID = 1L;
   private int id;
   private String status;


}
