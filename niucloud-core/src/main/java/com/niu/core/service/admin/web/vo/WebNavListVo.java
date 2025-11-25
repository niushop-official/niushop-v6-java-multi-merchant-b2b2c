package com.niu.core.service.admin.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
* PC导航管理列表
*/
@Data
public class WebNavListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 主键 */
        private Integer id;

        /** 导航名称 */
        private String navTitle;

        /** 链接地址 */
        @JsonSerialize( using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String navUrl;

        /** 排序号 */
        private Integer sort;

        /** 是否新打开 */
        private Integer isBlank;



        /** 是否显示 1显示 0不显示 */
        private Integer isShow;
        /** 创建时间 */
        @JsonSerialize( using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;


        /** 修改时间 */
        @JsonSerialize( using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long updateTime;


}
