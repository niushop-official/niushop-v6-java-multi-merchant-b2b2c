package com.niu.core.entity.web;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
* PC导航管理实体
*/
@Data
public class WebNav implements Serializable {

private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 导航名称 */
    private String navTitle;

    /** 链接地址 */
    private String navUrl;

    /** 排序号 */
    private Integer sort;

    /** 是否新打开 */
    private Integer isBlank;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 是否显示 1显示 0不显示 */
    private Integer isShow;

}