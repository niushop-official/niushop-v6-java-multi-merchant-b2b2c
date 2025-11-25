package com.niu.mall.service.site.stat.vo;

import lombok.Data;

@Data
public class SuggestionVo {

    private String key;

    private String title;

    private String desc;

    private String buttonName;

    private String routerPath;

    private Integer isComplete;

}
