package com.niu.mall.service.admin.web.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 首页楼层参数
 */
@Data
public class MallWebFloorSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 楼层名称 */
    private String floorName;

}
