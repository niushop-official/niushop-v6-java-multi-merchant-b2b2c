package com.niu.mall.service.admin.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
/**
* 首页楼层列表
*/
@Data
public class MallWebFloorListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 配置参数 */
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String config;

        /** 添加时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        /** 楼层名称 */
        private String floorName;

        /** 主键 */
        private Integer id;

        /** 排序号 */
        private Integer sort;
        private  Integer status;
        /** 配置模板 */
        private String  statusName;
        private String temp;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private  Long updateTime;



}
