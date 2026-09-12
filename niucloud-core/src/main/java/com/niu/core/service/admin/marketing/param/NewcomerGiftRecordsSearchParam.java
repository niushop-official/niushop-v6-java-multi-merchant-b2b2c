package com.niu.core.service.admin.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 新人礼记录查询参数
 */
@Data
public class NewcomerGiftRecordsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keywords;

    private String[] createTime;
}
