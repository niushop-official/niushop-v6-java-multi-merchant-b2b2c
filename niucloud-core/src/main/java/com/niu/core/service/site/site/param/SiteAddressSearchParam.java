package com.niu.core.service.site.site.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商家地址库参数
 */
@Data
public class SiteAddressSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile;

    private String fullAddress;

}
