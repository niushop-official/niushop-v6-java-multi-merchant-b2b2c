package com.niu.core.service.admin.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
* 友情链接列表
*/
@Data
public class WebFriendlyLinkListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 索引id */
        private Integer id;

        /** 标题 */
        private String linkTitle;

        /** 链接 */
        private String linkUrl;

        /** 图片 */
        private String linkPic;

        /** 排序号 */
        private Integer linkSort;
        private Integer isShow;

}
